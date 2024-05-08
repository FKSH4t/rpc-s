package com.whedc.rpcsspringbootstarter.annotation;

import com.whedc.constant.RpcConstant;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供者注解
 * 需要指定服务注册信息
 * 如：服务接口实现类、版本号
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    /**
     * 默认为一个空的Class
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 默认版本
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
}
