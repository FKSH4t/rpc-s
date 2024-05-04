package com.whedc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.whedc.config.RegistryConfig;
import com.whedc.model.ServiceMeteInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * etcd服务实现类
 * 先实现init方法，读取注册中心配置，并初始化etcd客户端对象
 */
public class EtcdRegistry implements Registry {
    /**
     * 正在监听的key集合
     */
    private final Set<String> watchingKeys = new ConcurrentHashSet<>();
    /**
     * 本机注册的节点key集合
     */
    private final Set<String> localRegisterNodeSet = new HashSet<>();
    private final RegistryServiceCache serviceListCache = new RegistryServiceCache();
    private Client client;
    private KV kvClient;
    /**
     * 根路径
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    /**
     * 服务注册
     * 创建key并设置过期时间，value为服务元信息的json序列化后的字符串
     *
     * @param serviceMeteInfo 服务元信息
     */
    @Override
    public void register(ServiceMeteInfo serviceMeteInfo) throws Exception {
        // leaseClient用于管理etcd的租约机制
        // 租约是etcd中的一种时间片，用于为键值对分配生存时间
        Lease leaseClient = client.getLeaseClient();

        // 创建一个30s的租约
        long leaseId = leaseClient.grant(30).get().getID();

        String registryKey = ETCD_ROOT_PATH + serviceMeteInfo.getServiceNodeKey();
        // 获取kv键值对
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMeteInfo), StandardCharsets.UTF_8);

        // 创建一个PutOption，用于将键值对和租约关联起来
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        localRegisterNodeSet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMeteInfo serviceMeteInfo) {
        String unRegistryKey = ETCD_ROOT_PATH + serviceMeteInfo.getServiceNodeKey();
        System.out.println(unRegistryKey);
        try {
            DeleteResponse response = kvClient.delete(ByteSequence.from(unRegistryKey, StandardCharsets.UTF_8)).get();
            localRegisterNodeSet.remove(unRegistryKey);
            System.out.println("response.toString() = " + response.toString());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据服务名作为前缀，从etcd获取服务下的节点列表
     *
     * @param serviceKey 服务键名
     * @return
     */
    @Override
    public List<ServiceMeteInfo> serviceDiscovery(String serviceKey) {
        // 首先搜索节点的本地缓存，如果缓存没有再使用kvClient搜索
        List<ServiceMeteInfo> cachedServiceList = serviceListCache.readCache();
        List<ServiceMeteInfo> serv = RegistryServiceCache.getServiceMeteInfos(serviceKey, cachedServiceList);
        if (serv != null) return serv;

        // 前缀搜索，结尾要加'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        // 前缀搜索
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValueList = kvClient
                    .get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();
            List<ServiceMeteInfo> serviceList = keyValueList.stream()
                    .map(keyValue -> {
                        String keyStr = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        watch(keyStr);
                        String valueStr = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(valueStr, ServiceMeteInfo.class);
                    })
                    .collect(Collectors.toList());
            serviceListCache.writeCache(serviceList);
            return serviceList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get service lists", e);
        }
    }



    /**
     * 注册中心的销毁
     */
    @Override
    public void destroy() {
        System.out.println("Current node is going to offline");
        // 当节点自身出现问题时
        // 被动下线当前节点所有的服务
        for (String key : localRegisterNodeSet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Failed to offline current node: " + key, e);
            }
        }
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    /**
     * 使用Hutool工具包的CronUtil实现定时任务，
     * 对所有localRegisterNodeSet集合中的节点执行重新注册，相当于续约
     */
    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                for (String key : localRegisterNodeSet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 判断如果keyValues为空，则说明该节点已经过期，需要重启服务后才能重新注册
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，则续约
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMeteInfo serviceMeteInfo = JSONUtil.toBean(value, ServiceMeteInfo.class);
                        register(serviceMeteInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + ": 续约失败", e);
                    }
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean newWatch = watchingKeys.add(serviceNodeKey);
        // 判断是否添加成功，添加成功则说明之前没有被监听
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8),
                    watchResponse -> {
                        for (WatchEvent event : watchResponse.getEvents()) {
                            switch (event.getEventType()) {
                                case DELETE:
                                    serviceListCache.clearCache();
                                    break;
                                case PUT:
                                default:
                                    break;
                            }
                        }
                    });
        }
    }
}
