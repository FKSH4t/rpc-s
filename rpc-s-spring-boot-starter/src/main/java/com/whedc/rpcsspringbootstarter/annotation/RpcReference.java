package com.whedc.rpcsspringbootstarter.annotation;

import com.whedc.constant.RpcConstant;
import com.whedc.fault.retry.RetryStrategyKeys;
import com.whedc.fault.tolerant.TolerantStrategyKeys;
import com.whedc.loadBalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者
 * 需要指定调用服务的相关属性
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    /**
     * 服务接口类
     * @return
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 默认版本
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 负载均衡器
     * @return 默认负载均衡器轮询
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;

    /**
     * 重试策略
     * @return 默认不开启重试
     */
    String retryStrategy() default RetryStrategyKeys.NO;

    /**
     * 容错策略
     * @return 默认快速失败
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_FAST;

    /**
     * 模拟调用
     * @return 默认不开启
     */
    boolean mock() default false;
}
