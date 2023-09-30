package com.itxiaohao.train.business.req;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ConfirmOrderDoReq {
    /**
     * 日志跟踪号
     */
    private String logId;
    /**
    * id
    */
    private Long id;
    /**
    * 会员id
    */
    private Long memberId;
    /**
    * 日期
    */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "日期不能为空")
    private Date date;
    /**
    * 车次编号
    */
    @NotBlank(message = "车次编号不能为空")
    private String trainCode;
    /**
    * 出发站
    */
    @NotBlank(message = "出发站不能为空")
    private String start;
    /**
    * 到达站
    */
    @NotBlank(message = "到达站不能为空")
    private String end;
    /**
    * 余票ID
    */
    @NotNull(message = "余票ID不能为空")
    private Long dailyTrainTicketId;
    /**
    * 车票
    */
    @NotEmpty(message = "车票不能为空")
    private List<ConfirmOrderTicketReq> tickets;
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String imageCode;
    /**
     * 图片验证码token
     */
    @NotBlank(message = "图片验证码参数非法")
    private String imageCodeToken;
}
