package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.utils.ResponseUtil;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.*;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.param.UpdatePermissionsParam;
import com.aprilz.tiny.service.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thoughtworks.xstream.core.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.*;



@RestController
@RequestMapping("/role")
@Validated
@Slf4j
@Api("角色管理")
public class ApRoleController {

    @Autowired
    private IApRoleService roleService;
    @Autowired
    private IApPermissionService permissionService;
    @Autowired
    private IApAdminService adminService;

    @Autowired
    private IApAdminRoleRelationService  adminRoleRelationService;

    @Autowired
    private IApRolePermissionRelationService  rolePermissionRelationService;

    @PreAuthorize("hasAuthority('admin:role:list')")
    @ApiOperation("系统管理-角色管理-角色查询")
    @GetMapping("/list")
    public CommonResult list(String name,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApRole> roleList = roleService.querySelective(name, page, limit, sort, order);
        return CommonResult.success(roleList);
    }

    @GetMapping("/options")
    public CommonResult options() {
        List<ApRole> roleList = roleService.list();

        List<Map<String, Object>> options = new ArrayList<>(roleList.size());
        for (ApRole role : roleList) {
            Map<String, Object> option = new HashMap<>(2);
            option.put("value", role.getId());
            option.put("label", role.getName());
            options.add(option);
        }

        return CommonResult.success(options);
    }

    @PreAuthorize("hasAuthority('admin:role:read')")
    @ApiOperation("系统管理-角色管理-角色详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApRole role = roleService.getById(id);
        return CommonResult.success(role);
    }




    @PreAuthorize("hasAuthority('admin:role:create')")
    @ApiOperation("系统管理-角色管理-角色添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApRole role) {

        if (roleService.checkExist(role.getName())) {
            return CommonResult.error(ResultCode.ROLE_NAME_EXIST);
        }

        roleService.save(role);

        return CommonResult.success(role);
    }

    @PreAuthorize("hasAuthority('admin:role:update')")
    @ApiOperation("系统管理-角色管理-角色编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApRole role) {

        roleService.updateById(role);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:role:delete')")
    @ApiOperation("系统管理-角色管理-角色删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam role) {
        Long id = role.getId();
        // 如果当前角色所对应管理员仍存在，则拒绝删除角色。
        boolean exists = adminRoleRelationService.lambdaQuery().eq(ApAdminRoleRelation::getRoleId, role)
                .eq(ApAdminRoleRelation::getDeleteFlag, false).exists();
        if(exists){
            return CommonResult.error(ResultCode.ROLE_USER_EXIST);
        }
        roleService.lambdaUpdate().set(ApRole::getDeleteFlag, true).eq(ApRole::getId, id).update();
        return CommonResult.success();
    }





    /**
     * 管理员的权限情况
     *
     * @return 系统所有权限列表、角色权限、管理员已分配权限
     */
    @PreAuthorize("hasAuthority('admin:role:get')")
    @ApiOperation("系统管理-角色管理-权限详情")
    @GetMapping("/permissions")
    public CommonResult getPermissions(Integer roleId) {
        List<ApPermission> systemPermissions =permissionService.list();

        //todo 这里需要注意的是，如果存在超级权限*，那么这里需要转化成当前所有系统权限。
        // 之所以这么做，是因为前端不能识别超级权限，所以这里需要转换一下。
        ApAdmin user = UserUtil.getUser();
        List<ApPermission> assignedPermissions =  permissionService.getPermissionList(user.getId());
        Optional<ApPermission> first = assignedPermissions.parallelStream().filter(per -> "*".equals(per.getValue())).findFirst();



        Map<String, Object> data = new HashMap<>();
        data.put("systemPermissions", systemPermissions);

        if(Objects.nonNull(first.get())){
            data.put("assignedPermissions", systemPermissions);
        }else{
            data.put("assignedPermissions", assignedPermissions);
        }
        data.put("curPermissions", assignedPermissions);
        return CommonResult.success(data);
    }


    /**
     * 更新管理员的权限
     *
     * @param param
     * @return
     */
    @PreAuthorize("hasAuthority('admin:role:update')")
    @ApiOperation("系统管理-角色管理-权限变更")
    @PostMapping("/permissions")
    public CommonResult updatePermissions(@RequestBody UpdatePermissionsParam param) {
        Long roleId =param.getRoleId();
        List<Long> permissions = param.getPermissions();

        // 如果修改的角色是超级权限，则拒绝修改。
        if (permissionService.checkSuperPermission(roleId)) {
            return CommonResult.error(ResultCode.ROLE_SUPER_SUPERMISSION);
        }

        // 先删除旧的权限，再更新新的权限
        rolePermissionRelationService.deleteByRoleId(roleId);
        for (Long permission : permissions) {
            ApRolePermissionRelation apPermission = new ApRolePermissionRelation();
            apPermission.setRoleId(roleId);
            apPermission.setPermissionId(permission);
            rolePermissionRelationService.save(apPermission);
        }
        return CommonResult.success();
    }

}
