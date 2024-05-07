package com.whedc.fault.retry;

import com.github.rholder.retry.*;
import com.whedc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定重试时间间隔
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 重试条件，发生了异常就重试
                .retryIfExceptionOfType(Exception.class)
                // 等待策略，固定等待3秒进行重试
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                // 停止重试策略，重试三次后停止
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 重试监听器，当一次重试操作完成后会回调这个函数
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数，第{}次", attempt.getAttemptNumber());
                    }
                })
                .build();
        return retryer.call(callable);
    }
}
