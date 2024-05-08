package com.whedc.rpcsspringbootstarter.bootstrap;

import com.whedc.proxy.ServiceProxyFactory;
import com.whedc.rpcsspringbootstarter.annotation.RpcReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * Rpc消费者启动类
 * 与提供者类似，实现BeanPostProcessor来获取bean
 * 判断bean对象是否包含RpcReference注解标注的属性
 * 如果包含就说明是一个消费者，则为它生成代理对象
 */
@Slf4j
public class RpcConsumerBootstrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field : fields) {
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 为该属性生成代理对象
                Class<?> interfaceClass = rpcReference.interfaceClass();
                // 处理默认值
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                field.setAccessible(true);
                Object proxyInstance = ServiceProxyFactory.getProxyInstance(interfaceClass);
                try {
                    field.set(bean, proxyInstance);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("代理对象注入失败: ",e);
                }
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
