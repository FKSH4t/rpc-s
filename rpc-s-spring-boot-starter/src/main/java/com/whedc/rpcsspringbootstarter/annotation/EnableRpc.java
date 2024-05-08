package com.whedc.rpcsspringbootstarter.annotation;

import com.whedc.rpcsspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.whedc.rpcsspringbootstarter.bootstrap.RpcInitBootstrap;
import com.whedc.rpcsspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 全局标识需要引入Rpc-s框架的模块
 * 由于服务提供者和消费者需要启动的模块不同
 * 需要通过传入参数指定是否需要启动web服务器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcConsumerBootstrap.class, RpcProviderBootstrap.class, RpcInitBootstrap.class})
public @interface EnableRpc {
    /**
     * 默认需要启动webServer
     * @return 默认启动
     */
    boolean needServer() default true;
}
