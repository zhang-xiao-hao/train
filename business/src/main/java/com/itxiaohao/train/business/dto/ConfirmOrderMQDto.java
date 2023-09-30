package com.itxiaohao.train.business.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Author: itxiaohao
 * @date: 2023-09-30 16:39
 * @Description:
 */
@Data
@ToString
public class ConfirmOrderMQDto {
    private String logId;
    private Date date;
    private String trainCode;
}
