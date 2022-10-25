package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mapper.ApUserMapper;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.mbg.entity.ApGoodsProduct;
import com.aprilz.tiny.mbg.entity.ApOrder;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApGoodsProductService;
import com.aprilz.tiny.service.IApGoodsService;
import com.aprilz.tiny.service.IApOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@Validated
@Slf4j
@Api("首页管理")
public class ApDashbordController {

    @Autowired
    private ApUserMapper userMapper;
    @Autowired
    private IApGoodsService goodsService;
    @Autowired
    private IApGoodsProductService productService;
    @Autowired
    private IApOrderService orderService;

    @GetMapping("")
    public CommonResult info() {
        long userTotal = userMapper.selectCount( new LambdaQueryWrapper<ApUser>().eq(ApUser::getDeleteFlag, false));
        long goodsTotal = goodsService.lambdaQuery().eq(ApGoods::getDeleteFlag,false).count();
        long productTotal = productService.lambdaQuery().eq(ApGoodsProduct::getDeleteFlag,false).count();
        long orderTotal = orderService.lambdaQuery().eq(ApOrder::getDeleteFlag,false).count();
        Map<String, Long> data = new HashMap<>();
        data.put("userTotal", userTotal);
        data.put("goodsTotal", goodsTotal);
        data.put("productTotal", productTotal);
        data.put("orderTotal", orderTotal);
        return CommonResult.success(data);
    }

}
