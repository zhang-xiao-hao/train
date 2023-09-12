package com.itxiaohao.train.business.controller;

import com.itxiaohao.train.business.req.TrainQueryReq;
import com.itxiaohao.train.business.req.TrainSaveReq;
import com.itxiaohao.train.business.resp.TrainQueryResp;
import com.itxiaohao.train.business.service.TrainSeatService;
import com.itxiaohao.train.business.service.TrainService;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/train")
public class TrainController {
    @Resource
    private TrainService trainService;
    @Resource
    private TrainSeatService trainSeatService;

    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList(){
        return new CommonResp<>(trainService.queryAll());
    }
}
