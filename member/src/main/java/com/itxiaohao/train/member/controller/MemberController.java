package com.itxiaohao.train.member.controller;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.member.req.MemberLoginReq;
import com.itxiaohao.train.member.req.MemberRegisterReq;
import com.itxiaohao.train.member.req.MemberSendCodeReq;
import com.itxiaohao.train.member.resp.MemberLoginResp;
import com.itxiaohao.train.member.service.MemberService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: itxiaohao
 * @date: 2023-08-27 19:27
 * @Description:
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    @Resource
    private MemberService memberService;
    @GetMapping("/count")
    public CommonResp<Integer> count(){
        return new CommonResp<>(memberService.count());
    }
    @PostMapping("/register")
    public CommonResp<Long> register(@Valid MemberRegisterReq req){
        return new CommonResp<>(memberService.register(req));
    }
    @PostMapping("/send-code")
    public CommonResp<String> sendCode(@Valid @RequestBody MemberSendCodeReq req){
        memberService.sendCode(req);
        return new CommonResp<>();
    }
    @PostMapping("/login")
    public CommonResp<MemberLoginResp> login(@Valid @RequestBody MemberLoginReq req){
        return new CommonResp<>(memberService.login(req));
    }
}
