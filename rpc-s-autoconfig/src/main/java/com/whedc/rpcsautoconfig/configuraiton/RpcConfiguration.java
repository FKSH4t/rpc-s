package com.whedc.rpcsautoconfig.configuraiton;

import com.whedc.config.RegistryConfig;
import com.whedc.fault.retry.RetryStrategyKeys;
import com.whedc.fault.tolerant.TolerantStrategyKeys;
import com.whedc.loadBalancer.LoadBalancerKeys;
import com.whedc.serial.SerializerKeys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "rpc.server")
public class RpcConfiguration {
    /**
     * 是否开启模拟调用，默认不开启
     */
    private boolean mock = false;
    /**
     * 名称
     */
    private String name = "rpc-s";
    /**
     * 版本
     */
    private String version = "1.0";
    /**
     * 服务地址
     */
    private String serverHost = "localhost";
    /**
     * 服务端口号
     */
    private Integer serverPort = 8080;
    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;
    /**
     * 默认注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;
    /**
     * 容错策略
     */
    private String tolerantStrategy = TolerantStrategyKeys.FAIL_FAST;}
