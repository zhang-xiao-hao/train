package com.itxiaohao.train.common.exception;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: itxiaohao
 * @date: 2023-08-30 21:29
 * @Description: 自定义异常枚举类
 */
public enum BusinessExceptionEnum {
    MEMBER_MOBILE_EXIST("手机号已注册");
    private String desc;

    BusinessExceptionEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "BusinessExceptionEnum{" +
                "desc='" + desc + '\'' +
                '}';
    }

}
