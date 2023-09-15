package com.itxiaohao.train.member.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.member.req.TicketQueryReq;
import com.itxiaohao.train.member.req.TicketSaveReq;
import com.itxiaohao.train.member.resp.TicketQueryResp;
import com.itxiaohao.train.member.service.TicketService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/ticket")
public class TicketAdminController {
    @Resource
    private TicketService ticketService;

    @GetMapping("/query-list")
    public CommonResp<PageResp<TicketQueryResp>> query(@Valid TicketQueryReq req){
        return new CommonResp<>(ticketService.queryList(req));
    }
}
