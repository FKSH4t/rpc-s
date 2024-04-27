package com.whedc.serial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;

import java.io.IOException;

/**
 * Json序列化器
 */

public class JsonSerializer implements Serializer{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T value = OBJECT_MAPPER.readValue(bytes, type);
        if (value instanceof RpcRequest) {
            return handleRequest((RpcRequest) value, type);
        }
        if (value instanceof RpcResponse) {
            return handleResponse((RpcResponse) value, type);
        }
        return value;
    }

    /**
     * 由于反序列化时，Object原始对象类型会被擦除，
     * 需要在反序列化时进行额外判断和处理，
     * 将其转换成原始的正确类型
     * @return
     * @param <T> 泛型
     * @param rpcRequest 请求
     * @param type 原始类型
     */
    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException {
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            if (clazz.isAssignableFrom(args[i].getClass())) {
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }
        return type.cast(rpcRequest);
    }

    /**
     * 同理需要在客户端处理响应时进行相同的处理
     * 对响应结果的类型进行处理
     * @param rpcResponse 响应
     * @param type 响应结果类型
     * @return
     * @param <T> 泛型，正确的原始类型
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type) throws IOException {
        byte[] resBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getResult());
        rpcResponse.setResult(OBJECT_MAPPER.readValue(resBytes, rpcResponse.getResultType()));
        return type.cast(rpcResponse);
    }
}
