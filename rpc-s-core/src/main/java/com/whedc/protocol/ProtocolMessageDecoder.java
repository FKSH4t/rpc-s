package com.whedc.protocol;

import com.whedc.model.RpcRequest;
import com.whedc.model.RpcResponse;
import com.whedc.serial.Serializer;
import com.whedc.serial.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 解码器
 */
public class ProtocolMessageDecoder {

    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        // 获取请求魔数
        byte magic = buffer.getByte(0);
        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            throw new RuntimeException("不支持的请求协议");
        }
        // 根据协议的结构，分别从指定的位置读出相应的数据
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setLength(buffer.getInt(13));
        // 解决粘包问题，每次只读指定长度的字节数组
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getLength());
        // 获取请求头中指定的序列化器，反序列化消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null) {
            throw new RuntimeException("请求指定的序列化器不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (typeEnum == null) {
            throw new RuntimeException("请求指定的消息类型不存在");
        }
        return switch (typeEnum) {
            case REQUEST -> {
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                yield new ProtocolMessage<>(header, rpcRequest);
            }
            case RESPONSE -> {
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                yield new ProtocolMessage<>(header, rpcResponse);
            }
            default -> throw new RuntimeException("不支持的消息类型");
        };
    }
}
