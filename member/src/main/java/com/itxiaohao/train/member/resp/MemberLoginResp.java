package com.itxiaohao.train.member.resp;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MemberLoginResp {
    private Long id;

    private String mobile;

    private String token;

}