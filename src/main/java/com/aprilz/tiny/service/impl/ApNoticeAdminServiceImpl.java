package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApNoticeAdminMapper;
import com.aprilz.tiny.mbg.entity.ApNoticeAdmin;
import com.aprilz.tiny.service.IApNoticeAdminService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 通知管理员表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-10-25
 */
@Service
public class ApNoticeAdminServiceImpl extends ServiceImpl<ApNoticeAdminMapper, ApNoticeAdmin> implements IApNoticeAdminService {

    @Override
    public List<ApNoticeAdmin> queryByNoticeId(Long noticeId) {
        return this.lambdaQuery().eq(ApNoticeAdmin::getNoticeId, noticeId).eq(ApNoticeAdmin::getDeleteFlag, false).list();
    }

    @Override
    public boolean countReadByNoticeId(Long noticeId) {
        return this.lambdaQuery().eq(ApNoticeAdmin::getNoticeId, noticeId)
                .isNotNull(ApNoticeAdmin::getReadTime).eq(ApNoticeAdmin::getDeleteFlag, false).exists();
    }

    @Override
    public Long countUnread(Long id) {
        return this.lambdaQuery().eq(ApNoticeAdmin::getAdminId, id).isNull(ApNoticeAdmin::getReadTime)
                .eq(ApNoticeAdmin::getDeleteFlag, false).count();
    }

    @Override
    public Page<ApNoticeAdmin> querySelective(String title, String type, Long id, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApNoticeAdmin> query = this.lambdaQuery();

        if (StrUtil.isNotBlank(title)) {
            query.like(ApNoticeAdmin::getNoticeTitle, title);
        }
        if ("read".equals(type)) {
            query.isNotNull(ApNoticeAdmin::getReadTime);
        } else if ("unread".equals(type)) {
            query.isNull(ApNoticeAdmin::getReadTime);
        }

        query.eq(ApNoticeAdmin::getAdminId, id).eq(ApNoticeAdmin::getDeleteFlag, false);

        Page<ApNoticeAdmin> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }

    @Override
    public void markReadByIds(List<Long> ids, Long userId) {
        this.lambdaUpdate().eq(ApNoticeAdmin::getAdminId, userId)
                .eq(ApNoticeAdmin::getDeleteFlag, false)
                .in(ApNoticeAdmin::getId, ids)
                .set(ApNoticeAdmin::getReadTime, new Date()).update();
    }

    @Override
    public void deleteById(Long id, Long userId) {
        this.lambdaUpdate().eq(ApNoticeAdmin::getAdminId, userId)
                .eq(ApNoticeAdmin::getDeleteFlag, false)
                .eq(ApNoticeAdmin::getId, id)
                .set(ApNoticeAdmin::getDeleteFlag, true).update();
    }

    @Override
    public void deleteByIds(List<Long> ids, Long userId) {
        this.lambdaUpdate().eq(ApNoticeAdmin::getAdminId, userId)
                .eq(ApNoticeAdmin::getDeleteFlag, false)
                .in(ApNoticeAdmin::getId, ids)
                .set(ApNoticeAdmin::getDeleteFlag, true).update();
    }
}
