package com.whedc;

import com.whedc.config.RegistryConfig;
import com.whedc.config.RpcConfig;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.registry.LocalRegistry;
import com.whedc.registry.Registry;
import com.whedc.registry.RegistryFactory;
import com.whedc.server.HttpServer;
import com.whedc.server.VertxHttpServer;
import com.whedc.service.UserService;
import com.whedc.serviceImpl.UserServiceImpl;

public class ProviderExample {
    public static void main(String[] args) {
        // 初始化加载配置
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
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
        // 启动web服务器
        HttpServer httpServer = new VertxHttpServer();
        httpServer.startHttpServer(RpcApplication.getRpcConfig().getServerPort());
    }
}
