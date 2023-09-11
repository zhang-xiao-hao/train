package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.DailyTrainCarriageQueryReq;
import com.itxiaohao.train.business.req.DailyTrainCarriageSaveReq;
import com.itxiaohao.train.business.resp.DailyTrainCarriageQueryResp;
import com.itxiaohao.train.business.service.DailyTrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-carriage")
public class DailyTrainCarriageAdminController {
    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainCarriageSaveReq req){
        dailyTrainCarriageService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainCarriageQueryResp>> query(@Valid DailyTrainCarriageQueryReq req){
        return new CommonResp<>(dailyTrainCarriageService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        dailyTrainCarriageService.delete(id);
        return new CommonResp<>();
    }
}
