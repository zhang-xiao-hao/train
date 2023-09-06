package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.TrainStationQueryReq;
import com.itxiaohao.train.business.req.TrainStationSaveReq;
import com.itxiaohao.train.business.resp.TrainStationQueryResp;
import com.itxiaohao.train.business.service.TrainStationService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/train-station")
public class TrainStationAdminController {
    @Resource
    private TrainStationService trainStationService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainStationSaveReq req){
        trainStationService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainStationQueryResp>> query(@Valid TrainStationQueryReq req){
        return new CommonResp<>(trainStationService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        trainStationService.delete(id);
        return new CommonResp<>();
    }
}
