package com.itxiaohao.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ConfirmOrderSaveReq {
    /**
    * id
    */
    private Long id;
    /**
    * 会员id
    */
    @NotNull(message = "会员id不能为空")
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
    @NotBlank(message = "车票不能为空")
    private String tickets;
    /**
    * 订单状态|枚举[ConfirmOrderStatusEnum]
    */
    @NotBlank(message = "订单状态不能为空")
    private String status;
    /**
    * 新增时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
    * 修改时间
    */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
}
