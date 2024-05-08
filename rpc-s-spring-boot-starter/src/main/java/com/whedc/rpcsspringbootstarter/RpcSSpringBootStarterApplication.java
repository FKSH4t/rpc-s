package com.whedc.rpcsspringbootstarter;

import com.whedc.config.RegistryConfig;
import com.whedc.config.RpcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class RpcSSpringBootStarterApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RpcSSpringBootStarterApplication.class, args);
        RpcConfig rpcConfig = context.getBean(RpcConfig.class);
        RegistryConfig registryConfig = context.getBean(RegistryConfig.class);
        System.out.println("rpcConfig = " + rpcConfig);
        System.out.println("registryConfig = " + registryConfig);
    }

}
