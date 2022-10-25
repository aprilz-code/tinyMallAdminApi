package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApIssue;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 常见问题表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApIssueService extends IService<ApIssue> {

    Page<ApIssue> querySelective(String question, Integer page, Integer size, String sort, String order);
}
