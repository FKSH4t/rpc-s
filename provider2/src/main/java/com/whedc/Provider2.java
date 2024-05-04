package com.whedc;

import com.whedc.config.RegistryConfig;
import com.whedc.config.RpcConfig;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.registry.LocalRegistry;
import com.whedc.registry.Registry;
import com.whedc.registry.RegistryFactory;
import com.whedc.server.HttpServer;
import com.whedc.server.VertxHttpServer;
import com.whedc.service.ItemService;
import com.whedc.serviceImpl.ItemServiceImpl;

public class Provider2 {
    public static void main(String[] args) {
        RpcApplication.init();
        String serviceName = ItemService.class.getName();
        LocalRegistry.register(serviceName, ItemServiceImpl.class);

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
