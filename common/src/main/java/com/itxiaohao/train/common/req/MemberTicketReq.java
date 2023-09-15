package com.itxiaohao.train.common.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Author: itxiaohao
 * @date: 2023-09-15 14:47
 * @Description:
 */
@Data
@ToString
public class MemberTicketReq {
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
     * 乘客id
     */
    @NotNull(message = "乘客id不能为空")
    private Long passengerId;
    /**
     * 乘客姓名
     */
    private String passengerName;
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
     * 厢序
     */
    @NotNull(message = "厢序不能为空")
    private Integer carriageIndex;
    /**
     * 排号|01，02
     */
    @NotBlank(message = "排号不能为空")
    private String row;
    /**
     * 列号|枚举[SeatColEnum]
     */
    @NotBlank(message = "列号不能为空")
    private String col;
    /**
     * 出发站
     */
    @NotBlank(message = "出发站不能为空")
    private String start;
    /**
     * 出发时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "出发时间不能为空")
    private Date startTime;
    /**
     * 到达站
     */
    @NotBlank(message = "到达站不能为空")
    private String end;
    /**
     * 到达时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "到达时间不能为空")
    private Date endTime;
    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @NotBlank(message = "座位类型不能为空")
    private String seatType;
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
