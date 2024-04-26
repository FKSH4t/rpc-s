package com.whedc.server;

public interface HttpServer {
    /**
     * 启动http服务器
     * @param port 监听端口号
     */
    void startHttpServer(int port);
}
