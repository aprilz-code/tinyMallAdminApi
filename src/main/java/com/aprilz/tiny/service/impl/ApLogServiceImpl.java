package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApLogMapper;
import com.aprilz.tiny.mbg.entity.ApLog;
import com.aprilz.tiny.service.IApLogService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApLogServiceImpl extends ServiceImpl<ApLogMapper, ApLog> implements IApLogService {

    @Override
    public Page<ApLog> querySelective(String name, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApLog> query = this.lambdaQuery();
        if (StrUtil.isNotBlank(name)) {
            query.like(ApLog::getAdmin, name);
        }
        query.eq(ApLog::getDeleteFlag, false);
        Page<ApLog> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }
}
