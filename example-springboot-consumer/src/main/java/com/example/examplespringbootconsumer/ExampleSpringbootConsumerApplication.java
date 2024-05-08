package com.example.examplespringbootconsumer;

import com.whedc.config.RpcConfig;
import com.whedc.rpcsspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableRpc(needServer = false)
public class ExampleSpringbootConsumerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleSpringbootConsumerApplication.class, args);
        RpcConfig rpcConfig = context.getBean(RpcConfig.class);
        System.out.println("rpcConfig = " + rpcConfig);
    }

}
