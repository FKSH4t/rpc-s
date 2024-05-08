package com.whedc.bootstrap;

import com.whedc.RpcApplication;
import com.whedc.config.RegistryConfig;
import com.whedc.config.RpcConfig;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.registry.LocalRegistry;
import com.whedc.registry.Registry;
import com.whedc.registry.RegistryFactory;
import com.whedc.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者启动类
 * 提供者需要额外启动web服务器
 */
public class ProviderBootstrap {
    /**
     * 初始化
     * 注册相关的服务
     * @param serviceRegisterInfoList
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList) {
        // 初始化加载配置
        RpcApplication.init();
        // 获取全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 注册服务
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            String serviceName = serviceRegisterInfo.getServiceName();
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());
            // 注册到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMeteInfo serviceMeteInfo = new ServiceMeteInfo();
            serviceMeteInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMeteInfo.setServicePort(rpcConfig.getServerPort());
            serviceMeteInfo.setServiceName(serviceName);
            try {
                registry.register(serviceMeteInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        VertxTcpServer server = new VertxTcpServer();
        server.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
