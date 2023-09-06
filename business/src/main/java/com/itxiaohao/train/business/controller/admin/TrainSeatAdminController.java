package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.TrainSeatQueryReq;
import com.itxiaohao.train.business.req.TrainSeatSaveReq;
import com.itxiaohao.train.business.resp.TrainSeatQueryResp;
import com.itxiaohao.train.business.service.TrainSeatService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-seat")
public class TrainSeatAdminController {
    @Resource
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSeatSaveReq req){
        trainSeatService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainSeatQueryResp>> query(@Valid TrainSeatQueryReq req){
        return new CommonResp<>(trainSeatService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        trainSeatService.delete(id);
        return new CommonResp<>();
    }
}
