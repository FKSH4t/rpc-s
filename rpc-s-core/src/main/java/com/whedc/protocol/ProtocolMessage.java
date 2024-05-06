package com.whedc.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体：请求或响应对象
     */
    private T body;
    /**
     * 自定义协议头
     */
    @Data
    public static class Header {
        /**
         * 魔数，保证服务器不会处理框架之外的消息
         * 保证安全性
         */
        private byte magic;
        /**
         * 版本号
         */
        private byte version;
        /**
         * 序列化器
         */
        private byte serializer;
        /**
         * 消息类型：请求/响应
         */
        private byte type;
        /**
         * 消息状态
         */
        private byte status;
        /**
         * 消息id，唯一标识某一条消息
         */
        private long RequestId;
        /**
         * 消息体长度
         */
        private int length;
    }
}
