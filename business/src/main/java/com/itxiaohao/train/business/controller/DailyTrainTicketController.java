package com.itxiaohao.train.business.controller;

import com.itxiaohao.train.business.req.DailyTrainTicketQueryReq;
import com.itxiaohao.train.business.req.DailyTrainTicketSaveReq;
import com.itxiaohao.train.business.resp.DailyTrainTicketQueryResp;
import com.itxiaohao.train.business.service.DailyTrainTicketService;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/daily-train-ticket")
public class DailyTrainTicketController {
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> query(@Valid DailyTrainTicketQueryReq req){
        return new CommonResp<>(dailyTrainTicketService.queryList(req));
    }
}
