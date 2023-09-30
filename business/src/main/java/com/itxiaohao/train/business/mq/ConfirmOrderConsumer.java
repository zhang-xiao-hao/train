package com.itxiaohao.train.business.mq;

import com.alibaba.fastjson.JSON;
import com.itxiaohao.train.business.dto.ConfirmOrderMQDto;
import com.itxiaohao.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

/**
 * @Author: itxiaohao
 * @date: 2023-09-30 15:58
 * @Description: mq消费者
 */
@Service
@RocketMQMessageListener(consumerGroup = "default", topic = "CONFIRM_ORDER")
public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderConsumer.class);
    @Resource
    private ConfirmOrderService confirmOrderService;
    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        ConfirmOrderMQDto dto = JSON.parseObject(new String(body), ConfirmOrderMQDto.class);
        MDC.put("LOG_ID", dto.getLogId());
        LOG.info("mq接受到消息:{}", new String(body));
        confirmOrderService.doConfirm(dto);
    }
}
