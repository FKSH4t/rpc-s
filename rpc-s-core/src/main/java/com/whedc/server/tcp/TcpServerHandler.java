package com.whedc.server.tcp;

import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;
import com.whedc.protocol.ProtocolMessage;
import com.whedc.protocol.ProtocolMessageDecoder;
import com.whedc.protocol.ProtocolMessageEncoder;
import com.whedc.protocol.ProtocolMessageTypeEnum;
import com.whedc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 请求处理器
 * 接受请求并解析内容
 * 利用反射调用服务实现类
 */
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {
        TcpBufferHandlerWrapper tcpBufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            // 接受请求并解码
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException("协议消息解码错误: ", e);
            }
            RpcRequest rpcRequest = protocolMessage.getBody();

            // 处理请求
            // 构造相应对象
            RpcResponse rpcResponse = new RpcResponse();
            try{
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.getDeclaredConstructor().newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setResult(result);
                rpcResponse.setResultType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                     InstantiationException e) {
                e.printStackTrace();
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
            }

            // 发送响应
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header, rpcResponse);
            try {
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                netSocket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("响应编码错误: ", e);
            }
        });
        netSocket.handler(tcpBufferHandlerWrapper);
    }
}
