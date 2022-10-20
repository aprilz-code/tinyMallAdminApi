package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mall.CouponConstant;
import com.aprilz.tiny.mbg.entity.ApCoupon;
import com.aprilz.tiny.mbg.entity.ApCouponUser;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApCouponService;
import com.aprilz.tiny.service.IApCouponUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/coupon")
@Validated
@Slf4j
@Api("优惠券管理")
public class ApCouponController {

    @Autowired
    private IApCouponService couponService;
    @Autowired
    private IApCouponUserService couponUserService;

    @PreAuthorize("hasAuthority('admin:coupon:list')")
    @ApiOperation("推广管理-优惠券管理-查询")
    @GetMapping("/list")
    public CommonResult list(String name, Short type, Short status,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApCoupon> couponList = couponService.querySelective(name, type, status, page, limit, sort, order);
        return CommonResult.success(couponList);
    }

    @PreAuthorize("hasAuthority('admin:coupon:listuser')")
    @ApiOperation("推广管理-优惠券管理-查询用户")
    @GetMapping("/listuser")
    public CommonResult listuser(Integer userId, Integer couponId, Short status,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(defaultValue = "create_time") String sort,
                                 @RequestParam(defaultValue = "desc") String order) {
        Page<ApCouponUser> couponList = couponUserService.queryList(userId, couponId, status, page,
                limit, sort, order);
        return CommonResult.success(couponList);
    }


    @PreAuthorize("hasAuthority('admin:coupon:create')")
    @ApiOperation("推广管理-优惠券管理-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApCoupon coupon) {

        // 如果是兑换码类型，则这里需要生存一个兑换码
        if (coupon.getType().equals(CouponConstant.TYPE_CODE)) {
            String code = couponService.generateCode();
            coupon.setCode(code);
        }

        couponService.save(coupon);
        return CommonResult.success(coupon);
    }

    @PreAuthorize("hasAuthority('admin:coupon:read')")
    @ApiOperation("推广管理-优惠券管理-详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApCoupon coupon = couponService.getById(id);
        return CommonResult.success(coupon);
    }

    @PreAuthorize("hasAuthority('admin:coupon:update')")
    @ApiOperation("推广管理-优惠券管理-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApCoupon coupon) {
        Long id = coupon.getId();
        if (id == null) {
            return CommonResult.paramsError();
        }
        if (!couponService.updateById(coupon)) {
            return CommonResult.error("编辑异常");
        }
        return CommonResult.success(coupon);
    }

    @PreAuthorize("hasAuthority('admin:coupon:delete')")
    @ApiOperation("推广管理-优惠券管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam coupon) {
        couponService.removeById(coupon.getId());
        return CommonResult.success();
    }

}
