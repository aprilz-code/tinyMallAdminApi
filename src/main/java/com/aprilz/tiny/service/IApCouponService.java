package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApCart;
import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.aprilz.tiny.vo.CouponVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 优惠券信息及规则表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-14
 */
public interface IApCouponService extends IService<ApCoupon> {

    void assignForRegister(Long id);

    IPage<CouponVo> queryMyList(Integer couponId, Short status, Integer page, Integer size, String sort, String order);

    List<ApCoupon> queryAvailableList(Long userId);

    ApCoupon checkCoupon(Long userId, Long couponId, Long userCouponId, BigDecimal checkedGoodsPrice, List<ApCart> checkedGoodsList);

    Page<ApCoupon> queryList(Integer page, Integer size, String sort, String order);

    ApCoupon findByCode(String code);

    String generateCode();

    Page<ApCoupon> querySelective(String name, Short type, Short status, Integer page, Integer limit, String sort, String order);
}
