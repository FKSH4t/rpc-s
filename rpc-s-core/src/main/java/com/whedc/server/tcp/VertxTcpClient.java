package com.whedc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.whedc.RpcApplication;
import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 发送请求并接受响应
 * 使用装饰器封装客户端处理响的buffer
 */
public class VertxTcpClient {
    public static RpcResponse doRequest(RpcRequest rpcRequest, ServiceMeteInfo serviceMeteInfo) throws ExecutionException, InterruptedException {
        // 发送tcp请求
        Vertx vertx = Vertx.vertx();
        NetClient client = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        client.connect(serviceMeteInfo.getServicePort(), serviceMeteInfo.getServiceHost(),
                result -> {
                    if (result.succeeded()) {
                        System.out.println("Connected to tcp server!");
                        NetSocket socket = result.result();
                        // 构造消息并发送
                        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                        ProtocolMessage.Header header = new ProtocolMessage.Header();
                        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                        header.setSerializer((byte) ProtocolMessageSerializerEnum
                                .getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());

                        header.setRequestId(IdUtil.getSnowflakeNextId());
                        protocolMessage.setHeader(header);
                        protocolMessage.setBody(rpcRequest);
                        // 编码请求
                        try {
                            Buffer buffer = ProtocolMessageEncoder.encode(protocolMessage);
                            socket.write(buffer);
                        } catch (IOException e) {
                            throw new RuntimeException("协议消息编码错误:" + e.getMessage(), e);
                        }
                        // 接受响应
                        TcpBufferHandlerWrapper handlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                            try {
                                ProtocolMessage<RpcResponse> responseMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                responseFuture.complete(responseMessage.getBody());
                            } catch (IOException e) {
                                throw new RuntimeException("响应解码错误: " + e.getMessage(), e);
                            }
                        });
                        socket.handler(handlerWrapper);
                    } else {
                        System.out.println("Failed to connect to tcp server");
                    }
                });
        client.close();
        return responseFuture.get();
    }

}
