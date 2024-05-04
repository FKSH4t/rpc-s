package com.whedc.registry;

import com.whedc.config.RegistryConfig;
import com.whedc.model.ServiceMeteInfo;

import java.util.List;

/**
 * 遵循可扩展式设计，类似序列化器一样的结构设计
 * 先定义一个Registry接口，后续可以实现多种注册中心
 * 同样采用SPI机制动态加载
 */
public interface Registry {
    /**
     * 初始化注册中心配置
     * @param registryConfig 配置类
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务(服务端)
     * @param serviceMeteInfo 服务元信息
     */
    void register(ServiceMeteInfo serviceMeteInfo) throws Exception;

    /**
     * 注销服务(服务端)
     * @param serviceMeteInfo
     */
    void unRegister(ServiceMeteInfo serviceMeteInfo);

    /**
     * 服务发现(获取某个服务列表，消费端)
     * @param serviceKey 服务键名
     * @return 某个服务的服务列表
     */
    List<ServiceMeteInfo> serviceDiscovery(String serviceKey);

    /**
     * 服务销毁
     */
    void destroy();

    /**
     * 心跳检测（服务端）
     */
    void heartBeat();

    /**
     * 监听，当消费者监听的某个key发生修改时
     * etcd会主动通知消费者
     */
    void watch(String serviceNodeKey);
}
