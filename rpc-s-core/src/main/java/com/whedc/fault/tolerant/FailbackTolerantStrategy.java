package com.whedc.fault.tolerant;

import com.whedc.model.RpcResponse;

import java.util.Map;

/**
 * 故障恢复策略
 */
public class FailbackTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 服务降级
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setMessage("error");
        rpcResponse.setException(e);
        rpcResponse.setResult(context);
        return rpcResponse;
    }
}
