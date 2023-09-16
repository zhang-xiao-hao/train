package com.itxiaohao.train.batch.feign;

import com.itxiaohao.train.common.resp.CommonResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * @Author: itxiaohao
 * @date: 2023-09-11 19:40
 * @Description:
 */
// nacos服务注册后，只需要写服务名即可远程调用
@FeignClient("business")
//@FeignClient(name = "business", url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {
    @GetMapping("/business/admin/daily-train/gen-daily/{date}")
    CommonResp<Object> genDaily(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);

    @GetMapping("/business/hello/sayHello")
    String sayHello();
}
