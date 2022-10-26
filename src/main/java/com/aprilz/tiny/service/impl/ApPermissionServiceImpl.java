package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApPermissionMapper;
import com.aprilz.tiny.mbg.entity.ApPermission;
import com.aprilz.tiny.service.IApPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 后台用户权限表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-08-11
 */
@Service
public class ApPermissionServiceImpl extends ServiceImpl<ApPermissionMapper, ApPermission> implements IApPermissionService {

    @Override
    public List<ApPermission> getPermissionList(Long userId) {
        return  this.baseMapper.getPermissionList(userId);

    }

    @Override
    public boolean checkSuperPermission(Long roleId) {
        Long count = this.baseMapper.checkSuperPermission(roleId);
        return count > 0;
    }
}
