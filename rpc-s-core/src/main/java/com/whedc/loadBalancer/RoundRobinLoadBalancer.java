package com.whedc.loadBalancer;

import com.whedc.model.ServiceMeteInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer{
    /**
     * 使用JUC包下的AtomicInteger实现原子计数器
     * 防止并发冲突
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    /**
     * 轮询算法实现负载均衡
     * @param requestParams 请求参数
     * @param serviceMeteInfoList 可用服务列表
     * @return
     */
    @Override
    public ServiceMeteInfo select(Map<String, Object> requestParams, List<ServiceMeteInfo> serviceMeteInfoList) {
        int size = serviceMeteInfoList.size();
        // 服务列表为空直接返回
        if (size == 0) {
            return null;
        }
        // 列表中只有一个服务，无需轮询
        if (size == 1) {
            return serviceMeteInfoList.get(0);
        }
        // 取模运算
        int index = currentIndex.getAndIncrement() % size;
        return serviceMeteInfoList.get(index);
    }
}
