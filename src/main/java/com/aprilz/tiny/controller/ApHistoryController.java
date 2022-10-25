package com.aprilz.tiny.controller;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApSearchHistory;
import com.aprilz.tiny.service.IApSearchHistoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/history")
@Slf4j
@Api("搜索历史管理")
public class ApHistoryController {

    @Autowired
    private IApSearchHistoryService searchHistoryService;

    @PreAuthorize("hasAuthority('admin:history:list')")
    @ApiOperation("用户管理-搜索历史-查询")
    @GetMapping("/list")
    public CommonResult list(Long userId, String keyword,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApSearchHistory> historyList = searchHistoryService.querySelective(userId, keyword, page, limit,
                sort, order);
        return CommonResult.success(historyList);
    }
}
