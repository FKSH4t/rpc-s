package com.whedc.bootstrap;

import com.whedc.RpcApplication;

/**
 * 消费者启动类
 */
public class ConsumerBootstrap {
    public static void init() {
        // 因为消费者不需要注册服务，也不需要启动web框架
        // 因此只需要完成通用框架的初始化即可
        RpcApplication.init();
    }
}
