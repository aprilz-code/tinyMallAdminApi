package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 后台用户角色表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-08-11
 */
public interface IApRoleService extends IService<ApRole> {

    boolean checkExist(String name);

    Page<ApRole> querySelective(String name, Integer page, Integer limit, String sort, String order);
}
