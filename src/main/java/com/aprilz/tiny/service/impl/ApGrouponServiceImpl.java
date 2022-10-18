package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mall.GrouponConstant;
import com.aprilz.tiny.mapper.ApGrouponMapper;
import com.aprilz.tiny.mbg.entity.ApGroupon;
import com.aprilz.tiny.service.IApGrouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 团购活动表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApGrouponServiceImpl extends ServiceImpl<ApGrouponMapper, ApGroupon> implements IApGrouponService {

    @Override
    public List<ApGroupon> queryMyGroupon(Long userId) {
        return this.lambdaQuery().eq(ApGroupon::getUserId, userId).eq(ApGroupon::getCreatorUserId, userId)
                .eq(ApGroupon::getDeleteFlag, false).eq(ApGroupon::getGrouponId, 0L)
                .eq(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .orderByDesc(ApGroupon::getCreateTime).list();

    }

    @Override
    public List<ApGroupon> queryMyJoinGroupon(Long userId) {
        return this.lambdaQuery().eq(ApGroupon::getUserId, userId)
                .eq(ApGroupon::getDeleteFlag, false).eq(ApGroupon::getGrouponId, 0L)
                .eq(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .orderByDesc(ApGroupon::getCreateTime).list();
    }

    @Override
    public ApGroupon queryByOrderId(Long id) {
        return this.lambdaQuery().eq(ApGroupon::getOrderId, id)
                .eq(ApGroupon::getDeleteFlag, false).one();
    }

    @Override
    public Long countGroupon(Long grouponLinkId) {
        return this.lambdaQuery().eq(ApGroupon::getGrouponId, grouponLinkId)
                .ne(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .eq(ApGroupon::getDeleteFlag, false).count();
    }

    @Override
    public boolean hasJoin(Long userId, Long grouponLinkId) {
        return this.lambdaQuery().eq(ApGroupon::getUserId, userId)
                .eq(ApGroupon::getGrouponId, grouponLinkId)
                .ne(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .eq(ApGroupon::getDeleteFlag, false).exists();
    }

    @Override
    public List<ApGroupon> queryJoinRecord(Long grouponId) {
        return this.lambdaQuery().eq(ApGroupon::getGrouponId, grouponId)
                .ne(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .eq(ApGroupon::getDeleteFlag, false).orderByDesc(ApGroupon::getCreateTime)
                .list();
    }
}
