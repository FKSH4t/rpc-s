package com.whedc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

public class ConfigUtil {
    /**
     * 读取配置文件，加载配置对象
     * @param tClass 配置对象的类型
     * @param prefix 配置前缀
     * @param environment 配置环境
     * @return
     * @param <T> 泛型
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        // 前面都是拼接.properties配置文件的文件名，拼接好后直接从classpath下读取
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass, prefix);
    }

    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }
}
