package com.whedc.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import com.whedc.serial.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器
 * 相当于一个工具类，用于读取配置并加载配置中的实现类
 */
@Slf4j
public class SpiLoader {
    /**
     * 关键实现：
     * 1、使用Map来存储映射：key -> 实现类
     * 2、读取指定路径下的配置文件，获取到key和实现类的映射信息存入Map
     * 3、定义获取实例的方法，根据传入的接口和key，从Map中找到对应实现类的全限定名，
     *    然后通过反射获取实现类的实例对象。
     *    可以维护一个缓存，创建过一次的对象从该缓存中获取无需二次创建
     */

    /**
     * 存储已加载的类：接口名 -> (key -> 实现类)
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    /**
     * 对象实例缓存，避免重复创建实例
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    /**
     * 系统rpc spi配置路径
     */
    private static final String SPI_RPC_SYSTEM_PATH = "META-INF/rpc/system/";
    /**
     * 用户自定义rpc spi配置路径
     */
    private static final String SPI_RPC_CUSTOM_PATH = "META-INF/rpc/custom/";
    /**
     * 需要扫描的全部路径
     */
    private static final String[] SCAN_DIRS = new String[] {SPI_RPC_SYSTEM_PATH, SPI_RPC_CUSTOM_PATH};
    /**
     * 需要动态加载全部的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);

    /**
     * 加载全部的spi类型
     */
    public static void loadAll() {
        log.info("Loading all spi configurations");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }

    }

    /**
     * 加载某一个类型
     * @param loadClass 待加载的类型
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("Starting load Class {} SPI", loadClass.getName());
        Map<String, Class<?>> keyMapClass = new HashMap<>();
        for (String dir : SCAN_DIRS) {
            // 获取每一个配置路径下的配置文件全限定名
            List<URL> resources = ResourceUtil.getResources(dir + loadClass.getName());
            // 读取每一个资源
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length > 1) {
                            String key = split[0];
                            String className = split[1];
                            keyMapClass.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e) {
                    log.error("spi resource load error: {}", e.getMessage());
                }
            }
        }
        loaderMap.put(loadClass.getName(), keyMapClass);
        return keyMapClass;
    }

    /**
     * 根据key获取某个接口的实现类实例
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> classMap = loaderMap.get(tClassName);
        if (classMap == null) {
            throw new RuntimeException(String.format("SpiLoader unloaded SPI Class: %s", tClassName));
        }
        if (!classMap.containsKey(key)) {
            throw new RuntimeException(String.format("No exist Class where key = %s", key));
        }
        Class<?> implClass = classMap.get(key);
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.getConstructor().newInstance());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                String errorMsg = String.format("%s class instance create failed", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }
}
