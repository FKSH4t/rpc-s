package com.whedc.proxy;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@Slf4j
public class MockProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultReturn(returnType);
    }

    private Object getDefaultReturn(Class<?> returnType) {
        Faker faker = new Faker();
        // 若返回值为基本数据类型
        if (returnType.isPrimitive()) {
            if (returnType == int.class) {
                return faker.random().nextInt(10);
            } else if (returnType == boolean.class) {
                return faker.random().nextBoolean();
            } else if (returnType == short.class) {
                return (short) 0;
            } else if (returnType == long.class) {
                return faker.random().nextLong();
            }
        }

        // 若方法返回对象
        return "mock result object";
    }
}
