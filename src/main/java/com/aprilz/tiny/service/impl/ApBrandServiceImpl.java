package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApBrandMapper;
import com.aprilz.tiny.mbg.entity.ApAddress;
import com.aprilz.tiny.mbg.entity.ApBrand;
import com.aprilz.tiny.service.IApBrandService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 品牌商表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApBrandServiceImpl extends ServiceImpl<ApBrandMapper, ApBrand> implements IApBrandService {

    @Override
    public List<ApBrand> query(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApBrand> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApBrand::getDeleteFlag, false)
                .orderByDesc(ApBrand::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }

    @Override
    public Page<ApBrand> query(Integer page, Integer limit, String sort, String order) {
        Page<ApBrand> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return this.lambdaQuery().eq(ApBrand::getDeleteFlag, false)
                .page(pages);
    }

    @Override
    public Page<ApBrand> querySelective(String id, String name, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApBrand> query = this.lambdaQuery();
        if(StrUtil.isNotBlank(id)){
            query.eq(ApBrand::getId,Long.valueOf(id));
        }
        if(StrUtil.isNotBlank(name)){
            query.like(ApBrand::getName,name);
        }
        query.eq(ApBrand::getDeleteFlag,false);
        Page<ApBrand> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }
}
