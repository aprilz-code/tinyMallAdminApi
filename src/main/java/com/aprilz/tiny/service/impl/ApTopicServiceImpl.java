package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApTopicMapper;
import com.aprilz.tiny.mbg.entity.ApTopic;
import com.aprilz.tiny.service.IApTopicService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 专题表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApTopicServiceImpl extends ServiceImpl<ApTopicMapper, ApTopic> implements IApTopicService {

    @Override
    public List<ApTopic> query(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApTopic::getDeleteFlag, false)
                .orderByDesc(ApTopic::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }

    @Override
    public Page<ApTopic> queryList(Integer page, Integer limit, String sort, String order) {
        return this.lambdaQuery().eq(ApTopic::getDeleteFlag, false)
                .page(PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order)));
    }

    @Override
    public Page<ApTopic> queryRelatedList(Long id, int offset, int limit) {
        return this.lambdaQuery().eq(ApTopic::getDeleteFlag, false).eq(ApTopic::getId, id)
                .page(PageUtil.initPage(new PageVO().setPageNumber(offset).setPageSize(limit).setSort("create_time").setOrder("desc")));
    }

    @Override
    public Page<ApTopic> querySelective(String title, String subtitle, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApTopic> query = this.lambdaQuery();
        if (StrUtil.isNotBlank(title)) {
            query.like(ApTopic::getTitle, title);
        }
        if (StrUtil.isNotBlank(subtitle)) {
            query.like(ApTopic::getSubtitle, subtitle);
        }
        query.eq(ApTopic::getDeleteFlag, false);
        Page<ApTopic> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }
}
