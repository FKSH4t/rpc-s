package com.whedc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.whedc.RpcApplication;
import com.whedc.config.RpcConfig;
import com.whedc.constant.RpcConstant;
import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;
import com.whedc.model.ServiceMeteInfo;
import com.whedc.registry.Registry;
import com.whedc.registry.RegistryFactory;
import com.whedc.serial.JdkSerializer;
import com.whedc.serial.Serializer;
import com.whedc.serial.SerializerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

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

        String serviceName = method.getDeclaringClass().getName();
        // 构造rpc请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] serialized = serializer.serialize(rpcRequest);

            // 从注册中心获取服务提供者主机地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMeteInfo serviceMeteInfo = new ServiceMeteInfo();
            serviceMeteInfo.setServiceName(serviceName);
            serviceMeteInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMeteInfo> serviceList = registry.serviceDiscovery(serviceMeteInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceList)) {
                throw new RuntimeException("No service address");
            }
            // 默认获取列表第一个服务地址
            ServiceMeteInfo meteInfo = serviceList.get(0);
            System.out.println(method + ":" +meteInfo.getServiceAddress());

            try (HttpResponse httpResponse = HttpRequest.post(
                         meteInfo.getServiceAddress())
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
