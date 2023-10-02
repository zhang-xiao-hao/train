package com.itxiaohao.train.business.resp;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: itxiaohao
 * @date: 2023-10-02 16:08
 * @Description:
 */
@Data
@ToString
public class SeatSellResp {
    // 箱序
    private Integer carriageIndex;
    // 座位行号
    private String row;
    // 列号[SeatColumnEnum]
    private String col;
    // 座位类型[SeatTypeEnum]
    private String seatType;
    // 是家售卖信息
    private String sell;
}
