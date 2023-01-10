package com.mojiwu.easyrocketmq.common;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/12 10:05 上午
 */
@Slf4j
public class MethodHandler implements BaseHandler {

    private final Method method;

    private final Method bridgeMethod;

    private final Object target;

    private final Class<?> beanType;

    private final MethodParameter[] parameters;

    public MethodHandler(Object bean, Method method) {
        this.method = method;
        this.bridgeMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.target = bean;
        this.beanType = bean.getClass();
        this.parameters = initParameter();
    }


    public MethodParameter[] initParameter() {
        int count = this.bridgeMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            MethodParameter parameter = new MethodParameter(this.bridgeMethod, i);
            GenericTypeResolver.resolveParameterType(parameter, this.beanType);
            result[i] = parameter;
        }
        return result;
    }

    @Override
    public Object handle(Message message) {
        Object[] args = doResolveArgs(message);
        return doHandle(args);
    }

    private Object doHandle(Object... args) {
        try {
            return this.bridgeMethod.invoke(this.target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.error("IllegalAccessException :{}", e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            log.error("InvocationTargetException :{}", e.getMessage());
        }
        throw new IllegalStateException("IllegalStateException");
    }

    private Object[] doResolveArgs(Message message) {
        Object[] args = new Object[this.parameters.length];
        String s = new String(message.getBody());
        for (int i = 0; i < this.parameters.length; i++) {
            args[i] = JSONObject.parseObject(s, parameters[i].getParameterType());
        }
        return args;
    }

    public Method getBridgeMethod() {
        return method;
    }

    public Class<?> getBeanType() {
        return beanType;
    }
}
