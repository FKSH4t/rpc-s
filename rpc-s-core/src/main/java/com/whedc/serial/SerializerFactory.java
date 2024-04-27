package com.whedc.serial;

import com.whedc.utils.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化器工厂
 * 序列化器的实例对象是可以复用的，不需要每次调用时都创建一个新的实例对象
 * 因此选择工厂模式 + 单例模式来创建和获取序列化器对象
 */
public class SerializerFactory {

    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 序列化器映射，用于实现单例
     * 匿名内部类初始化HashMap
     */
//    private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>() {
//        {
//            put(SerializerKeys.JDK, new JdkSerializer());
//            put(SerializerKeys.JSON, new JsonSerializer());
//            put(SerializerKeys.HESSIAN, new HessianSerializer());
//            put(SerializerKeys.KRYO, new KryoSerializer());
//        }
//    };

    /**
     * 初始默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 如果不存在则返回默认的序列化器
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
