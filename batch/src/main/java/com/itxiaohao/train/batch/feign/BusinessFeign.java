package com.itxiaohao.train.batch.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: itxiaohao
 * @date: 2023-09-11 19:40
 * @Description:
 */
@FeignClient(name = "business", url = "http://127.0.0.1:8002/business")
public interface BusinessFeign {

}
