package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApAddressMapper;
import com.aprilz.tiny.mbg.entity.ApAd;
import com.aprilz.tiny.mbg.entity.ApAddress;
import com.aprilz.tiny.service.IApAddressService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 收货地址表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-18
 */
@Service
public class ApAddressServiceImpl extends ServiceImpl<ApAddressMapper, ApAddress> implements IApAddressService {

    @Override
    @Transactional
    public void resetDefault(Long userId) {
        this.lambdaUpdate().set(ApAddress::getIsDefault, false).eq(ApAddress::getUserId, userId).eq(ApAddress::getDeleteFlag, false)
                .update();
    }

    @Override
    public ApAddress query(Long userId, Long addressId) {
        return this.lambdaQuery().eq(ApAddress::getId, addressId).eq(ApAddress::getUserId, userId)
                .eq(ApAddress::getDeleteFlag, false).one();

    }

    @Override
    public ApAddress findDefault(Long userId) {
        return this.lambdaQuery().eq(ApAddress::getUserId, userId)
                .eq(ApAddress::getIsDefault, true).eq(ApAddress::getDeleteFlag, false)
                .last("limit 1").one();

    }

    @Override
    public Page<ApAddress> querySelective(Integer userId, String name, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApAddress> query = this.lambdaQuery();
        if(Objects.nonNull(userId)){
            query.eq(ApAddress::getUserId,userId);
        }

        if(StrUtil.isNotBlank(name)){
            query.like(ApAddress::getName,name);
        }
        query.eq(ApAddress::getDeleteFlag,false);

        Page<ApAddress> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }
}
