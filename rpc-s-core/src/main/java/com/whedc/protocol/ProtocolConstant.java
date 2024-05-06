package com.whedc.protocol;

/**
 * 协议相关常量
 */
public interface ProtocolConstant {
    /**
     * 消息头长度
     */
    int MESSAGE_HEADER_LENGTH = 17;
    /**
     * 魔数
     */
    byte PROTOCOL_MAGIC = 0x1;
    /**
     * 协议版本号
     */
    byte PROTOCOL_VERSION = 0x1;
}
