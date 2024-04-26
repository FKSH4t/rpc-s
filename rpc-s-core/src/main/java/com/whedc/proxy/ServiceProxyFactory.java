package com.whedc.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂
 * 用于创建服务代理对象
 */
public class ServiceProxyFactory {

    public static <T> T getProxyInstance(Class<T> serviceClass) {
        Class[] classes = new Class[]{serviceClass};
        ServiceProxy serviceProxy = new ServiceProxy();
        ClassLoader classLoader = serviceClass.getClassLoader();
        return (T) Proxy.newProxyInstance(classLoader, classes, serviceProxy);
    }
}