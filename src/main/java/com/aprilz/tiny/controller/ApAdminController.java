package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApAdminService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Objects;


@RestController
@RequestMapping("/admin/admin")
@Validated
@Slf4j
@Api(tags = "管理员管理")
public class ApAdminController {


    @Autowired
    private IApAdminService adminService;


    @PreAuthorize("hasAuthority('admin:admin:list')")
    @ApiOperation("系统管理-管理员管理查询")
    @GetMapping("/list")
    public CommonResult list(String username,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApAdmin> adminList = adminService.querySelective(username, page, limit, sort, order);
        return CommonResult.success(adminList);
    }


    @PreAuthorize("hasAuthority('admin:admin:create')")
    @ApiOperation("系统管理-管理员管理添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApAdmin admin) {


        String username = admin.getUsername();
        Long count = adminService.lambdaQuery().eq(ApAdmin::getUsername, username)
                .eq(ApAdmin::getDeleteFlag, false).count();
        if (count > 0) {
            return CommonResult.error(ResultCode.ADMIN_NAME_EXIST);
        }

        String rawPassword = admin.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(rawPassword);
        admin.setPassword(encodedPassword);
        adminService.save(admin);
        return CommonResult.success(admin);
    }

    @PreAuthorize("hasAuthority('admin:admin:read')")
    @ApiOperation("系统管理-管理员管理详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApAdmin admin = adminService.getById(id);
        return CommonResult.success(admin);
    }

    @PreAuthorize("hasAuthority('admin:admin:update')")
    @ApiOperation("系统管理-管理员管理编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApAdmin admin) {
        Long id = admin.getId();
        if (id == null) {
            return CommonResult.paramsError();
        }
        // 不允许管理员通过编辑接口修改密码
        admin.setPassword(null);
        if (!adminService.updateById(admin)) {
            return CommonResult.error();
        }

        return CommonResult.success(admin);
    }

    @PreAuthorize("hasAuthority('admin:admin:delete')")
    @ApiOperation("系统管理-管理员管理删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam admin) {
        Long anotherAdminId = admin.getId();

        // 管理员不能删除自身账号
        ApAdmin user = UserUtil.getUser();
        if (Objects.equals(user.getId(), anotherAdminId)) {
            return CommonResult.error(ResultCode.ADMIN_DELETE_NOT_ALLOWED);
        }
        adminService.removeById(anotherAdminId);
        return CommonResult.success();
    }
}
