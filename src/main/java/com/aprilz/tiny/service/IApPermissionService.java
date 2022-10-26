package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台用户权限表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-08-11
 */
public interface IApPermissionService extends IService<ApPermission> {

    List<ApPermission> getPermissionList(Long userId);

    boolean checkSuperPermission(Long roleId);
}
