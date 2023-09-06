package com.itxiaohao.train.business.controller;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.StationQueryReq;
import com.itxiaohao.train.business.req.StationSaveReq;
import com.itxiaohao.train.business.resp.StationQueryResp;
import com.itxiaohao.train.business.service.StationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/station")
public class StationController {
    @Resource
    private StationService stationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody StationSaveReq req){
        stationService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<StationQueryResp>> query(@Valid StationQueryReq req){
        return new CommonResp<>(stationService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        stationService.delete(id);
        return new CommonResp<>();
    }
}
