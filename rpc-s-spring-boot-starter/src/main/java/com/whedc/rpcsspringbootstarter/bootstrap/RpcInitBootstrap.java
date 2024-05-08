package com.whedc.rpcsspringbootstarter.bootstrap;

import com.whedc.RpcApplication;
import com.whedc.config.RpcConfig;
import com.whedc.rpcsspringbootstarter.annotation.EnableRpc;
import com.whedc.server.WebServer;
import com.whedc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 通过实现ImportBeanDefinitionRegistrar接口
 * 解析相应注解传入的参数
 */
@Slf4j
@Component
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    // 获取全局配置
    @Resource
    private RpcConfig rpcConfig;

    /**
     * Spring容器初始化时执行，初始化Rpc框架
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取EnableRpc注解的属性值
        // 判断是否needServer来判断是provider还是consumer
        boolean needServer = (boolean) importingClassMetadata
                .getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        log.info("rpcConfig is: {}", rpcConfig);

        // Rpc通用框架初始化
        RpcApplication.init(rpcConfig);

        if (needServer) {
            WebServer server = new VertxTcpServer();
            server.doStart(rpcConfig.getServerPort());
        } else {
            log.info("needServer is false, Consumer init without start webServer");
        }
    }
}
