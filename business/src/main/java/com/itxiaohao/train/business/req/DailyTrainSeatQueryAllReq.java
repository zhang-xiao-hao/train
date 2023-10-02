package com.itxiaohao.train.business.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: itxiaohao
 * @date: 2023-10-02 15:25
 * @Description:
 */
@Data
@ToString
public class DailyTrainSeatQueryAllReq {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "日期不能为空")
    private Date date;
    @NotBlank(message = "车次编号不能为空")
    private String trainCode;
}
