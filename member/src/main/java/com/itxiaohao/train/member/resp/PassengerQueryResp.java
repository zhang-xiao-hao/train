package com.itxiaohao.train.member.resp;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class PassengerQueryResp {
    private Long id;

    private Long memberId;

    private String name;

    private String idCard;

    private String type;

    private Date createTime;

    private Date updateTime;
}