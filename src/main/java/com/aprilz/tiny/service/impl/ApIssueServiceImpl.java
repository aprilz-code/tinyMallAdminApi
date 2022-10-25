package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApIssueMapper;
import com.aprilz.tiny.mbg.entity.ApIssue;
import com.aprilz.tiny.service.IApIssueService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 常见问题表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApIssueServiceImpl extends ServiceImpl<ApIssueMapper, ApIssue> implements IApIssueService {

    @Override
    public Page<ApIssue> querySelective(String question, Integer page, Integer size, String sort, String order) {
        Page<ApIssue> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(size).setSort(sort).setOrder(order));

        LambdaQueryWrapper<ApIssue> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(question)) {
            queryWrapper.like(ApIssue::getQuestion, question);
        }
        queryWrapper.eq(ApIssue::getDeleteFlag, false);

        return this.page(pages, queryWrapper);
    }
}
