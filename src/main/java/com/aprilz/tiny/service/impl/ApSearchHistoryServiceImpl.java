package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApSearchHistoryMapper;
import com.aprilz.tiny.mbg.entity.ApSearchHistory;
import com.aprilz.tiny.service.IApSearchHistoryService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * <p>
 * 搜索历史表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApSearchHistoryServiceImpl extends ServiceImpl<ApSearchHistoryMapper, ApSearchHistory> implements IApSearchHistoryService {

    @Override
    @Transactional
    public Integer deleteByUid(Long id) {
        this.lambdaUpdate().set(ApSearchHistory::getDeleteFlag, false).eq(ApSearchHistory::getUserId, id)
                .update();
        return 1;
    }

    @Override
    public Page<ApSearchHistory> querySelective(Long userId, String keyword, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApSearchHistory> query = this.lambdaQuery();

        if (Objects.nonNull(userId)) {
            query.eq(ApSearchHistory::getUserId, userId);
        }
        if (StrUtil.isNotBlank(keyword)) {
            query.like(ApSearchHistory::getKeyword, keyword);
        }
        query.eq(ApSearchHistory::getDeleteFlag, false);
        Page<ApSearchHistory> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }
}
