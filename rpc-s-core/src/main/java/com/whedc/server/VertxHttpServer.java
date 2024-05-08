package com.whedc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements WebServer {
    /**
     * 启动vertx服务器
     *
     * @param port 监听端口号
     */
    @Override
    public void doStart(int port) {
        // 创建vertx实例对象
        Vertx vertx = Vertx.vertx();

        // 获取vertx http server
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        // 配置requestHandler处理请求
        httpServer.requestHandler(new HttpRequetHandler());


        // 配置server监听端口并启动
        httpServer.listen(port, handler -> {
            if (handler.succeeded()) {
                System.out.println("Server is now listening on port: " + port);
            } else {
                System.out.println("Failed to start server because: " + handler.cause());
            }
        });

    }
}
