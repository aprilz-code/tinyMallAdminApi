package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.aprilz.tiny.mbg.entity.ApPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 后台用户表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-08-11
 */
public interface IApAdminService extends IService<ApAdmin> {


    ApAdmin getAdminByUsernameOrMobile(String username);

    ApAdmin register(ApAdmin apAdminParam);

    String login(String username, String password);

    List<ApPermission> getPermissionList(Long adminId);

    Page<ApAdmin> querySelective(String username, Integer page, Integer limit, String sort, String order);
}
