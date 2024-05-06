package com.whedc.loadBalancer;

import com.whedc.model.ServiceMeteInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡
 */
public interface LoadBalancer {
    /**
     * 负载均衡算法选择服务调用
     * @param requestParams 请求参数
     * @param serviceMeteInfoList 可用服务列表
     * @return
     */
    ServiceMeteInfo select(Map<String, Object> requestParams, List<ServiceMeteInfo> serviceMeteInfoList);
}
