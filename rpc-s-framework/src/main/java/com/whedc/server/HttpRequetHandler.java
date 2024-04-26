package com.whedc.server;

import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;
import com.whedc.registry.LocalRegistry;
import com.whedc.serial.JdkSerializer;
import com.whedc.serial.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Http请求处理
 * 业务流程：
 * 反序列化请求生成对象，并从对象中获取参数
 * 根据获取参数中的服务名从注册中心中获取相应服务的具体实现类
 * 通过反射调用具体的方法，得到返回结果
 * 对结果进行序列化封装，写入响应
 */
public class HttpRequetHandler implements Handler<HttpServerRequest> {
    // 指定序列化器
    private final Serializer serializer = new JdkSerializer();

    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        // 记录日志
        System.out.println("Receive request: " + httpServerRequest.method() + " " + httpServerRequest.uri());

        // 异步处理Http请求
        httpServerRequest.handler(req -> {
            byte[] reqBytes = req.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(reqBytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 构造响应RpcResponse
            RpcResponse rpcResponse = new RpcResponse();

            // 如果rpcRequest为空，直接返回null
            if (rpcRequest == null) {
                rpcResponse.setMessage("RpcRequest is null!");
                doResponse(httpServerRequest, rpcResponse, serializer);
                return;
            }

            // 不为空，使用反射调用相应的方法
            try {
                // 获取服务的实现类
                Class<?> ServiceClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = ServiceClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(ServiceClass.getConstructor().newInstance(), rpcRequest.getArgs());
                rpcResponse.setResult(result);
                rpcResponse.setResultType(method.getReturnType());
                rpcResponse.setMessage("200, OK");
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
            }
            doResponse(httpServerRequest, rpcResponse, serializer);
        });
    }

    /**
     * 发送响应
     * @param request 请求
     * @param rpcResponse 封装好的rpc响应
     * @param serializer 序列化器
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse response = request.response().putHeader("content-type", "application/json");
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            response.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            response.end(Buffer.buffer());
        }
    }

}
