package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.business.service.TrainSeatService;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.TrainQueryReq;
import com.itxiaohao.train.business.req.TrainSaveReq;
import com.itxiaohao.train.business.resp.TrainQueryResp;
import com.itxiaohao.train.business.service.TrainService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/train")
public class TrainAdminController {
    @Resource
    private TrainService trainService;
    @Resource
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody TrainSaveReq req){
        trainService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<TrainQueryResp>> query(@Valid TrainQueryReq req){
        return new CommonResp<>(trainService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        trainService.delete(id);
        return new CommonResp<>();
    }
    @GetMapping("/query-all")
    public CommonResp<List<TrainQueryResp>> queryList(){
        return new CommonResp<>(trainService.queryAll());
    }

    @GetMapping("/gen-seat/{trainCode}")
    public CommonResp<Object> genSeat(@PathVariable("trainCode") String trainCode){
        trainSeatService.genTrainSear(trainCode);
        return new CommonResp<>();
    }
}
