package com.itxiaohao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxiaohao.train.business.domain.TrainSeatExample;
import com.itxiaohao.train.business.enums.SeatColEnum;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.business.domain.TrainCarriage;
import com.itxiaohao.train.business.domain.TrainCarriageExample;
import com.itxiaohao.train.business.mapper.TrainCarriageMapper;
import com.itxiaohao.train.business.req.TrainCarriageQueryReq;
import com.itxiaohao.train.business.req.TrainCarriageSaveReq;
import com.itxiaohao.train.business.resp.TrainCarriageQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainCarriageService{
    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);
    @Resource
    private TrainCarriageMapper trainCarriageMapper;

    public void save(TrainCarriageSaveReq req){
        DateTime now = DateTime.now();
        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(req.getSeatType());
        req.setColCount(seatColEnums.size());
        req.setSeatCount(req.getColCount() * req.getRowCount());

        TrainCarriage trainCarriage = BeanUtil.copyProperties(req, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())){
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }
    }

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq req){
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        trainCarriageExample.setOrderByClause("id desc");
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        if (ObjectUtil.isNotEmpty(req.getTrainCode())) {
            criteria.andTrainCodeEqualTo(req.getTrainCode());
        }
        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainCarriage> trainCarriageList = trainCarriageMapper.selectByExample(trainCarriageExample);
        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriageList);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TrainCarriageQueryResp> list = BeanUtil.copyToList(trainCarriageList, TrainCarriageQueryResp.class);
        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(Long id){
        trainCarriageMapper.deleteByPrimaryKey(id);
    }

    public List<TrainCarriage> selectByTrainCode(String trainCode){
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        trainCarriageExample.setOrderByClause("`index` asc");
        criteria.andTrainCodeEqualTo(trainCode);
        return trainCarriageMapper.selectByExample(trainCarriageExample);
    }
}
