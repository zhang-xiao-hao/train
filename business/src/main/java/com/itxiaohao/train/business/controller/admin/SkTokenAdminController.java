package com.itxiaohao.train.business.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.business.req.SkTokenQueryReq;
import com.itxiaohao.train.business.req.SkTokenSaveReq;
import com.itxiaohao.train.business.resp.SkTokenQueryResp;
import com.itxiaohao.train.business.service.SkTokenService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/sk-token")
public class SkTokenAdminController {
    @Resource
    private SkTokenService skTokenService;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody SkTokenSaveReq req){
        skTokenService.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<SkTokenQueryResp>> query(@Valid SkTokenQueryReq req){
        return new CommonResp<>(skTokenService.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        skTokenService.delete(id);
        return new CommonResp<>();
    }
}
