package com.whedc.loadBalancer;

import com.whedc.model.ServiceMeteInfo;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 一致性哈希算法负载均衡器
 * 使用TreeMap实现一致性Hash环
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {
    /**
     * 一致性哈希环，存放虚拟节点
     */
    private final TreeMap<Long, ServiceMeteInfo> virtualNode = new TreeMap<>();
    /**
     * 虚拟节点的数量
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMeteInfo select(Map<String, Object> requestParams, List<ServiceMeteInfo> serviceMeteInfoList) {
        // 构建虚拟节点环
        for (ServiceMeteInfo serviceMeteInfo : serviceMeteInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                Long hashKey = getHash(serviceMeteInfo.getServiceAddress() + "#" + i);
                virtualNode.put(hashKey, serviceMeteInfo);
            }
        }

        // 获取调用请求的hashKey
        Long hashKey = getHash(requestParams);

        // 选择最近的大于请求hashKey的节点
        // 如果节点不存在，则返回环首部的节点元素
        Map.Entry<Long, ServiceMeteInfo> ceilingEntry = virtualNode.ceilingEntry(hashKey);
        if (ceilingEntry == null) {
            ceilingEntry = virtualNode.firstEntry();
        }
        return ceilingEntry.getValue();
    }

    /**
     * Hash算法
     * @param key
     * @return
     */
    private Long getHash(Object key) {
        long result = 17; // 初始值选择一个质数
        Field[] fields = key.getClass().getDeclaredFields();
        Arrays.sort(fields, (f1, f2) -> f1.getName().compareTo(f2.getName())); // 按照属性名字典序排序
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(key);
                if (value != null) {
                    result = 31 * result + value.hashCode(); // 通过哈希码混合来更新结果
                }
            } catch (IllegalAccessException e) {
                // 处理异常
                e.printStackTrace();
            }
        }
        return result;
    }
}
