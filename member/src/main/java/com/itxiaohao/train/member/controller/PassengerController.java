package com.itxiaohao.train.member.controller;

import com.itxiaohao.train.common.context.LoginMemberContext;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.member.req.MemberLoginReq;
import com.itxiaohao.train.member.req.PassengerQueryReq;
import com.itxiaohao.train.member.req.PassengerSaveReq;
import com.itxiaohao.train.member.resp.MemberLoginResp;
import com.itxiaohao.train.member.resp.PassengerQueryResp;
import com.itxiaohao.train.member.service.PassengerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: itxiaohao
 * @date: 2023-08-27 19:27
 * @Description:
 */
@RestController
@RequestMapping("/passenger")
public class PassengerController {
    @Resource
    private PassengerService passengerService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody PassengerSaveReq req){
        passengerService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<PassengerQueryResp>> query(@Valid PassengerQueryReq req){
        req.setMemberId(LoginMemberContext.getId());
        return new CommonResp<>(passengerService.queryList(req));
    }
}
