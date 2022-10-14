package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApAdMapper;
import com.aprilz.tiny.mbg.entity.ApAd;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.service.IApAdService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 广告表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-18
 */
@Service
public class ApAdServiceImpl extends ServiceImpl<ApAdMapper, ApAd> implements IApAdService {


    @Override
    public Page<ApAd> querySelective(String name, String content, Integer page, Integer limit, String sort, String order) {
        LambdaQueryWrapper<ApAd> queryWrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(name)) {
            queryWrapper.like(ApAd::getName, name);
        }

        if (StrUtil.isNotBlank(content)) {
            queryWrapper.like(ApAd::getContent, content);
        }
        queryWrapper.eq(ApAd::getDeleteFlag,false);

        Page<ApAd> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));

        return this.page(pages, queryWrapper);
    }
}
