package com.itxiaohao.train.business.req;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StationSaveReq {
    /**
    * id
    */
    private Long id;
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
    * 站名拼音首字母
    */
    @NotBlank(message = "站名拼音首字母不能为空")
    private String namePy;
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
