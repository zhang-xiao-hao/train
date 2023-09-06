package com.itxiaohao.train.${module}.controller.admin;

import com.itxiaohao.train.common.resp.CommonResp;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.${module}.req.${Domain}QueryReq;
import com.itxiaohao.train.${module}.req.${Domain}SaveReq;
import com.itxiaohao.train.${module}.resp.${Domain}QueryResp;
import com.itxiaohao.train.${module}.service.${Domain}Service;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/${do_main}")
public class ${Domain}AdminController {
    @Resource
    private ${Domain}Service ${domain}Service;

    @PostMapping("/save")
    public CommonResp<Object> save(@Valid @RequestBody ${Domain}SaveReq req){
        ${domain}Service.save(req);
        return new CommonResp<>();
    }
    @GetMapping("/query-list")
    public CommonResp<PageResp<${Domain}QueryResp>> query(@Valid ${Domain}QueryReq req){
        return new CommonResp<>(${domain}Service.queryList(req));
    }
    @DeleteMapping("/delete/{id}")
    public CommonResp<Object> delete(@PathVariable("id") Long id){
        ${domain}Service.delete(id);
        return new CommonResp<>();
    }
}
