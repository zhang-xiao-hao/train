package com.itxiaohao.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DailyTrainCarriageSaveReq {
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
    private Integer index;
    /**
    * 座位类型|枚举[SeatTypeEnum]
    */
    @NotBlank(message = "座位类型不能为空")
    private String seatType;
    /**
    * 座位数
    */
    private Integer seatCount;
    /**
    * 排数
    */
    @NotNull(message = "排数不能为空")
    private Integer rowCount;
    /**
    * 列数
    */
    private Integer colCount;
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
