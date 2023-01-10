package com.mojiwu.easyrocketmq.common;


/**
 * @author mojiwu
 * @description
 * @date 2022/12/9 5:14 下午
 */
public enum RocketTag {

    GET(0),
    DELETE(1),
    UPDATE(2),
    INSERT(3),
    ALL(4);


    private final int value;


    RocketTag(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

}
