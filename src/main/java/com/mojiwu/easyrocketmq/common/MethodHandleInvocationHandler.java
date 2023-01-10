package com.mojiwu.easyrocketmq.common;

import com.mojiwu.easyrocketmq.interceptor.ConsumeInterceptor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/15 3:34 下午
 */
@Slf4j
public class MethodHandleInvocationHandler implements InvocationHandler {

    private Object target;

    private ConsumeInterceptor[] interceptors;

    public MethodHandleInvocationHandler(Object target) {
        this.target = target;
    }

    public MethodHandleInvocationHandler(Object target, ConsumeInterceptor[] interceptors) {
        this.target = target;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke =null;
        batchPreStart(method, args);
        try {
            invoke = method.invoke(this.target, args);
        }catch (Exception e){
            log.error("调用代理出错",e);
        }
        batchAfterStart(method, invoke);
        return invoke;
    }

    private void batchPreStart(Method method, Object[] args) {
        if(interceptors == null){
            return;
        }
        for (ConsumeInterceptor interceptor : interceptors) {
            interceptor.preHandle(this.target, method, args);
        }
    }

    private void batchAfterStart(Method method, Object invoke) {
        if(interceptors == null){
            return;
        }
        for (ConsumeInterceptor interceptor : interceptors) {
            interceptor.afterHandle(this.target, method, invoke);
        }
    }


}
