package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApAddress;
import com.aprilz.tiny.service.IApAddressService;
import com.aprilz.tiny.service.IApRegionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/address")
@Validated
@Slf4j
@Api(tags = "收货地址管理")
public class ApAddressController {


    @Autowired
    private IApAddressService addressService;
    @Autowired
    private IApRegionService regionService;

    @PreAuthorize("hasAuthority('admin:address:list')")
    @ApiOperation("用户管理-收货地址列表查询")
    @GetMapping("/list")
    public CommonResult list(Integer userId, String name,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {

        Page<ApAddress> addressList = addressService.querySelective(userId, name, page, limit, sort, order);
        return CommonResult.success(addressList);
    }
}
