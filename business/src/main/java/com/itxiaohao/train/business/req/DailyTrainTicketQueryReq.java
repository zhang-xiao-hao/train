package com.itxiaohao.train.business.req;

import com.itxiaohao.train.common.req.PageReq;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ToString
public class DailyTrainTicketQueryReq extends PageReq {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private String trainCode;
    private String start;
    private String end;
}
