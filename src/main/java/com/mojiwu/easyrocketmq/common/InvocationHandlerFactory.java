package com.mojiwu.easyrocketmq.common;

import com.mojiwu.easyrocketmq.interceptor.ConsumeInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/15 3:03 下午
 */
public interface InvocationHandlerFactory {

    InvocationHandler create(Object bean, Method method);

    InvocationHandler create(Object bean, Method method, ConsumeInterceptor[] consumeInterceptors);

    final class Default implements InvocationHandlerFactory {
        @Override
        public InvocationHandler create(Object bean, Method method) {
            return new MethodHandleInvocationHandler(bean);
        }

        @Override
        public InvocationHandler create(Object bean, Method method, ConsumeInterceptor[] consumeInterceptors) {
            return new MethodHandleInvocationHandler(bean);
        }

    }
}
