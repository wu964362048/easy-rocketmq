package com.mojiwu.easyrocketmq.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.mojiwu.easyrocketmq.common.MethodHandler;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/15 6:04 下午
 */
@Slf4j
public class LoggingInterceptor implements ConsumeInterceptor {

    @Override
    public void preHandle(Object target, Method method, Object[] args) {
        log.info("PRE:执行对象：{},执行方法：{},接收消息体：{}",
                getProxyClassName(target), getProxyMethodName(target),
                JSONObject.toJSONString(args[0]));
    }

    @Override
    public void afterHandle(Object target, Method method, Object result) {
        log.info("AFTER:执行对象：{},执行方法：{},消息回复：{}",
                getProxyClassName(target), getProxyMethodName(target),
                JSONObject.toJSONString(result));
    }


    public String getProxyClassName(Object target) {
        return ((MethodHandler)target).getBeanType().getSimpleName();
    }


    public String getProxyMethodName(Object target) {
        return ((MethodHandler)target).getBridgeMethod().getName();
    }
}
