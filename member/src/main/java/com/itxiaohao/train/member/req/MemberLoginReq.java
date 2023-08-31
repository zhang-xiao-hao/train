package com.itxiaohao.train.member.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

/**
 * @Author: itxiaohao
 * @date: 2023-08-31 18:23
 * @Description:
 */
@Data
@ToString
public class MemberLoginReq {
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号码格式错误")
    private String mobile;

    @NotBlank(message = "[验证码]不能为空")
    private String code;
}
