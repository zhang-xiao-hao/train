package com.itxiaohao.train.business.controller;

import com.itxiaohao.train.business.req.ConfirmOrderDoReq;
import com.itxiaohao.train.business.req.ConfirmOrderQueryReq;
import com.itxiaohao.train.business.resp.ConfirmOrderQueryResp;
import com.itxiaohao.train.business.service.ConfirmOrderService;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {
    @Resource
    private ConfirmOrderService confirmOrderService;

    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req){
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }
}
