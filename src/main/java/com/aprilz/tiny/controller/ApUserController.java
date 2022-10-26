package com.aprilz.tiny.controller;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.service.IApUserService;
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
@RequestMapping("/user")
@Validated
@Slf4j
@Api("会员管理")
public class ApUserController {

    @Autowired
    private IApUserService userService;

    @PreAuthorize("hasAuthority('admin:user:list')")
    @ApiOperation("用户管理-会员管理-查询")
    @GetMapping("/list")
    public CommonResult list(String username, String mobile,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApUser> userList = userService.querySelective(username, mobile, page, limit, sort, order);
        return CommonResult.success(userList);
    }

    @PreAuthorize("hasAuthority('admin:user:read')")
    @ApiOperation("用户管理-会员管理-详情")
    @GetMapping("/detail")
    public CommonResult userDetail(@NotNull Integer id) {
    	ApUser user=userService.getById(id);
        return CommonResult.success(user);
    }

    @PreAuthorize("hasAuthority('admin:user:update')")
    @ApiOperation("用户管理-会员管理-编辑")
    @PostMapping("/update")
    public CommonResult userUpdate(@RequestBody ApUser user) {
        return CommonResult.success(userService.updateById(user));
    }
}
