package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApCollectMapper;
import com.aprilz.tiny.mbg.entity.ApCollect;
import com.aprilz.tiny.service.IApCollectService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApCollectServiceImpl extends ServiceImpl<ApCollectMapper, ApCollect> implements IApCollectService {

    @Override
    public Page<ApCollect> querySelective(String userId, String valueId, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApCollect> query = this.lambdaQuery();
        if (StrUtil.isNotBlank(userId)) {
            query.eq(ApCollect::getUserId, Long.valueOf(userId));
        }

        if (StrUtil.isNotBlank(valueId)) {
            query.eq(ApCollect::getValueId, Integer.valueOf(valueId));
        }
        query.eq(ApCollect::getDeleteFlag, false);

        Page<ApCollect> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }
}
