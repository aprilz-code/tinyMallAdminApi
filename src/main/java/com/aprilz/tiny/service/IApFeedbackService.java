package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApFeedback;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 意见反馈表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApFeedbackService extends IService<ApFeedback> {

    Page<ApFeedback> querySelective(Integer userId, String username, Integer page, Integer limit, String sort, String order);
}
