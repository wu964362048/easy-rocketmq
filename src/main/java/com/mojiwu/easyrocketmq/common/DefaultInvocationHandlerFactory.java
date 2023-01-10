package com.mojiwu.easyrocketmq.common;

import com.mojiwu.easyrocketmq.interceptor.ConsumeInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/20 12:40 下午
 */
@Slf4j
public class DefaultInvocationHandlerFactory implements InvocationHandlerFactory {

    @Override
    public InvocationHandler create(Object bean, Method method) {
        return new MethodHandleInvocationHandler(bean);
    }

    @Override
    public InvocationHandler create(Object bean, Method method, ConsumeInterceptor[] consumeInterceptors) {
        return new MethodHandleInvocationHandler(bean,consumeInterceptors);

    }
}
