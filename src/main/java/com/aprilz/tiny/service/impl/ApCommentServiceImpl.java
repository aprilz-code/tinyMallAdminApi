package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApCommentMapper;
import com.aprilz.tiny.mbg.entity.ApComment;
import com.aprilz.tiny.service.IApCommentService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 评论表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApCommentServiceImpl extends ServiceImpl<ApCommentMapper, ApComment> implements IApCommentService {

    @Override
    public Page<ApComment> querySelective(String userId, String valueId, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApComment> query = this.lambdaQuery();

        // type=2 是订单商品回复，这里过滤
        query.ne(ApComment::getType, 2);
        if (StrUtil.isNotBlank(userId)) {
            query.eq(ApComment::getUserId, Long.valueOf(userId));
        }

        if (StrUtil.isNotBlank(valueId)) {
            query.eq(ApComment::getValueId, Integer.valueOf(valueId)).eq(ApComment::getType, 0);
        }
        query.eq(ApComment::getDeleteFlag, false);
        Page<ApComment> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }
}
