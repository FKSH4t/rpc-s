package com.whedc.server;

public interface WebServer {
    /**
     * 启动http服务器
     * @param port 监听端口号
     */
    void doStart(int port);
}
