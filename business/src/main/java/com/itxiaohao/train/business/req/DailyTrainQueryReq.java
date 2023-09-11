package com.itxiaohao.train.business.req;

import com.itxiaohao.train.common.req.PageReq;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString
public class DailyTrainQueryReq extends PageReq {
    private String code;
    //GET请求的日期是拼接在URL里的，校验需要用@DateTimeFormat
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
