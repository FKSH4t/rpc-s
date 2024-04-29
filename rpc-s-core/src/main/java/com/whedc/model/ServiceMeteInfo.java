package com.whedc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务元信息(服务注册信息)
 */

@Data
public class ServiceMeteInfo {
    /**
     * 服务名
     */
    private String serviceName;
    /**
     * 服务版本
     * 默认1.0
     */
    private String serviceVersion = "1.0";
    /**
     * 服务域名
     */
    private String serviceHost;
    /**
     * 服务端口号
     */
    private Integer servicePort;
    /**
     * 服务分组
     */
    private String serviceGroup = "default";
    /**
     * 获取服务的key
     * key设计：serviceName:serviceVersion
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 获取服务节点key
     * key设计：serviceKey:serviceHost:servicePort
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
