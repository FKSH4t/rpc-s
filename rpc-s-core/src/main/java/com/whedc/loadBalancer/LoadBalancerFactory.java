package com.whedc.loadBalancer;

import com.whedc.utils.SpiLoader;

public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }

    // 默认使用round robin负载均衡器
    private static final LoadBalancer DEFAULT_LOAD_BALANCER = new RoundRobinLoadBalancer();

    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }
}
