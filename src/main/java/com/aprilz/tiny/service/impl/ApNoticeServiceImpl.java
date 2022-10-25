package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApNoticeMapper;
import com.aprilz.tiny.mbg.entity.ApNotice;
import com.aprilz.tiny.service.IApNoticeService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 通知表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-10-25
 */
@Service
public class ApNoticeServiceImpl extends ServiceImpl<ApNoticeMapper, ApNotice> implements IApNoticeService {

    @Override
    public Page<ApNotice> querySelective(String title, String content, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApNotice> query = this.lambdaQuery();
        if (StrUtil.isNotBlank(title)) {
            query.like(ApNotice::getTitle, title);
        }
        if (StrUtil.isNotBlank(content)) {
            query.like(ApNotice::getContent, content);
        }
        query.eq(ApNotice::getDeleteFlag, false);
        Page<ApNotice> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }
}
