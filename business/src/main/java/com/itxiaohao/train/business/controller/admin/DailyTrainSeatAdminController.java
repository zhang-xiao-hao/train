package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.DailyTrainSeatQueryReq;
import com.itxiaohao.train.business.req.DailyTrainSeatSaveReq;
import com.itxiaohao.train.business.resp.DailyTrainSeatQueryResp;
import com.itxiaohao.train.business.service.DailyTrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-seat")
public class DailyTrainSeatAdminController {
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainSeatSaveReq req){
        dailyTrainSeatService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainSeatQueryResp>> query(@Valid DailyTrainSeatQueryReq req){
        return new CommonResp<>(dailyTrainSeatService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        dailyTrainSeatService.delete(id);
        return new CommonResp<>();
    }
}
