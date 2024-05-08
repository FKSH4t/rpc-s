package com.whedc.rpcsautoconfig.autoconfig;

import com.whedc.config.RegistryConfig;
import com.whedc.config.RpcConfig;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import com.whedc.rpcsautoconfig.configuraiton.RpcRegistryConfiguration;
import com.whedc.rpcsautoconfig.configuraiton.RpcConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {RpcRegistryConfiguration.class, RpcConfiguration.class})
@Slf4j
public class LoadConfig {
    @Bean
    public RegistryConfig registryConfig(RpcRegistryConfiguration rpcRegistryConfiguration) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(rpcRegistryConfiguration, RegistryConfig.class);
    }

    @Bean
    public RpcConfig rpcConfig(RpcConfiguration rpcConfiguration, RegistryConfig registryConfig) {
        ModelMapper modelMapper = new ModelMapper();
        RpcConfig rpcConfig = modelMapper.map(rpcConfiguration, RpcConfig.class);
        rpcConfig.setRegistryConfig(registryConfig);
        log.info("rpcConfig init");
        return rpcConfig;
    }

}
