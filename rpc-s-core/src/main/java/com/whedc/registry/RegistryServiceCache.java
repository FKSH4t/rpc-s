package com.whedc.registry;

import cn.hutool.core.collection.CollUtil;
import com.whedc.model.ServiceMeteInfo;

import java.util.List;
import java.util.stream.Collectors;

public class RegistryServiceCache {
    /**
     * 服务缓存列表
     */
    List<ServiceMeteInfo> serviceCache;

    /**
     * 获取服务列表写入节点的本地缓存
     * @param serviceList kvClient查询出的服务列表
     */
    void writeCache(List<ServiceMeteInfo> serviceList) {
        if (this.serviceCache != null) {
            this.serviceCache.addAll(serviceList);
        } else {
            this.serviceCache = serviceList;
        }
    }

    /**
     * 获取缓存的服务列表
     * @return
     */
    List<ServiceMeteInfo> readCache() {
        return this.serviceCache;
    }

    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache = null;
    }

    public static List<ServiceMeteInfo> getServiceMeteInfos(String serviceKey, List<ServiceMeteInfo> cachedServiceList) {
        if (cachedServiceList != null ) {
            List<ServiceMeteInfo> serv = cachedServiceList.stream().map(kv -> {
                System.out.println(kv.getServiceKey());
                if (kv.getServiceKey().equalsIgnoreCase(serviceKey)) {
                    return kv;
                }
                return null;
            }).collect(Collectors.toList());
            serv = CollUtil.removeNull(serv);
            if (!serv.isEmpty()) {
                return serv;
            }
        }
        return null;
    }
}
