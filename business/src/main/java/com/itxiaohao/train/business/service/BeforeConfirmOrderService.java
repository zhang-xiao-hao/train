package com.itxiaohao.train.business.service;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.itxiaohao.train.business.domain.ConfirmOrder;
import com.itxiaohao.train.business.dto.ConfirmOrderMQDto;
import com.itxiaohao.train.business.enums.ConfirmOrderStatusEnum;
import com.itxiaohao.train.business.enums.RocketMQTopicEnum;
import com.itxiaohao.train.business.mapper.ConfirmOrderMapper;
import com.itxiaohao.train.business.req.ConfirmOrderDoReq;
import com.itxiaohao.train.business.req.ConfirmOrderTicketReq;
import com.itxiaohao.train.common.context.LoginMemberContext;
import com.itxiaohao.train.common.exception.BusinessException;
import com.itxiaohao.train.common.exception.BusinessExceptionEnum;
import com.itxiaohao.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;
import java.util.List;


/**
 * @Author: itxiaohao
 * @date: 2023-09-30 15:24
 * @Description:
 */
public class BeforeConfirmOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);
    @Resource
    private SkTokenService skTokenService;
    @Resource
    private ConfirmOrderMapper confirmOrderMapper;
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    public Long beforeDoConfirm(ConfirmOrderDoReq req){
        req.setMemberId(LoginMemberContext.getId());
        // 校验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), LoginMemberContext.getMember().getId());
        if (validSkToken) {
            LOG.info("令牌校验通过");
        }else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }
        ConfirmOrder confirmOrder = new ConfirmOrder();
        DateTime now = DateTime.now();
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        List<ConfirmOrderTicketReq> tickets = req.getTickets();

        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(req.getMemberId());
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

        // 发送MQ
        ConfirmOrderMQDto confirmOrderMQDto = new ConfirmOrderMQDto();
        confirmOrderMQDto.setDate(req.getDate());
        confirmOrderMQDto.setTrainCode(req.getTrainCode());
        confirmOrderMQDto.setLogId(MDC.get("LOG_ID"));
        req.setLogId(MDC.get("LOG_ID"));
        String reqJson = JSON.toJSONString(confirmOrderMQDto);
        LOG.info("发送MQ排队购票消息：{}", reqJson);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getCode(), reqJson);
        return confirmOrder.getId();
    }
}
