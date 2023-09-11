package com.itxiaohao.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DailyTrainSeatSaveReq {
    /**
    * id
    */
    private Long id;
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
    * 箱序
    */
    @NotNull(message = "箱序不能为空")
    private Integer carriageIndex;
    /**
    * 排号|01, 02
    */
    @NotBlank(message = "排号不能为空")
    private String row;
    /**
    * 列号|枚举[SeatColEnum]
    */
    @NotBlank(message = "列号不能为空")
    private String col;
    /**
    * 座位类型|枚举[SeatTypeEnum]
    */
    @NotBlank(message = "座位类型不能为空")
    private String seatType;
    /**
    * 同车箱座序
    */
    @NotNull(message = "同车箱座序不能为空")
    private Integer carriageSeatIndex;
    /**
    * 售卖情况|将经过的车站用01拼接，0表示可卖，1表示已卖
    */
    @NotBlank(message = "售卖情况不能为空")
    private String sell;
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
