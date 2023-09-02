package com.itxiaohao.train.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Author: itxiaohao
 * @date: 2023-09-02 19:40
 * @Description:
 */
@Data
@ToString
public class PassengerQueryReq {
    private Long memberId;
}
