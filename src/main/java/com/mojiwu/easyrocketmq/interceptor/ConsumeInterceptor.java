package com.mojiwu.easyrocketmq.interceptor;

import java.lang.reflect.Method;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/15 5:56 下午
 */
public interface ConsumeInterceptor {

    void preHandle(Object target, Method method, Object[] args);

    void afterHandle(Object target, Method method, Object result);

}
