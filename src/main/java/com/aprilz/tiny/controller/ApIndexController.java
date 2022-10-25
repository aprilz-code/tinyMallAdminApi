package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
@Slf4j
public class ApIndexController {


    @RequestMapping("/index")
    public CommonResult index() {
        return CommonResult.success("hello world, this is admin service");
    }

    @RequestMapping("/guest")
    public CommonResult guest() {
        return CommonResult.success("hello world, this is admin service");
    }

    @RequestMapping("/authn")
    public CommonResult authn() {
        return CommonResult.success("hello world, this is admin service");
    }

    @RequestMapping("/user")
    public CommonResult user() {
        return CommonResult.success("hello world, this is admin service");
    }

    @RequestMapping("/admin")
    public CommonResult admin() {
        return CommonResult.success("hello world, this is admin service");
    }

    @RequestMapping("/admin2")
    public CommonResult admin2() {
        return CommonResult.success("hello world, this is admin service");
    }

    @PreAuthorize("hasAuthority('index:permission:read')")
    @ApiOperation("其他-权限测试-权限读")
    @GetMapping("/read")
    public CommonResult read() {
        return CommonResult.success("hello world, this is admin service");
    }

    @PreAuthorize("hasAuthority('index:permission:write')")
    @ApiOperation("其他-权限测试-权限写")
    @PostMapping("/write")
    public CommonResult write() {
        return CommonResult.success("hello world, this is admin service");
    }

}
