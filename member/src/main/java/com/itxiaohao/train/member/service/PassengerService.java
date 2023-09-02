package com.itxiaohao.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.itxiaohao.train.common.context.LoginMemberContext;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.member.domain.Passenger;
import com.itxiaohao.train.member.domain.PassengerExample;
import com.itxiaohao.train.member.mapper.PassengerMapper;
import com.itxiaohao.train.member.req.PassengerQueryReq;
import com.itxiaohao.train.member.req.PassengerSaveReq;
import com.itxiaohao.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<PassengerQueryResp> queryList(PassengerQueryReq req){
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        // 有条件查询
        if (ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
    }
}
