package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.consts.CacheConst;
import com.aprilz.tiny.service.IApSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/config")
@Validated
@Slf4j
@Api("商场配置管理")
public class ApConfigController {

    @Autowired
    private IApSystemService systemService;

    @PreAuthorize("hasAuthority('admin:config:mall:list')")
    @ApiOperation("配置管理-商场配置-详情")
    @GetMapping("/mall")
    public CommonResult listMall() {
        Map<String, String> data = systemService.listForType("ap_mall_");
        return CommonResult.success(data);
    }

    @PreAuthorize("hasAuthority('admin:config:mall:updateConfigs')")
    @ApiOperation("配置管理-商场配置-编辑")
    @PostMapping("/mall")
    public CommonResult updateMall(@RequestBody Map<String, String> map) {
        systemService.updateConfig(map);
        CacheConst.refreshCache();
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:config:express:list')")
    @ApiOperation("配置管理-运费配置-详情")
    @GetMapping("/express")
    public CommonResult listExpress() {
        Map<String, String> data = systemService.listForType("ap_express_");
        return CommonResult.success(data);
    }

    @PreAuthorize("hasAuthority('admin:config:express:updateConfigs')")
    @ApiOperation("配置管理-运费配置-编辑")
    @PostMapping("/express")
    public CommonResult updateExpress(@RequestBody Map<String, String> map) {
        systemService.updateConfig(map);
        CacheConst.refreshCache();
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:config:order:list')")
    @ApiOperation("配置管理-订单配置-详情")
    @GetMapping("/order")
    public CommonResult lisOrder() {
        Map<String, String> data = systemService.listForType("ap_order_");
        return CommonResult.success(data);
    }

    @PreAuthorize("hasAuthority('admin:config:order:updateConfigs')")
    @ApiOperation("配置管理-订单配置-编辑")
    @PostMapping("/order")
    public CommonResult updateOrder(@RequestBody Map<String, String> map) {
        systemService.updateConfig(map);
        CacheConst.refreshCache();
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:config:wx:list')")
    @ApiOperation("配置管理-小程序配置-详情")
    @GetMapping("/wx")
    public CommonResult listWx() {
        Map<String, String> data = systemService.listForType("ap_wx_");
        return CommonResult.success(data);
    }

    @PreAuthorize("hasAuthority('admin:config:wx:updateConfigs')")
    @ApiOperation("配置管理-小程序配置-编辑")
    @PostMapping("/wx")
    public CommonResult updateWx(@RequestBody Map<String, String> map) {
        systemService.updateConfig(map);
        CacheConst.refreshCache();
        return CommonResult.success();
    }
}
