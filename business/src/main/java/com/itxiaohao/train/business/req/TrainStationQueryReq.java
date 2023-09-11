package com.itxiaohao.train.business.req;

import com.itxiaohao.train.common.req.PageReq;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TrainStationQueryReq extends PageReq {
    private String trainCode;
}
