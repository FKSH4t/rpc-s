package com.whedc.registry;

import cn.hutool.json.JSONUtil;
import com.whedc.config.RegistryConfig;
import com.whedc.model.ServiceMeteInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * etcd服务实现类
 * 先实现init方法，读取注册中心配置，并初始化etcd客户端对象
 */
public class EtcdRegistry implements Registry{
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
    }

    /**
     * 服务注册
     * 创建key并设置过期时间，value为服务元信息的json序列化后的字符串
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
    }

    @Override
    public void unRegister(ServiceMeteInfo serviceMeteInfo) {
        String unRegistryKey = ETCD_ROOT_PATH + serviceMeteInfo.getServiceNodeKey();
        System.out.println(unRegistryKey);
        try {
            DeleteResponse response = kvClient.delete(ByteSequence.from(unRegistryKey, StandardCharsets.UTF_8)).get();
            System.out.println("response.toString() = " + response.toString());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据服务名作为前缀，从etcd获取服务下的节点列表
     * @param serviceKey 服务键名
     * @return
     */
    @Override
    public List<ServiceMeteInfo> serviceDiscovery(String serviceKey) {
        // 前缀搜索，结尾要加'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        // 前缀搜索
        try {
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValueList = kvClient
                    .get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();
            return keyValueList.stream()
                    .map(keyValue -> {
                        String valueStr = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(valueStr, ServiceMeteInfo.class);
                    })
                    .collect(Collectors.toList());
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
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }
}
