package com.itxiaohao.train.member.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

/**
 * @Author: itxiaohao
 * @date: 2023-08-30 21:07
 * @Description: DTO
 */
@Data
@ToString
public class MemberRegisterReq {
    @NotBlank(message = "[手机号]不能为空")
    private String mobile;
}
