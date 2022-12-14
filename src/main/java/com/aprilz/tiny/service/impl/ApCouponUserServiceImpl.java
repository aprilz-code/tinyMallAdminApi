package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mall.CouponUserConstant;
import com.aprilz.tiny.mapper.ApCouponUserMapper;
import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.aprilz.tiny.mbg.entity.ApCouponUser;
import com.aprilz.tiny.service.IApCouponUserService;
import com.aprilz.tiny.vo.CouponVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 优惠券用户使用表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
@Service
public class ApCouponUserServiceImpl extends ServiceImpl<ApCouponUserMapper, ApCouponUser> implements IApCouponUserService {

    @Override
    public ApCouponUser queryOne(Long userId, Long couponId) {
        return this.lambdaQuery().eq(ApCouponUser::getCouponId, couponId)
                .eq(ApCouponUser::getUserId, userId)
                .eq(ApCouponUser::getDeleteFlag, false)
                .last("limit 1").one();
    }

    @Override
    public IPage<CouponVo> queryAll(Long userId) {
        Page<CouponVo> objectPage = PageUtil.initPage(new PageVO().setOrder("desc").setSort("create_time"));
        QueryWrapper<CouponVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", CouponUserConstant.STATUS_USABLE);
        queryWrapper.eq("user_id", userId);
        return this.baseMapper.pageCoupon(objectPage, queryWrapper);

    }

    @Override
    public Long countCoupon(Long couponId) {
        return this.lambdaQuery().eq(ApCouponUser::getCouponId, couponId).eq(ApCouponUser::getDeleteFlag, false).count();
    }

    @Override
    public Long countUserAndCoupon(Long userId, Long couponId) {
        return this.lambdaQuery().eq(ApCouponUser::getUserId, userId).eq(ApCouponUser::getCouponId, couponId).eq(ApCouponUser::getDeleteFlag, false).count();

    }

    @Override
    public Page<ApCouponUser> queryList(Integer userId, Integer couponId, Short status, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApCouponUser> query = this.lambdaQuery();
        if(Objects.nonNull(userId)){
            query.eq(ApCouponUser::getUserId, userId);
        }
        if(Objects.nonNull(couponId)){
            query.eq(ApCouponUser::getCouponId, couponId);
        }

        if(Objects.nonNull(status)){
            query.eq(ApCouponUser::getStatus, status);
        }
        query.eq(ApCouponUser::getDeleteFlag, false);
        Page<ApCouponUser> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }
}
