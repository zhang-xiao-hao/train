package com.itxiaohao.train.member.controller.feign;

import com.itxiaohao.train.common.req.MemberTicketReq;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: itxiaohao
 * @date: 2023-09-15 14:41
 * @Description:
 */
@RestController
@RequestMapping("/feign/ticket")
public class FeignTicketController {
    @Resource
    private TicketService ticketService;
    public CommonResp<Object> save(@Valid @RequestBody MemberTicketReq req) throws Exception{
        ticketService.save(req);
        return new CommonResp<>();
    }
}
