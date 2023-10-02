package com.itxiaohao.train.business.controller;

import com.itxiaohao.train.business.req.SeatSellReq;
import com.itxiaohao.train.business.resp.SeatSellResp;
import com.itxiaohao.train.business.service.DailyTrainSeatService;
import com.itxiaohao.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: itxiaohao
 * @date: 2023-10-02 16:05
 * @Description:
 */
@RestController
@RequestMapping("/seat-sell")
public class SeatSellController {
    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;
    @GetMapping("/query")
    public CommonResp<List<SeatSellResp>> query(@Valid SeatSellReq req){
        List<SeatSellResp> seatList = dailyTrainSeatService.querySeatSell(req);
        return new CommonResp<>(seatList);
    }
}
