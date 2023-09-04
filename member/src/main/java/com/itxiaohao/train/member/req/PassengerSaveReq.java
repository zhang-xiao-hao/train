package com.itxiaohao.train.member.req;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PassengerSaveReq {
    private Long id;
    private Long memberId;
    @NotBlank(message = "名字不能为空")
    private String name;
    @NotBlank(message = "身份证不能为空")
    private String idCard;
    @NotBlank(message = "旅客类型不能为空")
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
