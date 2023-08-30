package com.itxiaohao.train.member.controller;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.member.req.MemberRegisterReq;
import com.itxiaohao.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CommonResp<Long> register(MemberRegisterReq req){
        return new CommonResp<>(memberService.register(req));
    }
}
