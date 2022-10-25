package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApLog;
import com.aprilz.tiny.service.IApLogService;
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
@RequestMapping("/admin/log")
@Validated
@Slf4j
@Api("操作日志管理")
public class ApLogController {

    @Autowired
    private IApLogService logService;

    @PreAuthorize("hasAuthority('admin:log:list')")
    @ApiOperation("系统管理-操作日志-查询")
    @GetMapping("/list")
    public CommonResult list(String name,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApLog> logList = logService.querySelective(name, page, limit, sort, order);
        return CommonResult.success(logList);
    }
}
