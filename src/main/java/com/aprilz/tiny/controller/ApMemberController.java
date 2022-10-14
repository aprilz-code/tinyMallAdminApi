package com.aprilz.tiny.controller;


import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.cache.Cache;
import com.aprilz.tiny.dto.ApAdminLoginParam;
import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.aprilz.tiny.mbg.entity.ApPermission;
import com.aprilz.tiny.service.IApAdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员登录注册管理Controller
 * Created by aprilz on 2018/8/3.
 */
@Controller
@Api(tags = "会员登录注册管理")
@RequestMapping("/sso")
@Validated
public class ApMemberController {
    @Autowired
    private IApAdminService adminService;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private Cache cache;

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "/register")
    @ResponseBody
    public CommonResult<ApAdmin> register(@RequestBody ApAdmin ApAdminParam, BindingResult result) {


        ApAdmin ApAdmin = adminService.register(ApAdminParam);
        if (ApAdmin == null) {
            CommonResult.error();
        }
        return CommonResult.success(ApAdmin);
    }

    @ApiOperation(value = "登录以后返回token")
    @PostMapping(value = "/login")
    @ResponseBody
    public CommonResult login(@RequestBody ApAdminLoginParam ApAdminLoginParam, HttpServletRequest request, BindingResult result) {

        String code = cache.getString(ApAdminLoginParam.getUuid());
        if (StrUtil.isBlank(code) || !code.equalsIgnoreCase(ApAdminLoginParam.getCode())) {
            return CommonResult.error(ResultCode.ADMIN_INVALID_KAPTCHA);
        }

        String token = adminService.login(ApAdminLoginParam.getUsername(), ApAdminLoginParam.getPassword());
        if (token == null) {
            return CommonResult.error(ResultCode.ADMIN_INVALID_ACCOUNT);
        }
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenHead + token);

        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取用户所有权限（包括+-权限）")
    @GetMapping(value = "/permission/{adminId}")
    @ResponseBody
    @PreAuthorize("hasAuthority('sso:permission:read')")
    public CommonResult<List<ApPermission>> getPermissionList(@PathVariable Long adminId) {
        List<ApPermission> permissionList = adminService.getPermissionList(adminId);
        return CommonResult.success(permissionList);
    }

}
