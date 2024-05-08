package com.whedc.rpcsspringbootstarter.bootstrap;

import com.whedc.RpcApplication;
import com.whedc.config.RegistryConfig;
import com.whedc.config.RpcConfig;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.registry.LocalRegistry;
import com.whedc.registry.Registry;
import com.whedc.registry.RegistryFactory;
import com.whedc.rpcsspringbootstarter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 通过实现BeanPostProcessor的方法
 * 实现在bean初始化之后扫描相关的注解
 * 获取对应的bean对象以及它们的相关属性
 * 完成服务的注册
 */
@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        // 如果rpcService不为空则找到了标注了注解的bean
        if (rpcService != null) {
            // 获取接口实现类
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 处理默认值，默认拿第一个实现类
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            // 本地注册
            LocalRegistry.register(serviceName, beanClass);
            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMeteInfo serviceMeteInfo = new ServiceMeteInfo();
            serviceMeteInfo.setServiceName(serviceName);
            serviceMeteInfo.setServiceVersion(serviceVersion);
            serviceMeteInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMeteInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMeteInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败", e);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
