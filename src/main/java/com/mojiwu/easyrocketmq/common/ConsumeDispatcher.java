package com.mojiwu.easyrocketmq.common;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.shade.com.google.common.collect.HashBasedTable;
import com.mojiwu.easyrocketmq.interceptor.ConsumeInterceptor;

import java.lang.reflect.Proxy;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/12 2:56 下午
 */
public class ConsumeDispatcher {

    private final Object target = null;

    private final HashBasedTable<String, String, BaseHandler> dispatch = HashBasedTable.create();

    public Object invoke(Message message) {
        return dispatch.get(message.getTopic(), getTag(message.getTag())).handle(message);

    }

    public void add(String topic, String tag, MethodHandler methodHandler, ConsumeInterceptor[] consumeInterceptors) {
        this.dispatch.put(topic, tag, (BaseHandler) Proxy.newProxyInstance(methodHandler.getClass().getClassLoader(),
                new Class[]{BaseHandler.class}, new DefaultInvocationHandlerFactory().create(methodHandler, methodHandler.getBridgeMethod(),consumeInterceptors)));
    }


    private String getTag(String tag) {
        if (tag == null || "*".equals(tag)) {
            tag = "*";
        }
        return tag;
    }
}
