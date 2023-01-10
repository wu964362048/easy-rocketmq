package com.mojiwu.easyrocketmq.common;

import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/15 3:12 下午
 */
public interface BaseHandler {

    Object handle(Message message);

}
