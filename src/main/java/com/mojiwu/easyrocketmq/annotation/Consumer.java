

package com.mojiwu.easyrocketmq.annotation;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

import java.lang.annotation.*;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/9 5:08 下午
 */
@Target(value = {ElementType.METHOD,ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Consumer {
    /**
     * MessageModel 消息监听模式，默认集群模式
     */
    MessageModel messageModel() default MessageModel.CLUSTERING;

    /**
     * Topic
     * 用于配置当前类中消费方法所使用的默认 Topic
     * 支持 spring 环境属性占位符 ${}
     */
    String topic();

    /**
     * Consumer Group
     * 用于配置当前类中消费方法所使用的默认GID
     * 支持 spring 环境属性占位符${}
     */
    String consumerGroup() default "${rocketmq.consumerId}";

    /**
     * type 消息类型  1 普通 2 顺序
     * 用于配置监听的消息类型，监听普通消息还是顺序消息，默认为普通消息
     */
    String type() default "1";

    /**
     * 用于配置aliyun accessKey
     * 支持 spring 环境属性占位符 ${}
     */
    String accessKey() default "${rocketmq.accessKey}";

    /**
     * 用于配置aliyun secretKey
     * 支持 spring 环境属性占位符 ${}
     *
     * @return
     */
    String secretKey() default "${rocketmq.secretKey:}";


    /**
     * Name Server
     * 用于配置实例 NAMESRV_ADDR
     * 支持 spring 环境属性占位符 ${}
     */
    String nameServer() default "${rocketmq.nameServer:}";

}
