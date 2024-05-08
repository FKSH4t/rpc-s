package com.whedc.rpcsautoconfig.configuraiton;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("rpc.registry")
public class RpcRegistryConfiguration {
    /**
     * 注册中心类别
     */
    private String registry = "etcd";
    /**
     * 注册中心服务地址
     */
    private String address = "http://localhost:2379";
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 服务宕机超时时间
     */
    private Long timeout = 10000L;
}
