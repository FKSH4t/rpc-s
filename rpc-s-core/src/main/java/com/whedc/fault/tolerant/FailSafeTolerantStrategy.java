package com.whedc.fault.tolerant;

import com.whedc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理，只记录日志，不做其它处理
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy{
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("服务调用错误，记录日志", e);
        return new RpcResponse();
    }
}
