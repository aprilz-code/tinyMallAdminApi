package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApRoleMapper;
import com.aprilz.tiny.mbg.entity.ApRole;
import com.aprilz.tiny.service.IApRoleService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台用户角色表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-08-11
 */
@Service
public class ApRoleServiceImpl extends ServiceImpl<ApRoleMapper, ApRole> implements IApRoleService {

    @Override
    public boolean checkExist(String name) {
        return this.lambdaQuery().eq(ApRole::getName, name).eq(ApRole::getDeleteFlag, false).exists();
    }

    @Override
    public Page<ApRole> querySelective(String name, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApRole> query = this.lambdaQuery();
        if (StrUtil.isNotBlank(name)) {
            query.like(ApRole::getName, name);
        }
        query.eq(ApRole::getDeleteFlag, false);
        Page<ApRole> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }
}
