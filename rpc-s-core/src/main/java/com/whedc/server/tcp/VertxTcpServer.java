package com.whedc.server.tcp;


import com.whedc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest(byte[] requestData) {
        System.out.println(new String(requestData));
        // 响应处理demo
        return "Hello, tcp".getBytes();
    }
    @Override
    public void startHttpServer(int port) {
        // 创建vertx实例
        Vertx vertx = Vertx.vertx();

        // 创建Tcp服务器
        NetServer server = vertx.createNetServer();

        // 处理请求
        server.connectHandler(new TcpServerHandler());
//        server.connectHandler(socket -> {
//            // 处理连接
//            socket.handler(buffer -> {
//                // 处理接收到的字节数组
//                byte[] requestData = buffer.getBytes();
//                // 在这里进行字节数组的处理逻辑
//                // 例如进行相关请求的解析
//                byte[] responseData = handleRequest(requestData);
//                socket.write(Buffer.buffer(responseData));
//            });
//        });

        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP server started on port: " + port);
            } else {
                System.err.println("Failed to start server: " + result.cause());
            }
        });
    }

}
