package com.mojiwu.easyrocketmq.annotation;

import com.mojiwu.easyrocketmq.common.RocketTag;

import java.lang.annotation.*;

/**
 * @author mojiwu
 * @description
 * @date 2022/12/9 5:08 下午
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface ConsumeTag {

    RocketTag value();

}
