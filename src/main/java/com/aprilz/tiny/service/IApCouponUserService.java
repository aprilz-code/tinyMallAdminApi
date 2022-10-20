package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApCouponUser;
import com.aprilz.tiny.vo.CouponVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 优惠券用户使用表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
public interface IApCouponUserService extends IService<ApCouponUser> {

    ApCouponUser queryOne(Long userId, Long couponId);

    IPage<CouponVo> queryAll(Long userId);

    Long countCoupon(Long couponId);

    Long countUserAndCoupon(Long userId, Long couponId);

    Page<ApCouponUser> queryList(Integer userId, Integer couponId, Short status, Integer page, Integer limit, String sort, String order);
}
