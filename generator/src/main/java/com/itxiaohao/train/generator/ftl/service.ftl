package com.itxiaohao.train.${module}.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itxiaohao.train.common.resp.PageResp;
import com.itxiaohao.train.common.util.SnowUtil;
import com.itxiaohao.train.${module}.domain.${Domain};
import com.itxiaohao.train.${module}.domain.${Domain}Example;
import com.itxiaohao.train.${module}.mapper.${Domain}Mapper;
import com.itxiaohao.train.${module}.req.${Domain}QueryReq;
import com.itxiaohao.train.${module}.req.${Domain}SaveReq;
import com.itxiaohao.train.${module}.resp.${Domain}QueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ${Domain}Service{
    private static final Logger LOG = LoggerFactory.getLogger(${Domain}Service.class);
    @Resource
    private ${Domain}Mapper ${domain}Mapper;

    public void save(${Domain}SaveReq req){
        ${Domain} ${domain} = BeanUtil.copyProperties(req, ${Domain}.class);
        DateTime now = DateTime.now();
        if (ObjectUtil.isNull(${domain}.getId())){
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.insert(${domain});
        } else {
            ${domain}.setUpdateTime(now);
            ${domain}Mapper.updateByPrimaryKey(${domain});
        }
    }

    public PageResp<${Domain}QueryResp> queryList(${Domain}QueryReq req){
        ${Domain}Example ${domain}Example = new ${Domain}Example();
        ${domain}Example.setOrderByClause("id desc");
        ${Domain}Example.Criteria criteria = ${domain}Example.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<${Domain}> ${domain}List = ${domain}Mapper.selectByExample(${domain}Example);
        PageInfo<${Domain}> pageInfo = new PageInfo<>(${domain}List);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());
        List<${Domain}QueryResp> list = BeanUtil.copyToList(${domain}List, ${Domain}QueryResp.class);
        PageResp<${Domain}QueryResp> pageResp = new PageResp<>();
        pageResp.setList(list);
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    public void delete(Long id){
        ${domain}Mapper.deleteByPrimaryKey(id);
    }
}
