package com.whedc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.whedc.RpcApplication;
import com.whedc.config.RpcConfig;
import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;
import com.whedc.serial.JdkSerializer;
import com.whedc.serial.Serializer;
import com.whedc.serial.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Jdk动态服务代理
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * 自定义调用代理方法的具体过程
     *
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造rpc请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] serialized = serializer.serialize(rpcRequest);
            try (HttpResponse httpResponse = HttpRequest.post(
                    RpcApplication.getRpcConfig().getServerHost()
                            + ":"
                            + RpcApplication.getRpcConfig().getServerPort())
                         .body(serialized)
                         .execute()) {

                RpcResponse rpcResponse = serializer.deserialize(httpResponse.bodyBytes(), RpcResponse.class);
                return rpcResponse.getResult();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
