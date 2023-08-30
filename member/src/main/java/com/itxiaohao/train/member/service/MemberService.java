package com.itxiaohao.train.member.service;

import cn.hutool.core.collection.CollUtil;
import com.itxiaohao.train.member.domain.Member;
import com.itxiaohao.train.member.domain.MemberExample;
import com.itxiaohao.train.member.mapper.MemberMapper;
import com.itxiaohao.train.member.req.MemberRegisterReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: itxiaohao
 * @date: 2023-08-28 21:50
 * @Description:
 */
@Service
public class MemberService {
    @Resource
    private MemberMapper memberMapper;
    public int count(){
        return Math.toIntExact(memberMapper.countByExample(null));
    }

    public long register(MemberRegisterReq req){
        // 手机号是否被注册
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        if (CollUtil.isNotEmpty(list)){
            throw new RuntimeException("手机号已注册");
        }
        // 注册
        Member member = new Member();
        member.setId(System.currentTimeMillis());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
}
