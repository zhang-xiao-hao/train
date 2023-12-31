package com.itxiaohao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxiaohao.train.business.mapper.cust.SkTokenMapperCust;
import com.itxiaohao.train.common.exception.BusinessException;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.business.domain.SkToken;
import com.itxiaohao.train.business.domain.SkTokenExample;
import com.itxiaohao.train.business.mapper.SkTokenMapper;
import com.itxiaohao.train.business.req.SkTokenQueryReq;
import com.itxiaohao.train.business.req.SkTokenSaveReq;
import com.itxiaohao.train.business.resp.SkTokenQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkTokenService{
    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);
    @Resource
    private SkTokenMapper skTokenMapper;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    @Resource
    private DailyTrainStationService dailyTrainStationService;
    @Resource
    private SkTokenMapperCust skTokenMapperCust;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void save(SkTokenSaveReq req){
        SkToken skToken = BeanUtil.copyProperties(req, SkToken.class);
        DateTime now = DateTime.now();
        if (ObjectUtil.isNull(skToken.getId())){
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);
            skTokenMapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            skTokenMapper.updateByPrimaryKey(skToken);
        }
    }

    public boolean validSkToken(Date date, String trainCode, Long memberId){
        LOG.info("会员{}获取日期{}车次{}的令牌开始", memberId, DateUtil.formatDate(date), trainCode);
        String lockKey = "LOCK_SK_TOKEN"+"-"+DateUtil.formatDate(date) + "-" + trainCode + "-" + memberId;
        // 不主动释放锁，防止机器人刷票
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockKey, 5, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(setIfAbsent)){
            LOG.info("拿到令牌锁{}", lockKey);
        }else {
            LOG.info("没拿到令牌锁{}", lockKey);
            return false;
        }
        String skTokenCountKey = "SK_TOKEN_COUNT" + "-" + DateUtil.formatDate(date) + "-" + trainCode;
        Object skTokenCount = redisTemplate.opsForValue().get(skTokenCountKey);
        if (skTokenCount != null) {
            LOG.info("缓存中有该车次令牌的key：{}", skTokenCountKey);
            Long count = redisTemplate.opsForValue().decrement(skTokenCountKey, 1);
            if (count < 0L){
                LOG.info("获取令牌失败:{}", skTokenCountKey);
                return false;
            }else {
                LOG.info("获取令牌后，令牌余数：{}", count);
                redisTemplate.expire(skTokenCountKey, 60 ,TimeUnit.SECONDS);
                // 每获取5个令牌更新一次数据库
                if (count % 5 == 0){
                    skTokenMapperCust.decrease(date, trainCode, 5);
                }
                return true;
            }
        }else {
            LOG.info("缓存中没有该车次令牌的key：{}", skTokenCountKey);
            // 检查是否还有令牌
            SkTokenExample skTokenExample = new SkTokenExample();
            skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
            List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);
            if (CollUtil.isEmpty(skTokenList)){
                LOG.info("找不到日期{}车次{}的令牌记录", DateUtil.formatDate(date), trainCode);
                return false;
            }
            SkToken skToken = skTokenList.get(0);
            if (skToken.getCount() <= 0){
                LOG.info("日期{}车次{}的令牌余量为0", DateUtil.formatDate(date), trainCode);
                return false;
            }
            Integer count = skToken.getCount() - 1;
            skToken.setCount(count);
            LOG.info("将该车次令牌放入缓存中,key:{},count:{}", skTokenCountKey, count);
            redisTemplate.opsForValue().set(skTokenCountKey, String.valueOf(count), 60, TimeUnit.SECONDS);
            return true;
        }
        //        int updateCount = skTokenMapperCust.decrease(date, trainCode, 1);
//        if (updateCount > 0) {
//            return true;
//        }else {
//            return false;
//        }
    }

    public void genDaily(Date date, String trainCode){
        LOG.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        skTokenMapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("车次{}座位数{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countStation(date, trainCode);
        LOG.info("车次{}到站数{}", trainCode, stationCount);

        int count = (int)(seatCount * stationCount);
        LOG.info("车次{}初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);
        skTokenMapper.insert(skToken);
    }

    public PageResp<SkTokenQueryResp> queryList(SkTokenQueryReq req){
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.setOrderByClause("id desc");
        SkTokenExample.Criteria criteria = skTokenExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<SkToken> skTokenList = skTokenMapper.selectByExample(skTokenExample);
        PageInfo<SkToken> pageInfo = new PageInfo<>(skTokenList);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<SkTokenQueryResp> list = BeanUtil.copyToList(skTokenList, SkTokenQueryResp.class);
        PageResp<SkTokenQueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(Long id){
        skTokenMapper.deleteByPrimaryKey(id);
    }
}
