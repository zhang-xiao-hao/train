package com.itxiaohao.train.business.controller;

import com.itxiaohao.train.business.req.StationQueryReq;
import com.itxiaohao.train.business.req.StationSaveReq;
import com.itxiaohao.train.business.resp.StationQueryResp;
import com.itxiaohao.train.business.service.StationService;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/station")
public class StationController {
    @Resource
    private StationService stationService;
    @GetMapping("/query-all")
    public CommonResp<List<StationQueryResp>> queryList(){
        return new CommonResp<>(stationService.queryAll());
    }
}
