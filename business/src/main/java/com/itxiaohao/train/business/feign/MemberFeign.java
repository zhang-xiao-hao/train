package com.itxiaohao.train.business.feign;

import com.itxiaohao.train.common.req.MemberTicketReq;
import com.itxiaohao.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: itxiaohao
 * @date: 2023-09-15 14:55
 * @Description:
 */
@FeignClient(name = "member", url = "http://127.0.0.1:8001")
public interface MemberFeign {
    @GetMapping("/member/feign/ticket/save")
    CommonResp<Object> save(@RequestBody MemberTicketReq req);
}
