package com.whedc.fault.retry;

import com.whedc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * 不使用重试策略
 * 直接执行任务
 */
@Slf4j
public class NoRetryingStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
