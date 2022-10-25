package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApFeedbackMapper;
import com.aprilz.tiny.mbg.entity.ApFeedback;
import com.aprilz.tiny.service.IApFeedbackService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 意见反馈表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApFeedbackServiceImpl extends ServiceImpl<ApFeedbackMapper, ApFeedback> implements IApFeedbackService {

    @Override
    public Page<ApFeedback> querySelective(Integer userId, String username, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApFeedback> query = this.lambdaQuery();

        if (Objects.nonNull(userId)) {
            query.eq(ApFeedback::getUserId, userId);
        }

        if (StrUtil.isNotBlank(username)) {
            query.eq(ApFeedback::getUsername, username);
        }
        query.eq(ApFeedback::getDeleteFlag, false);
        Page<ApFeedback> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }
}
