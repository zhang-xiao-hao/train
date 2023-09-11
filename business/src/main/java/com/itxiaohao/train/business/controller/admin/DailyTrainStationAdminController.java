package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.DailyTrainStationQueryReq;
import com.itxiaohao.train.business.req.DailyTrainStationSaveReq;
import com.itxiaohao.train.business.resp.DailyTrainStationQueryResp;
import com.itxiaohao.train.business.service.DailyTrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-station")
public class DailyTrainStationAdminController {
    @Resource
    private DailyTrainStationService dailyTrainStationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainStationSaveReq req){
        dailyTrainStationService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainStationQueryResp>> query(@Valid DailyTrainStationQueryReq req){
        return new CommonResp<>(dailyTrainStationService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        dailyTrainStationService.delete(id);
        return new CommonResp<>();
    }
}
