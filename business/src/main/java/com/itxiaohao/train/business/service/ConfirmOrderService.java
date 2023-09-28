package com.itxiaohao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxiaohao.train.business.domain.*;
import com.itxiaohao.train.business.enums.ConfirmOrderStatusEnum;
import com.itxiaohao.train.business.enums.SeatColEnum;
import com.itxiaohao.train.business.enums.SeatTypeEnum;
import com.itxiaohao.train.business.req.ConfirmOrderTicketReq;
import com.itxiaohao.train.common.context.LoginMemberContext;
import com.itxiaohao.train.common.exception.BusinessException;
import com.itxiaohao.train.common.exception.BusinessExceptionEnum;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.business.mapper.ConfirmOrderMapper;
import com.itxiaohao.train.business.req.ConfirmOrderQueryReq;
import com.itxiaohao.train.business.req.ConfirmOrderDoReq;
import com.itxiaohao.train.business.resp.ConfirmOrderQueryResp;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ConfirmOrderService{
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);
    @Resource
    private ConfirmOrderMapper confirmOrderMapper;
    @Resource
    private DailyTrainTicketService dailyTrainTicketService;
    @Resource
    private DailyTrainCarriageService dailyTrainCarriageService ;
    @Resource
    private DailyTrainSeatService dailyTrainSeatService;
    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SkTokenService skTokenService;

    @Resource
    private RedissonClient redissonClient;

    public void save(ConfirmOrderDoReq req){
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(req, ConfirmOrder.class);
        DateTime now = DateTime.now();
        if (ObjectUtil.isNull(confirmOrder.getId())){
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }
    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req){
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.setOrderByClause("id desc");
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrderList = confirmOrderMapper.selectByExample(confirmOrderExample);
        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrderList);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<ConfirmOrderQueryResp> list = BeanUtil.copyToList(confirmOrderList, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(Long id){
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    public void doConfirm(ConfirmOrderDoReq req){
        // 校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getMember().getId());
        if (validSkToken) {
            LOG.info("令牌校验通过");
        }else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        // 分布式锁解决超卖问题
        String locKey = req.getDate() + ":" + req.getTrainCode();
        RLock lock = null;
        try{
            // 使用redisson，自带看门狗
            lock = redissonClient.getLock(locKey);
            boolean tryLock = lock.tryLock(0, TimeUnit.SECONDS);
            if (!tryLock) {
                LOG.info("未拿到分布式锁");
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }
            // 1省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过
            // 2保存确认订单表，状态"初始"
            ConfirmOrder confirmOrder = new ConfirmOrder();
            DateTime now = DateTime.now();
            Date date = req.getDate();
            String trainCode = req.getTrainCode();
            String start = req.getStart();
            String end = req.getEnd();
            List<ConfirmOrderTicketReq> tickets = req.getTickets();

            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setMemberId(LoginMemberContext.getId());
            confirmOrder.setDate(date);
            confirmOrder.setTrainCode(trainCode);
            confirmOrder.setStart(start);
            confirmOrder.setEnd(end);
            confirmOrder.setDailyTrainTicketId(req.getDailyTrainTicketId());
            confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrder.setTickets(JSON.toJSONString(tickets));
            confirmOrderMapper.insert(confirmOrder);

            // 3查出余票记录，需要得到真实的库存
            DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
            LOG.info("查出余票记录：{}", dailyTrainTicket);
            // 4预扣减余票数量，并判断余票是否足够
            reduceTickets(req, dailyTrainTicket);
            // 最终的选座结果
            List<DailyTrainSeat> finalSeatList = new ArrayList<>();
            // 计算相对于第一个座位的偏移值
            // 比如选择的是C1 D2,则偏移值是：[0,5]
            ConfirmOrderTicketReq ticketReq0 = tickets.get(0);
            if (StrUtil.isNotBlank(ticketReq0.getSeat())){
                LOG.info("本次购票有指定选座");
                // 查出本次选座的座位类型都有哪些列，用于计算所选座位与第一个座位的偏移值
                List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(ticketReq0.getSeatTypeCode());
                LOG.info("本次选座的座位类型包含的列：{}", colEnumList);

                // 组成和前端两排的选座一样的列表，用于参照的座位列表，例referSeatList = {A1, C1, D1, F1, A2, C2, D2, F2}
                List<String> referSeatList = new ArrayList<>();
                for (int i = 1; i <= 2; i++) {
                    for (SeatColEnum seatColEnum: colEnumList){
                        referSeatList.add(seatColEnum.getCode() + i);
                    }
                }
                LOG.info("本次选座的用于参照的座位列表：{}", referSeatList);
                // 先计算绝对偏移值，即：在参照座位列表中的位置
                List<Integer> absOffsetList = new ArrayList<>();
                for (ConfirmOrderTicketReq ticketReq: tickets) {
                    int index = referSeatList.indexOf(ticketReq.getSeat());
                    absOffsetList.add(index);
                }
                LOG.info("计算得到所有座位的绝对偏移值：{}", absOffsetList);
                // 计算相对偏移值(相对于第一个座位)
                List<Integer> offsetList = new ArrayList<>();
                for (Integer index : absOffsetList) {
                    int offset = index - absOffsetList.get(0);
                    offsetList.add(offset);
                }
                LOG.info("计算得到所有座位的相对偏移值：{}", offsetList);
                // 找符合条件的座位（多个座位一起选）
                getSeat(finalSeatList,
                        date,
                        trainCode,
                        ticketReq0.getSeatTypeCode(),
                        ticketReq0.getSeat().split("")[0], //从A2得到A
                        offsetList,
                        dailyTrainTicket.getStartIndex(),
                        dailyTrainTicket.getEndIndex());
            } else {
                LOG.info("本次购票没有指定选座");
                // 找座位（没有条件，一个一个选（有座位就要））
                for (ConfirmOrderTicketReq ticketReq: tickets) {
                    getSeat(finalSeatList,
                            date,
                            trainCode,
                            ticketReq.getSeatTypeCode(),
                            null, null,
                            dailyTrainTicket.getStartIndex(),
                            dailyTrainTicket.getEndIndex()
                    );
                }
            }
            LOG.info("最终选座：{}", finalSeatList);
            try{
                // 座位表修改售卖情况sell；
                afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder);
            }catch (Exception e){
                LOG.error("保存购票信息失败", e);
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
            }
        } catch (InterruptedException e){
          LOG.info("购票异常", e);
        } finally {
            if (null != lock && lock.isHeldByCurrentThread()){
                lock.unlock();
                LOG.info("分布式锁已释放");
            }
        }
    }

    /**
     * 选座
     * @param finalSeatList
     * @param date
     * @param trainCode
     * @param seatType
     * @param column
     * @param offsetList
     * @param startIndex
     * @param endIndex
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList,
                         Date date,
                         String trainCode,
                         String seatType,
                         String column,
                         List<Integer> offsetList,
                         Integer startIndex,
                         Integer endIndex){
        // 临时选中的座位
        List<DailyTrainSeat> getSeatList;
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());
        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            // 初始化（清空）
            getSeatList = new ArrayList<>();
            // 指定车厢的座位
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数:{}", dailyTrainCarriage.getIndex(), seatList.size());
            for (DailyTrainSeat dailyTrainSeat: seatList) {
                // 当前座位的座位号（从1开始，并且是连续的）
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();

                // 判断当前座位是否被选过了（不指定座位选座时），选中过则跳过该座位
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())){
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LOG.info("座位{}被选中过，不能重复选，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断column,有值的话要比对列号
                String col = dailyTrainSeat.getCol();
                if (StrUtil.isBlank(column)){
                    LOG.info("没有指定座位");
                }else {
                    if (!column.equals(col)){
                        LOG.info("座位{}列号不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }
                boolean isChosen = callSell(dailyTrainSeat, startIndex-1, endIndex-1);
                if (isChosen){
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                }else {
                    continue;
                }
                // 根据offset选剩下的座位
                boolean isGetAllOffsetSet = true;
                if (CollUtil.isNotEmpty(offsetList)){
                    LOG.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int i = 1; i < offsetList.size(); i++) {
                        Integer offset = offsetList.get(i);
                        // 座位号从1开始，seatList的索引从0开始（seatIndex=seatList[0]=1）
                        // seatList[seatIndex + offset - 1] = 偏移座位号
                        int nextIndex = seatIndex + offset - 1;
                        // 有选座时，一定是在同一个车厢
                        if (nextIndex >= seatList.size()){
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车厢的座位数", nextIndex);
                            // 跳出第二层层循环(座位已经超出了车厢，应从新选择第一个座位，并循环选择偏移座位)
                            isGetAllOffsetSet = false;
                            break;
                        }
                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        boolean isChosenNext = callSell(nextDailyTrainSeat, startIndex-1, endIndex-1);
                        if (isChosenNext){
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        }else {
                            LOG.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSet = false;
                            break;
                        }
                    }
                }
                if (!isGetAllOffsetSet){
                    // 清空临时选中的座位
                    getSeatList = new ArrayList<>();
                    continue;
                }

                // 保存选好的座位
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001（0到1站和4到站都卖了），本次购买区间站1-4，则区间已售000
     * 全部是0，表示这个区间可买：只要有1，就表示区间内某站已售过票
     *
     * 选中后，要计算购票后的sell，则sell=11111
     * 方案：先构造被刺购票的售卖信息01110，再与原sell=10001按位或
     * @param dailyTrainSeat
     */
    private boolean callSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex){
        // 00001
        String sell = dailyTrainSeat.getSell();
        // 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0){
            LOG.info("座位{}在本次车站区间{}-{}已经售过票，不可选择该座", dailyTrainSeat.getCarriageSeatIndex(),
                    startIndex, endIndex);
            return false;
        }else{
            LOG.info("座位{}在本次车站区间{}-{}未售过票，可选择该座", dailyTrainSeat.getCarriageSeatIndex(),
                    startIndex, endIndex);
            // 111
            String curSell = sellPart.replace('0', '1');
            // 0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());
            // 01111
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            // 1111
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息：{}，车站信息：{}-{}，即：{}，最终售票信息：{}",
                    dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;
        }
    }
    private static void reduceTickets(ConfirmOrderDoReq req, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq: req.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum){
                case YDZ ->{
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ ->{
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setEdz(countLeft);
                }
                case RW ->{
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setRw(countLeft);
                }
                case YW ->{
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0){
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYw(countLeft);
                }
            }
            
        }
    }
}
