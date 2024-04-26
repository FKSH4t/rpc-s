package com.whedc;

import com.whedc.registry.LocalRegistry;
import com.whedc.server.HttpServer;
import com.whedc.server.VertxHttpServer;
import com.whedc.service.UserService;
import com.whedc.serviceImpl.UserServiceImpl;

public class ProviderExample {
    public static void main(String[] args) {
        // 初始化加载配置
        RpcApplication.init();

        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        // 启动web服务器
        HttpServer httpServer = new VertxHttpServer();
        httpServer.startHttpServer(RpcApplication.getRpcConfig().getServerPort());
    }
}
