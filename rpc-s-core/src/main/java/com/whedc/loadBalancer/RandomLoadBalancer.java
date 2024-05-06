package com.whedc.loadBalancer;

import com.whedc.model.ServiceMeteInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 使用Java原生的Random类实现随机算法
 */
public class RandomLoadBalancer implements LoadBalancer{
    private final Random random = new Random();
    @Override
    public ServiceMeteInfo select(Map<String, Object> requestParams, List<ServiceMeteInfo> serviceMeteInfoList) {
        int size = serviceMeteInfoList.size();
        // 服务列表为空直接返回
        if (size == 0) {
            return null;
        }
        // 列表中只有一个服务，无需随机
        if (size == 1) {
            return serviceMeteInfoList.get(0);
        }
        return serviceMeteInfoList.get(random.nextInt(size));
    }
}
