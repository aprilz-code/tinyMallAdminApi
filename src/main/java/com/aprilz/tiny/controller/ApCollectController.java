package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApCollect;
import com.aprilz.tiny.service.IApCollectService;
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
@RequestMapping("/collect")
@Validated
@Slf4j
@Api("用户收藏管理")
public class ApCollectController {

    @Autowired
    private IApCollectService collectService;


    @PreAuthorize("hasAuthority('admin:collect:list')")
    @ApiOperation("用户管理-用户收藏-查询")
    @GetMapping("/list")
    public CommonResult list(String userId, String valueId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApCollect> collectList = collectService.querySelective(userId, valueId, page, limit, sort, order);
        return CommonResult.success(collectList);
    }
}
