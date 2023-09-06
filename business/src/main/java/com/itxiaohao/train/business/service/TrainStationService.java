package com.itxiaohao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.business.domain.TrainStation;
import com.itxiaohao.train.business.domain.TrainStationExample;
import com.itxiaohao.train.business.mapper.TrainStationMapper;
import com.itxiaohao.train.business.req.TrainStationQueryReq;
import com.itxiaohao.train.business.req.TrainStationSaveReq;
import com.itxiaohao.train.business.resp.TrainStationQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainStationService{
    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);
    @Resource
    private TrainStationMapper trainStationMapper;

    public void save(TrainStationSaveReq req){
        TrainStation trainStation = BeanUtil.copyProperties(req, TrainStation.class);
        DateTime now = DateTime.now();
        if (ObjectUtil.isNull(trainStation.getId())){
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }
    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq req){
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("id desc");
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<TrainStation> trainStationList = trainStationMapper.selectByExample(trainStationExample);
        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStationList);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TrainStationQueryResp> list = BeanUtil.copyToList(trainStationList, TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(Long id){
        trainStationMapper.deleteByPrimaryKey(id);
    }
}
