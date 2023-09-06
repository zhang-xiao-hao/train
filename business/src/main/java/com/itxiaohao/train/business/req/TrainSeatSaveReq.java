package com.itxiaohao.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrainSeatSaveReq {
    /**
    * id
    */
    private Long id;
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
    * 同车厢座序
    */
    @NotNull(message = "同车厢座序不能为空")
    private Integer carriageSeatIndex;
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
