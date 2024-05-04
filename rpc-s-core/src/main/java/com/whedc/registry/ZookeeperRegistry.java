package com.whedc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.whedc.config.RegistryConfig;
import com.whedc.model.ServiceMeteInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class ZookeeperRegistry implements Registry {
    private CuratorFramework client;
    private ServiceDiscovery<ServiceMeteInfo> serviceDiscovery;
    /**
     * 提供者节点本地注册的节点集合
     * 用于服务续期
     */
    private final Set<String> localRegisterNodeSet = new ConcurrentHashSet<>();
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    /**
     * 正在监听的key集合
     */
    private final Set<String> watchingKeys = new ConcurrentHashSet<>();
    private static final String ZK_ROOT_PATH = "/rpc/zk";

    @Override
    public void init(RegistryConfig registryConfig) {
        // 构建client实例
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();
        // 构建serviceDiscovery实例
        serviceDiscovery = ServiceDiscoveryBuilder
                .builder(ServiceMeteInfo.class)
                .client(client)
                .basePath(ZK_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMeteInfo.class))
                .build();
        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceMeteInfo serviceMeteInfo) throws Exception {
        // 注册服务到zk
        serviceDiscovery.registerService(buildServiceInstance(serviceMeteInfo));

        // 添加节点信息到本地缓存
        String registryKey = ZK_ROOT_PATH + "/" + serviceMeteInfo.getServiceNodeKey();
        localRegisterNodeSet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMeteInfo serviceMeteInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serviceMeteInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 从本地缓存移除
        String registryKey = ZK_ROOT_PATH + "/" + serviceMeteInfo.getServiceNodeKey();
        localRegisterNodeSet.remove(registryKey);
    }

    @Override
    public List<ServiceMeteInfo> serviceDiscovery(String serviceKey) {
        // 先从缓存获取
        // 首先搜索节点的本地缓存，如果缓存没有再使用kvClient搜索
        List<ServiceMeteInfo> cachedServiceList = registryServiceCache.readCache();
        List<ServiceMeteInfo> serv = RegistryServiceCache.getServiceMeteInfos(serviceKey, cachedServiceList);
        if (serv != null) return serv;

        try {
            // 查询服务信息
            Collection<ServiceInstance<ServiceMeteInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceKey);

            // 解析
            List<ServiceMeteInfo> serviceMetaInfoList = serviceInstances.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败", e);
        }
    }


    @Override
    public void destroy() {
        log.info("当前节点下线");
        for (String node : localRegisterNodeSet) {
            try {
                client.delete().guaranteed().forPath(node);
            } catch (Exception e) {
                throw new RuntimeException("节点下线失败", e);
            }
        }

        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // zk不需要手动实现心跳机制
    }

    @Override
    public void watch(String serviceNodeKey) {
        String watchKey = ZK_ROOT_PATH + "/" + serviceNodeKey;
        boolean newWatch = watchingKeys.add(watchKey);
        if (newWatch) {
            CuratorCache curatorCache = CuratorCache.build(client, watchKey);
            curatorCache.start();
            curatorCache.listenable().addListener(
                    CuratorCacheListener
                            .builder()
                            .forDeletes(childData -> registryServiceCache.clearCache())
                            .forChanges(((oldNode, node) -> registryServiceCache.clearCache()))
                            .build());
        }
    }

    private ServiceInstance<ServiceMeteInfo> buildServiceInstance(ServiceMeteInfo serviceMeteInfo) {
        String serviceAddress = serviceMeteInfo.getServiceHost() + ":" + serviceMeteInfo.getServicePort();
        try {
            return ServiceInstance
                    .<ServiceMeteInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMeteInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMeteInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
