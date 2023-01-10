package com.mojiwu.easyrocketmq.config;

import com.mojiwu.easyrocketmq.interceptor.LoggingInterceptor;
import com.mojiwu.easyrocketmq.processor.ConsumerAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/26 4:09 下午
 */
@Configuration
public class RocketMqAutoConfiguration {

    @Bean
    public ConsumerAnnotationBeanPostProcessor annotationBeanPostProcessor(){
        return new ConsumerAnnotationBeanPostProcessor();
    }

    @Bean
    public LoggingInterceptor loggingInterceptor(){
        return new LoggingInterceptor();
    }

}
