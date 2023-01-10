package com.mojiwu.easyrocketmq.common;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/12 2:52 下午
 */
public class DefaultMessageListener extends ConsumeDispatcher implements MessageListener {


    @Override
    public Action consume(Message message, ConsumeContext context) {
        try {
            Object result = invoke(message);
            if (result instanceof Action) {
                return (Action) result;
            }
        } catch (Exception e) {
            return Action.ReconsumeLater;
        }
        return Action.CommitMessage;
    }
}
