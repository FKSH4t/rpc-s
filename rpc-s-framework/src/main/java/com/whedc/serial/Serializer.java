package com.whedc.serial;

import java.io.IOException;

/**
 * 序列化接口
 * 提供序列化和反序列化两个方法
 */
public interface Serializer {
    /**
     * 序列化
     * @param object 待序列化对象
     * @return 序列化byte数组
     * @param <T> 泛型
     * @throws IOException IO异常
     */
    <T> byte[] serialize(T object) throws IOException;

    /**
     * 反序列化
     * @param bytes 序列化后的byte数组
     * @param type 反序列化的对象类型
     * @return 反序列化的对象
     * @param <T> 泛型
     * @throws IOException IO异常
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;
}
