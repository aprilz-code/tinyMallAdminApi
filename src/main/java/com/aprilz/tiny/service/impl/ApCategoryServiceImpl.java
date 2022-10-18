package com.aprilz.tiny.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aprilz.tiny.common.cache.Cache;
import com.aprilz.tiny.config.init.SystemConfig;
import com.aprilz.tiny.mapper.ApCategoryMapper;
import com.aprilz.tiny.mbg.entity.ApCategory;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.service.IApCategoryService;
import com.aprilz.tiny.service.IApGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 类目表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApCategoryServiceImpl extends ServiceImpl<ApCategoryMapper, ApCategory> implements IApCategoryService {


    @Autowired
    private Cache cache;

    @Resource
    private IApGoodsService goodsService;

    /**
     * @param
     * @return java.util.List<java.util.Map>
     * @author aprilz
     * @description 首页楼层商品查询
     * @since 2022/7/20
     **/
    @Override
    public List<Map> queryIndex() {
        Integer categoryNum = Optional.ofNullable(cache.getInteger(SystemConfig.AP_WX_INDEX_CATLOG_LIST)).orElse(4);

        List<Map> categoryList = new ArrayList<>();
        LambdaQueryWrapper<ApCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApCategory::getDeleteFlag, false).eq(ApCategory::getLevel, "L1")
                .ne(ApCategory::getName, "推荐").orderByAsc(ApCategory::getSortOrder).last("limit " + 0 + "," + categoryNum);
        List<ApCategory> list = this.list(queryWrapper);
        if (CollUtil.isEmpty(list)) {
            return categoryList;
        }

        list.stream().forEach(c -> {
            //查询子级分类
            queryWrapper.clear();
            queryWrapper.select(ApCategory::getId).eq(ApCategory::getDeleteFlag, false).eq(ApCategory::getPid, c.getId())
                    .orderByAsc(ApCategory::getSortOrder);
            List<ApCategory> childList = this.list(queryWrapper);
            List<Long> collect = childList.stream().map(ApCategory::getId).collect(Collectors.toList());
            List<ApGoods> categoryGoods;
            if (CollUtil.isEmpty(collect)) {
                categoryGoods = new ArrayList<>();
            } else {
                //商品挂在二级分类下
                Integer goodsNum = Optional.ofNullable(cache.getInteger(SystemConfig.AP_WX_INDEX_CATLOG_GOODS)).orElse(4);
                LambdaQueryWrapper<ApGoods> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ApGoods::getCategoryId, collect).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false)
                        .orderByAsc(ApGoods::getSortOrder).last("limit 0," + goodsNum);
                categoryGoods = goodsService.list(wrapper);
            }
            Map<String, Object> catGoods = new HashMap<>();
            catGoods.put("id", c.getId());
            catGoods.put("name", c.getName());
            catGoods.put("goodsList", categoryGoods);
            categoryList.add(catGoods);
        });
        return categoryList;
    }

    @Override
    public List<ApCategory> queryByPid(Long pid) {
        return this.lambdaQuery().eq(ApCategory::getPid,pid).eq(ApCategory::getDeleteFlag,false).list();
    }

    @Override
    public List<ApCategory> queryL1() {
        return this.lambdaQuery().eq(ApCategory::getLevel,"L1").eq(ApCategory::getDeleteFlag,false)
                .list();

    }
}
