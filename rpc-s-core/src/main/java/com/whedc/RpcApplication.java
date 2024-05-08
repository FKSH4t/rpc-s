package com.whedc;

import com.whedc.config.RegistryConfig;
import com.whedc.config.RpcConfig;
import com.whedc.constant.RpcConstant;
import com.whedc.registry.Registry;
import com.whedc.registry.RegistryFactory;
import com.whedc.utils.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 维护了一个Rpc框架中的全局配置对象，在引入rpc框架的项目启动时
 * 从配置文件中读取配置并且创建单例的配置对象实例
 * 之后每次需要读取配置项时集中的从这个对象中获取
 * 而不需要每次都重新读取然后创建对象节约系统开销
 * 相当于配置对象的Holder，采用双重检查锁实现单例
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * Rpc初始化，支持传入自定义的配置
     * @param newRpcConfig 传入的自定义配置
     */
    public static void init(RpcConfig newRpcConfig)
    {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());

        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init, config = {}", registryConfig);

        // 创建并注册Shutdown hook，jvm退出时自动执行相关操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 初始化加载配置
     */
    public static void init() {
        RpcConfig newRpcConfig;
        RegistryConfig registryConfig;
        try {
            newRpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
            registryConfig = ConfigUtil.loadConfig(RegistryConfig.class, RpcConstant.DEFAULT_REGISTRY_PREFIX);
            newRpcConfig.setRegistryConfig(registryConfig);
        } catch (Exception e) {
            // 自定义配置加载失败，使用默认配置
            log.info("Custom config load failed, using default config");
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置，采用双重检查获取单例配置对象
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
