package com.aprilz.tiny.controller;

import cn.hutool.core.bean.BeanUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApAd;
import com.aprilz.tiny.param.CreateOrUpdateAdParam;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApAdService;
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
@RequestMapping("/ad")
@Validated
@Slf4j
@Api(tags = "广告管理")
public class ApAdController {

    @Autowired
    private IApAdService adService;

    @PreAuthorize("hasAuthority('admin:ad:list')")
    @ApiOperation("广告列表")
    @GetMapping("/list")
    public CommonResult list(String name, String content,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApAd> adList = adService.querySelective(name, content, page, limit, sort, order);
        return CommonResult.success(adList);
    }

    @PreAuthorize("hasAuthority('admin:ad:create')")
    @ApiOperation("广告添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody CreateOrUpdateAdParam ad) {
        ApAd apAd = new ApAd();
        BeanUtil.copyProperties(ad,apAd,"id");
        adService.save(apAd);
        return CommonResult.success(apAd);
    }

    @PreAuthorize("hasAuthority('admin:ad:read')")
    @ApiOperation("广告详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApAd ad = adService.getById(id);
        return CommonResult.success(ad);
    }

    @PreAuthorize("hasAuthority('admin:ad:update')")
    @ApiOperation("广告编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody CreateOrUpdateAdParam ad) {
        ApAd apAd = new ApAd();
        BeanUtil.copyProperties(ad,apAd);
        if (!adService.updateById(apAd)) {
            return CommonResult.error("编辑异常");
        }

        return CommonResult.success(apAd);
    }

    @PreAuthorize("hasAuthority('admin:ad:delete')")
    @ApiOperation("广告删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam param) {
        adService.removeById(param.getId());
        return CommonResult.success();
    }

}
