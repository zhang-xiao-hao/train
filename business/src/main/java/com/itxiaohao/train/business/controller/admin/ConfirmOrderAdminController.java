package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.ConfirmOrderQueryReq;
import com.itxiaohao.train.business.req.ConfirmOrderDoReq;
import com.itxiaohao.train.business.resp.ConfirmOrderQueryResp;
import com.itxiaohao.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/confirm-order")
public class ConfirmOrderAdminController {
    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ConfirmOrderDoReq req){
        confirmOrderService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<ConfirmOrderQueryResp>> query(@Valid ConfirmOrderQueryReq req){
        return new CommonResp<>(confirmOrderService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        confirmOrderService.delete(id);
        return new CommonResp<>();
    }
}
