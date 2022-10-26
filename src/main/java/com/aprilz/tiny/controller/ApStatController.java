package com.aprilz.tiny.controller;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.service.IApStatService;
import com.aprilz.tiny.vo.StatVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stat")
@Validated
@Slf4j
@Api("统计管理")
public class ApStatController {


    @Autowired
    private IApStatService statService;

    @PreAuthorize("hasAuthority('admin:stat:user')")
    @ApiOperation("统计管理-用户统计-查询")
    @GetMapping("/user")
    public CommonResult statUser() {
        List<Map> rows = statService.statUser();
        String[] columns = new String[]{"day", "users"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return CommonResult.success(statVo);
    }

    @PreAuthorize("hasAuthority('admin:stat:order')")
    @ApiOperation("统计管理-订单统计-查询")
    @GetMapping("/order")
    public CommonResult statOrder() {
        List<Map> rows = statService.statOrder();
        String[] columns = new String[]{"day", "orders", "customers", "amount", "pcr"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);

        return CommonResult.success(statVo);
    }

    @PreAuthorize("hasAuthority('admin:stat:goods')")
    @ApiOperation("统计管理-商品统计-查询")
    @GetMapping("/goods")
    public CommonResult statGoods() {
        List<Map> rows = statService.statGoods();
        String[] columns = new String[]{"day", "orders", "products", "amount"};
        StatVo statVo = new StatVo();
        statVo.setColumns(columns);
        statVo.setRows(rows);
        return CommonResult.success(statVo);
    }

}
