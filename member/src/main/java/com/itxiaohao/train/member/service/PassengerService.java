package com.itxiaohao.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.itxiaohao.train.common.context.LoginMemberContext;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.member.domain.Passenger;
import com.itxiaohao.train.member.mapper.PassengerMapper;
import com.itxiaohao.train.member.req.PassengerSaveReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: itxiaohao
 * @date: 2023-09-02 19:41
 * @Description:
 */
@Service
public class PassengerService {
    @Resource
    private PassengerMapper passengerMapper;
    public void save(PassengerSaveReq req){
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        DateTime now = DateTime.now();
        passenger.setId(SnowUtil.getSnowflakeNextId());
        // 从线程本地变量获取member id
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setCreateTime(now);
        passenger.setUpdateTime(now);
        passengerMapper.insert(passenger);
    }
}
