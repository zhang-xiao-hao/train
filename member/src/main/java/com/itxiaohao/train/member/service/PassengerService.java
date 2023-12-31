package com.itxiaohao.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxiaohao.train.common.context.LoginMemberContext;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.member.domain.Passenger;
import com.itxiaohao.train.member.domain.PassengerExample;
import com.itxiaohao.train.member.mapper.PassengerMapper;
import com.itxiaohao.train.member.req.PassengerQueryReq;
import com.itxiaohao.train.member.req.PassengerSaveReq;
import com.itxiaohao.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService{
    private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);
    @Resource
    private PassengerMapper passengerMapper;

    public void save(PassengerSaveReq req){
        Passenger passenger = BeanUtil.copyProperties(req, Passenger.class);
        DateTime now = DateTime.now();
        if (ObjectUtil.isNull(passenger.getId())){
            passenger.setId(SnowUtil.getSnowflakeNextId());
            // 从线程本地变量获取member id
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateByPrimaryKey(passenger);
        }
    }

    public PageResp<PassengerQueryResp> queryList(PassengerQueryReq req){
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("id desc");
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        // 有条件查询
        if (ObjectUtil.isNotNull(req.getMemberId())){
            criteria.andMemberIdEqualTo(req.getMemberId());
        }

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Passenger> passengerList = passengerMapper.selectByExample(passengerExample);
        PageInfo<Passenger> pageInfo = new PageInfo<>(passengerList);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<PassengerQueryResp> list = BeanUtil.copyToList(passengerList, PassengerQueryResp.class);
        PageResp<PassengerQueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(Long id){
        passengerMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查找我的所有乘客
     * @return
     */
    public List<PassengerQueryResp> queryMine(){
        PassengerExample passengerExample = new PassengerExample();
        passengerExample.setOrderByClause("name asc");
        passengerExample.createCriteria().andMemberIdEqualTo(LoginMemberContext.getId());
        List<Passenger> list = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(list, PassengerQueryResp.class);
    }
}
