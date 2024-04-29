package com.whedc.config;

import lombok.Data;

/**
 * 注册中心配置类
 * 用户在连接注册中心时需要配置相关的信息，如注册中心的类别、连接地址等
 */
@Data
public class RegistryConfig {
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
