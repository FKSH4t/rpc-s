package com.whedc;

import com.whedc.registry.LocalRegistry;
import com.whedc.server.WebServer;
import com.whedc.server.VertxHttpServer;
import com.whedc.service.UserService;
import com.whedc.serviceImpl.UserServiceImpl;

public class SimpleProviderStartup {
    public static void main(String[] args) {
        RpcApplication.init();
        // 服务者启动时先注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动web服务器
        WebServer httpServer = new VertxHttpServer();
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
