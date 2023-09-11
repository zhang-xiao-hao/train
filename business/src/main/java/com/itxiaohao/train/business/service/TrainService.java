package com.itxiaohao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxiaohao.train.business.domain.TrainCarriage;
import com.itxiaohao.train.common.exception.BusinessException;
import com.itxiaohao.train.common.exception.BusinessExceptionEnum;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.business.domain.Train;
import com.itxiaohao.train.business.domain.TrainExample;
import com.itxiaohao.train.business.mapper.TrainMapper;
import com.itxiaohao.train.business.req.TrainQueryReq;
import com.itxiaohao.train.business.req.TrainSaveReq;
import com.itxiaohao.train.business.resp.TrainQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainService{
    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);
    @Resource
    private TrainMapper trainMapper;

    public void save(TrainSaveReq req){
        Train train = BeanUtil.copyProperties(req, Train.class);
        DateTime now = DateTime.now();
        if (ObjectUtil.isNull(train.getId())){
            // 校验唯一键是否存在
            Train trainDB = selectByUnique(req.getCode());
            if (ObjectUtil.isNotEmpty(trainDB)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        } else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }
    }

    public PageResp<TrainQueryResp> queryList(TrainQueryReq req){
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");
        TrainExample.Criteria criteria = trainExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        PageInfo<Train> pageInfo = new PageInfo<>(trainList);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<TrainQueryResp> list = BeanUtil.copyToList(trainList, TrainQueryResp.class);
        PageResp<TrainQueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(Long id){
        trainMapper.deleteByPrimaryKey(id);
    }

    public List<TrainQueryResp> queryAll(){
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("code asc");
        List<Train> trainList = trainMapper.selectByExample(trainExample);
        return BeanUtil.copyToList(trainList, TrainQueryResp.class);
    }

    private Train selectByUnique(String code) {
        TrainExample trainExample = new TrainExample();
        trainExample.createCriteria()
                .andCodeEqualTo(code);
        List<Train> list = trainMapper.selectByExample(trainExample);
        if (CollUtil.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }
}
