package com.itxiaohao.train.business.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.esotericsoftware.minlog.Log;
import com.itxiaohao.train.business.req.ConfirmOrderDoReq;
import com.itxiaohao.train.business.req.ConfirmOrderQueryReq;
import com.itxiaohao.train.business.resp.ConfirmOrderQueryResp;
import com.itxiaohao.train.business.service.ConfirmOrderService;
import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/confirm-order")
public class ConfirmOrderController {
    @Resource
    private ConfirmOrderService confirmOrderService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @SentinelResource(value = "confirmOrderDo", blockHandler = "exceptionHandler")
    @PostMapping("/do")
    public CommonResp<Object> doConfirm(@Valid @RequestBody ConfirmOrderDoReq req){
        // 图像验证码校验
        String imageCodeToken = req.getImageCodeToken();
        String imageCode = req.getImageCode();
        String imageCodeRedis = stringRedisTemplate.opsForValue().get(imageCodeToken);
        Log.info("从redis中获取到验证码:{}", imageCodeRedis);
        if (ObjectUtil.isEmpty(imageCodeRedis)){
            return new CommonResp<>(false, "验证码已过期", null);
        }
        if (!imageCodeRedis.equalsIgnoreCase(imageCode)){
            return new CommonResp<>(false, "验证码不正确", null);
        }else {
            stringRedisTemplate.delete(imageCodeToken);
        }
        confirmOrderService.doConfirm(req);
        return new CommonResp<>();
    }

    public CommonResp<Object> exceptionHandler(ConfirmOrderDoReq req){
        CommonResp<Object> commonResp = new CommonResp<>();
        commonResp.setMessage("服务器忙，请稍后再试");
        return commonResp;
    }
}
