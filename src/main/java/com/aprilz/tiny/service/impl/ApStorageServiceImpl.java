package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApStorageMapper;
import com.aprilz.tiny.mbg.entity.ApStorage;
import com.aprilz.tiny.service.IApStorageService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件存储表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApStorageServiceImpl extends ServiceImpl<ApStorageMapper, ApStorage> implements IApStorageService {

    @Override
    public ApStorage findByKey(String key) {
        return this.lambdaQuery().eq(ApStorage::getKey, key).eq(ApStorage::getDeleteFlag, false).one();

    }

    @Override
    public boolean deleteByKey(String key) {
        return this.remove(this.lambdaQuery().eq(ApStorage::getKey, key));
    }

    @Override
    public Page<ApStorage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApStorage> queryChainWrapper = this.lambdaQuery();
        if (StrUtil.isNotBlank(key)) {
            queryChainWrapper.like(ApStorage::getKey, key);
        }
        if (StrUtil.isNotBlank(name)) {
            queryChainWrapper.like(ApStorage::getName, name);
        }
        queryChainWrapper.eq(ApStorage::getDeleteFlag, false);
        Page<ApStorage> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return queryChainWrapper.page(pages);
    }
}
