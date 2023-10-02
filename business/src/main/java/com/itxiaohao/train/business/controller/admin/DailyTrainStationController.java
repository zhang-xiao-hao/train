package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.business.req.DailyTrainSeatQueryAllReq;
import com.itxiaohao.train.business.resp.DailyTrainStationQueryResp;
import com.itxiaohao.train.business.service.DailyTrainStationService;
import com.itxiaohao.train.common.resp.CommonResp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: itxiaohao
 * @date: 2023-10-02 15:22
 * @Description:
 */
@RestController
@RequestMapping("/daily-train-station")
public class DailyTrainStationController {
    @Autowired
    private DailyTrainStationService dailyTrainStationService;
    @GetMapping("/query-by-train-code")
    public CommonResp<List<DailyTrainStationQueryResp>> queryByTrain(@Valid DailyTrainSeatQueryAllReq req){
        List<DailyTrainStationQueryResp> list = dailyTrainStationService.queryByTrain(req.getDate(), req.getTrainCode());
        return new CommonResp<>(list);
    }
}
