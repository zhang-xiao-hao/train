package com.itxiaohao.train.business.service;


import com.itxiaohao.train.business.domain.*;
import com.itxiaohao.train.business.enums.ConfirmOrderStatusEnum;
import com.itxiaohao.train.business.feign.MemberFeign;
import com.itxiaohao.train.business.mapper.ConfirmOrderMapper;
import com.itxiaohao.train.business.mapper.DailyTrainSeatMapper;
import com.itxiaohao.train.business.mapper.cust.DailyTrainTicketMapperCust;
import com.itxiaohao.train.business.req.ConfirmOrderTicketReq;
import com.itxiaohao.train.common.context.LoginMemberContext;
import com.itxiaohao.train.common.req.MemberTicketReq;
import com.itxiaohao.train.common.resp.CommonResp;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(AfterConfirmOrderService.class);
    @Resource
    private DailyTrainSeatMapper dailyTrainSeatMapper;
    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;
    @Resource
    private MemberFeign memberFeign;
    @Resource
    private ConfirmOrderMapper confirmOrderMapper;
    /***
     *     选中座位后事务处理：
     *        座位表修改售卖情况sell
     *        余票详情表修改余票
     *        为会员增加购票记录
     *        更新确认订单为成功
     */
//    @Transactional
    @GlobalTransactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket,
                               List<DailyTrainSeat> finalSeatList,
                               List<ConfirmOrderTicketReq> tickets,
                               ConfirmOrder confirmOrder) throws Exception {
        LOG.info("seata全局事务ID：{}", RootContext.getXID());
        // 更新sell列
        for (int j = 0; j < finalSeatList.size(); j++) {
            DailyTrainSeat dailyTrainSeat = finalSeatList.get(j);
            DailyTrainSeat seatForUpdate = new DailyTrainSeat();
            seatForUpdate.setId(dailyTrainSeat.getId());
            seatForUpdate.setSell(dailyTrainSeat.getSell());
            seatForUpdate.setUpdateTime(new Date());
            // updateByPrimaryKeySelective只更新部分列
            dailyTrainSeatMapper.updateByPrimaryKeySelective(seatForUpdate);

            // 计算这个站卖出去后，影响了哪些站的余票库存
            // 参考2.3节分析
            // 影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            // 例：假设10个站，本次买4-7站(从0站开始)
            // 原售：001000001
            // 购买：000011100
            // 新售：001011101
            // 影响：XXX11111X
            // startIndex=4
            Integer startIndex = dailyTrainTicket.getStartIndex();
            // endIndex=7
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] chars = seatForUpdate.getSell().toCharArray();
            // maxStartIndex=6
            Integer maxStartIndex = endIndex - 1;
            Integer minStartIndex = 0;
            // minStartIndex=3
            for (int i = startIndex - 1; i >= 0 ; i--) {
                char aChar = chars[i];
                if (aChar == '1'){
                    minStartIndex = i + 1;
                    break;
                }
            }
            // 3-6
            LOG.info("影响出发站区间：{}-{}", minStartIndex, maxStartIndex);

            // minEndIndex=5
            Integer minEndIndex = startIndex + 1;
            Integer maxEndIndex = seatForUpdate.getSell().length();
            // maxEndIndex=8
            for (int i = endIndex; i < seatForUpdate.getSell().length(); i++) {
                char aChar = chars[i];
                if (aChar == '1'){
                    maxEndIndex = i;
                    break;
                }
            }
            // 5-8 （出发站为3，4，5，6，并且到达站为5，6，7，8的余票都要-1）
            LOG.info("影响到达站区间：{}-{}", minEndIndex, maxEndIndex);
            // 更新库存
            dailyTrainTicketMapperCust.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);

            // 调用会员服务接口，为会员增加一张车票
            MemberTicketReq memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(LoginMemberContext.getId());
            memberTicketReq.setPassengerId(tickets.get(j).getPassengerId());
            memberTicketReq.setPassengerName(tickets.get(j).getPassengerName());
            memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainSeat.getTrainCode());
            memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
            memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
            memberTicketReq.setStartStation(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
            CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
            LOG.info("调用member接口，返回:{}", commonResp);

            // 更新订单状态为成功
            ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderForUpdate.setUpdateTime(new Date());
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);
            // 测试seata分布式事务
//            if (1==1){
//                throw new Exception("测试异常");
//            }
        }
    }
}
