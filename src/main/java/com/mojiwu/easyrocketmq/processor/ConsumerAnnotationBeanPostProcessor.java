package com.mojiwu.easyrocketmq.processor;

import com.aliyun.openservices.ons.api.Admin;
import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.mojiwu.easyrocketmq.common.ConsumeDispatcher;
import com.mojiwu.easyrocketmq.common.DefaultMessageListener;
import com.mojiwu.easyrocketmq.common.MethodHandler;
import com.mojiwu.easyrocketmq.annotation.ConsumeTag;
import com.mojiwu.easyrocketmq.annotation.Consumer;
import com.mojiwu.easyrocketmq.interceptor.ConsumeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/12 11:46 上午
 */
@Slf4j
public class ConsumerAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements SmartLifecycle, EnvironmentAware, ApplicationContextAware {

    private Environment environment;

    private ApplicationContext applicationContext;

    private boolean running = false;

    private Map<String, Admin> listenerMap = new HashMap<>();

    private ConsumeInterceptor[] consumeInterceptors;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    // new Bean之后
    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    // 初始化Bean 之前
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Map<String, ConsumeInterceptor> interceptorMap = this.applicationContext.getBeansOfType(ConsumeInterceptor.class);
        ConsumeInterceptor[] interceptors = new ConsumeInterceptor[interceptorMap.size()];
        interceptorMap.values().toArray(interceptors);
        this.consumeInterceptors = interceptors;
        buildConsumerMetaData(bean, beanName);
        return super.postProcessBeforeInitialization(bean, beanName);
    }

    private void buildConsumerMetaData(Object bean, String beanName) {
        Class<?> beanClass = AopProxyUtils.ultimateTargetClass(bean);
        Consumer consumer = AnnotationUtils.findAnnotation(beanClass, Consumer.class);
        if (Objects.isNull(consumer)) {
            return;
        }
        Properties properties = buildProperties(consumer);
        boolean order = Objects.equals(consumer.type(), "2");
        if (!order) {
            // 普通消息
            ConsumerBean consumerBean = new ConsumerBean();
            consumerBean.setProperties(properties);
            consumerBean.setSubscriptionTable(findSubscription(bean, consumer, beanClass));
            listenerMap.put(beanName, consumerBean);
        } else {
            // 顺序消息
        }
    }

    private Properties buildProperties(Consumer consumer) {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.GROUP_ID, environment.resolvePlaceholders(consumer.consumerGroup()));
        properties.setProperty(PropertyKeyConst.AccessKey, environment.resolvePlaceholders(consumer.accessKey()));
        properties.setProperty(PropertyKeyConst.SecretKey, environment.resolvePlaceholders(consumer.secretKey()));
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, environment.resolvePlaceholders(consumer.nameServer()));
        properties.setProperty(PropertyKeyConst.MessageModel, consumer.messageModel().getModeCN());
        return properties;
    }

    private Map<Subscription, MessageListener> findSubscription(Object bean, Consumer consumer, Class<?> beanClass) {
        Map<String, MessageListener> listenerMap = new HashMap<>();

        Set<String> tagSet = new HashSet<>();

        Set<String> tagExistSet = new HashSet<>();

        ReflectionUtils.doWithMethods(beanClass, method -> {
            ConsumeTag event = AnnotationUtils.findAnnotation(method, ConsumeTag.class);
            if (Objects.nonNull(event)) {
                if (tagExistSet.contains(event.value().name())) {
                    throw new RuntimeException("不允许存在相同的消费者组和Tag！");
                } else {
                    tagExistSet.add(event.value().name());
                    tagSet.add(event.value().name());
                }
            } else {
                return;
            }
            if (method.getParameterCount() != 1) {
                throw new RuntimeException("消费方法类仅支持单参数！");
            }
            String topic = environment.resolvePlaceholders(consumer.topic());
            DefaultMessageListener listener = (DefaultMessageListener) listenerMap.get(topic);
            if (listener == null) {
                listener = new DefaultMessageListener();
                listenerMap.put(topic, listener);
            }
            addHandler(bean, method, topic, event.value().name(), listener);
        });

        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        for (Map.Entry<String, MessageListener> listenerEntry : listenerMap.entrySet()) {
            String topic = listenerEntry.getKey();
            MessageListener listener = listenerEntry.getValue();
            Subscription subscription = new Subscription();
            subscription.setTopic(topic);
            subscription.setExpression(combineTag(tagSet));
            subscriptionTable.put(subscription, listener);
        }

        return subscriptionTable;
    }

    private String combineTag(Set<String> tagSet) {
        if (tagSet.contains("*")) {
            return "*";
        } else {
            return String.join(" || ", tagSet);

        }
    }


    private void addHandler(Object bean, Method method, String topic, String combineTag, ConsumeDispatcher handler) {
        MethodHandler methodHandler = new MethodHandler(bean, method);
        handler.add(topic, combineTag, methodHandler,consumeInterceptors);
    }


    @Override
    public void start() {
        if (!listenerMap.isEmpty()) {
            for (Admin value : listenerMap.values()) {
                value.start();
            }
        }
        this.running = true;
    }

    @Override
    public void stop() {
        if (!listenerMap.isEmpty()) {
            for (Admin value : listenerMap.values()) {
                value.shutdown();
            }
        }
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
