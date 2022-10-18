package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApComment;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApCommentService extends IService<ApComment> {

    Page<ApComment> querySelective(String userId, String valueId, Integer page, Integer limit, String sort, String order);
}
