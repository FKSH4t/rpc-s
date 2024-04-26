package com.whedc.registry;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 */
public class LocalRegistry {
    /**
     * 使用ConcurrentHashMap作为服务注册中心
     * key为服务名称
     * value为服务接口的实现类
     */
    private static final ConcurrentHashMap<String, Class<?>> serviceMap = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serviceName 服务名称
     * @param serviceImpl 服务接口实现类
     */
    public static void register(String serviceName, Class<?> serviceImpl) {
        serviceMap.put(serviceName, serviceImpl);
    }

    /**
     * 获取服务实例
     * @param serviceName 服务名
     * @return
     */
    public static Class<?> get(String serviceName) {
        return serviceMap.get(serviceName);
    }

    /**
     * 删除服务
     * @param serviceName 服务名
     */
    public static void remove(String serviceName) {
        serviceMap.remove(serviceName);
    }
}
