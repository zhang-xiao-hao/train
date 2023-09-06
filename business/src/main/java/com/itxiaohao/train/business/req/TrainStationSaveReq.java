package com.itxiaohao.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrainStationSaveReq {
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
    * 站序
    */
    @NotNull(message = "站序不能为空")
    private Integer index;
    /**
    * 站名
    */
    @NotBlank(message = "站名不能为空")
    private String name;
    /**
    * 站名拼音
    */
    @NotBlank(message = "站名拼音不能为空")
    private String namePinyin;
    /**
    * 进站时间
    */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date inTime;
    /**
    * 出站时间
    */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date outTime;
    /**
    * 停站时长
    */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date stopTime;
    /**
    * 里程（公里）|从上一站到本站的距离
    */
    @NotNull(message = "里程（公里）不能为空")
    private BigDecimal km;
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
