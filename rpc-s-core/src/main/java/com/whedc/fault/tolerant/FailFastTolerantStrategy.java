package com.whedc.fault.tolerant;

import com.whedc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败
 */
public class FailFastTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务调用失败！错误：", e);
    }
}
