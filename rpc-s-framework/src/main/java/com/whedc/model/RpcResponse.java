package com.whedc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Response的作用是封装服务方法调用得到的返回结果
 * 以及调用失败时产生的异常信息
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    /**
     * 响应数据
     */
    private Object result;
    /**
     * 响应数据类型
     */
    private Class<?> resultType;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 异常信息
     */
    private Exception exception;
}
