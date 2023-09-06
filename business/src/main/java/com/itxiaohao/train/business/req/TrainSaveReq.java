package com.itxiaohao.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrainSaveReq {
    /**
    * id
    */
    private Long id;
    /**
    * 车次编号
    */
    @NotBlank(message = "车次编号不能为空")
    private String code;
    /**
    * 车次类型|枚举[TrainTypeEnum]
    */
    @NotBlank(message = "车次类型不能为空")
    private String type;
    /**
    * 始发站
    */
    @NotBlank(message = "始发站不能为空")
    private String start;
    /**
    * 始发站拼音
    */
    @NotBlank(message = "始发站拼音不能为空")
    private String startPinyin;
    /**
    * 出发时间
    */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "出发时间不能为空")
    private Date startTime;
    /**
    * 终点站
    */
    @NotBlank(message = "终点站不能为空")
    private String end;
    /**
    * 终点站拼音
    */
    @NotBlank(message = "终点站拼音不能为空")
    private String endPinyin;
    /**
    * 到站时间
    */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "到站时间不能为空")
    private Date endTime;
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
