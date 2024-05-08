package com.whedc.utils;

import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

public class ConfigUtil {
    /**
     * 读取配置文件，加载配置对象
     * 扩展支持多种类型的配置文件：yml、yaml、properties
     * @param tClass      配置对象的类型
     * @param prefix      配置前缀
     * @param environment 配置环境
     * @param <T>         泛型
     * @return
     */

    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        StringBuilder propsFileName = new StringBuilder(configFileBuilder.toString()).append(".properties");
        StringBuilder yamlFileName = new StringBuilder(configFileBuilder.toString()).append(".yaml");
        StringBuilder ymlFileName = new StringBuilder(configFileBuilder.toString()).append(".yml");
        // 前面都是拼接.properties配置文件的文件名，拼接好后直接从classpath下读取

        Props props = null;
        Properties properties = new Properties();
        try {
            // 读取YAML文件
            Yaml yaml = new Yaml();
            FileInputStream fis = new FileInputStream(yamlFileName.toString());
            Map<String, Object> yamlMap = yaml.load(fis);

            // 将YAML映射转换为Properties对象

            yamlMap.forEach((key, value) -> properties.setProperty(key, value.toString()));
            // 输出Properties对象
            properties.forEach((key, value) -> System.out.println(key + "=" + value));
            props = new Props(properties);
            return props.toBean(tClass, prefix);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return YamlUtil.loadByPath(ymlFileName.toString(), tClass);
        } catch (NoResourceException yamlEx) {
            try {
                return YamlUtil.loadByPath(yamlFileName.toString(), tClass);
            } catch (NoResourceException ymlEx) {
                props = new Props(propsFileName.toString());
                return props.toBean(tClass, prefix);
            }
        }
    }

    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }
}
