package com.whedc.proxy;

import com.whedc.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 * 用于创建服务代理对象
 */
public class ServiceProxyFactory {

    public static <T> T getProxyInstance(Class<T> serviceClass) {
        if (RpcApplication.getRpcConfig().isMock()) {
            return getMockProxy(serviceClass);
        } else {
            Class[] classes = new Class[]{serviceClass};
            ServiceProxy serviceProxy = new ServiceProxy();
            ClassLoader classLoader = serviceClass.getClassLoader();
            return (T) Proxy.newProxyInstance(classLoader, classes, serviceProxy);
        }
    }


    /**
     * 根据服务类获取mock代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockProxy());
    }
}