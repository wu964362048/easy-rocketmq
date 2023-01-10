package com.mojiwu.easyrocketmq.testing;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.mojiwu.easyrocketmq.common.RocketTag;
import com.mojiwu.easyrocketmq.annotation.ConsumeTag;
import com.mojiwu.easyrocketmq.annotation.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author mojiwu
 * @description
 * @date 2022/12/12 3:54 下午
 */
@Slf4j
@Component
@Consumer(topic = "${rocketmq.topic}")
public class ConsumerTest {


    @ConsumeTag(RocketTag.DELETE)
    public Action consumeForDelete(JSONObject message) {
        log.info("获取tag为 DELETE 的 Message：{}", JSONObject.toJSONString(message));
        return Action.CommitMessage;
    }

    @ConsumeTag(RocketTag.INSERT)
    public Action consumeForInsert(String message) {
        log.info("获取tag为 INSERT 的 Message：{}", JSONObject.toJSONString(message));
        return Action.CommitMessage;
    }

}
