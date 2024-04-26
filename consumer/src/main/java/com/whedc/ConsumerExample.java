package com.whedc;

import com.whedc.config.RpcConfig;
import com.whedc.utils.ConfigUtil;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig config = ConfigUtil.loadConfig(RpcConfig.class, "rpc");
        System.out.println(config);
    }
}
