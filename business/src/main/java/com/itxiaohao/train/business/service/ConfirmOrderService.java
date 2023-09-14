package com.itxiaohao.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        // 1省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 2保存确认订单表，状态初始
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
            for (int i = 0; i < 2; i++) {
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
            getSeat(date,
                    trainCode,
                    ticketReq0.getSeatTypeCode(),
                    ticketReq0.getSeat().split("")[0], //从A2得到A
                    offsetList);
        } else {
            LOG.info("本次购票没有指定选座");
            // 找座位（没有条件，一张一张选（有座位就要））
            for (ConfirmOrderTicketReq ticketReq: tickets) {
                getSeat(date,
                        trainCode,
                        ticketReq.getSeatTypeCode(),
                        null, null);
            }
        }

        // 5选座

            // 5.1一个车箱一个车箱的获取座位数据

            // 5.2挑选符合条件的座位，如果这个车箱不满足，则进入下个车箱（多个选座应该在同一个车厢）

        // 6选中座位后事务处理：

            // 6.1座位表修改售卖情况sell；
            // 6.2余票详情表修改余票；
            // 6.3为会员增加购票记录
            // 6.4更新确认订单为成功
    }

    private void getSeat(Date date, String trainCode, String seatType, String col, List<Integer> list){
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());
        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数:{}", dailyTrainCarriage.getIndex(), seatList.size());
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
