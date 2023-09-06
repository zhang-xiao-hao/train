package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.TrainCarriageQueryReq;
import com.itxiaohao.train.business.req.TrainCarriageSaveReq;
import com.itxiaohao.train.business.resp.TrainCarriageQueryResp;
import com.itxiaohao.train.business.service.TrainCarriageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-carriage")
public class TrainCarriageAdminController {
    @Resource
    private TrainCarriageService trainCarriageService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainCarriageSaveReq req){
        trainCarriageService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainCarriageQueryResp>> query(@Valid TrainCarriageQueryReq req){
        return new CommonResp<>(trainCarriageService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        trainCarriageService.delete(id);
        return new CommonResp<>();
    }
}
