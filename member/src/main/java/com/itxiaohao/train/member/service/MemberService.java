package com.itxiaohao.train.member.service;

import com.itxiaohao.train.member.mapper.MemberMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
}
