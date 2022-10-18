package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApGoodsMapper;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.service.IApGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品基本信息表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApGoodsServiceImpl extends ServiceImpl<ApGoodsMapper, ApGoods> implements IApGoodsService {

    @Override
    public List<ApGoods> queryByNew(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoods::getIsNew, true).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false)
                .orderByDesc(ApGoods::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }

    @Override
    public List<ApGoods> queryByHot(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoods::getIsHot, true).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false)
                .orderByDesc(ApGoods::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }

    @Override
    public Page<ApGoods> querySelective(Integer categoryId, Integer brandId, String keyword, Boolean isHot, Boolean isNew, Integer page, Integer limit, String sort, String order) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(categoryId) && categoryId != 0) {
            queryWrapper.eq(ApGoods::getCategoryId, categoryId);
        }
        if (Objects.nonNull(brandId)) {
            queryWrapper.eq(ApGoods::getBrandId, brandId);
        }
        if (BooleanUtil.isTrue(isHot)) {
            queryWrapper.eq(ApGoods::getIsHot, isHot);
        }

        if (BooleanUtil.isTrue(isNew)) {
            queryWrapper.eq(ApGoods::getIsNew, isNew);
        }

        //搜索keywords以及goodName
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(qw -> qw.like(ApGoods::getName, keyword).or().like(ApGoods::getKeywords, keyword));
        }
        queryWrapper.eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false);

        Page<ApGoods> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));

        return this.baseMapper.selectPage(pages, queryWrapper);

    }

    @Override
    public List<Long> getCategoryIds(Integer categoryId, Integer brandId, String keyword, Boolean isHot, Boolean isNew) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(categoryId) && categoryId != 0) {
            queryWrapper.eq(ApGoods::getCategoryId, categoryId);
        }
        if (Objects.nonNull(brandId)) {
            queryWrapper.eq(ApGoods::getBrandId, brandId);
        }
        if (BooleanUtil.isTrue(isHot)) {
            queryWrapper.eq(ApGoods::getIsHot, isHot);
        }

        if (BooleanUtil.isTrue(isNew)) {
            queryWrapper.eq(ApGoods::getIsNew, isNew);
        }

        //搜索keywords以及goodName
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(qw -> qw.like(ApGoods::getName, keyword).or().like(ApGoods::getKeywords, keyword));
        }
        queryWrapper.eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false);

        queryWrapper.select(ApGoods::getCategoryId);
        return this.list(queryWrapper).stream().map(ApGoods::getCategoryId).collect(Collectors.toList());
    }
}
