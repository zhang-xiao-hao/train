package com.itxiaohao.train.member.controller;

import com.itxiaohao.train.member.service.MemberService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: itxiaohao
 * @date: 2023-08-27 19:27
 * @Description:
 */
@RestController
public class MemberController {
    @Resource
    private MemberService memberService;
    @GetMapping("/count")
    public Integer count(){
        return memberService.count();
    }
}
