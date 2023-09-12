package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.DailyTrainTicketQueryReq;
import com.itxiaohao.train.business.req.DailyTrainTicketSaveReq;
import com.itxiaohao.train.business.resp.DailyTrainTicketQueryResp;
import com.itxiaohao.train.business.service.DailyTrainTicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/daily-train-ticket")
public class DailyTrainTicketAdminController {
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody DailyTrainTicketSaveReq req){
        dailyTrainTicketService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<DailyTrainTicketQueryResp>> query(@Valid DailyTrainTicketQueryReq req){
        return new CommonResp<>(dailyTrainTicketService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        dailyTrainTicketService.delete(id);
        return new CommonResp<>();
    }
}
