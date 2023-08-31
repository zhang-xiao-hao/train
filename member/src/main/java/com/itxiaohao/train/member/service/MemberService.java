package com.itxiaohao.train.member.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.itxiaohao.train.common.exception.BusinessException;
import com.itxiaohao.train.common.exception.BusinessExceptionEnum;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.member.domain.Member;
import com.itxiaohao.train.member.domain.MemberExample;
import com.itxiaohao.train.member.mapper.MemberMapper;
import com.itxiaohao.train.member.req.MemberRegisterReq;
import com.itxiaohao.train.member.req.MemberSendCodeReq;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: itxiaohao
 * @date: 2023-08-28 21:50
 * @Description:
 */
@Service
public class MemberService {
    private static final Logger LOG = LoggerFactory.getLogger(MemberService.class);
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
            LOG.info("手机号已注册");
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }
        // 注册
        Member member = new Member();
        // 雪花算法生成id
        member.setId(SnowUtil.getSnowflakeNextId());
        member.setMobile(mobile);
        memberMapper.insert(member);
        return member.getId();
    }
    public void sendCode(MemberSendCodeReq req){
        // 手机号是否被注册
        String mobile = req.getMobile();
        MemberExample memberExample = new MemberExample();
        memberExample.createCriteria().andMobileEqualTo(mobile);
        List<Member> list = memberMapper.selectByExample(memberExample);
        // 注册
        if (CollUtil.isNotEmpty(list)){
            LOG.info("手机号不存在，插入一条记录");
            Member member = new Member();
            // 雪花算法生成id
            member.setId(SnowUtil.getSnowflakeNextId());
            member.setMobile(mobile);
            memberMapper.insert(member);
        } else {
            LOG.info("手机号存在，不插入记录");
        }
        // 生成验证码
        String code = RandomUtil.randomString(4);
        LOG.info("生成短信验证码：{}", code);
        // 保存短信记录表：手机号、短信验证码、有效期、是否已使用、业务类型、发送时间、使用时间。。。
        LOG.info("保存短信记录表");
        // 对接短信通道，发送短信
        LOG.info("对接短信通道");
    }
}
