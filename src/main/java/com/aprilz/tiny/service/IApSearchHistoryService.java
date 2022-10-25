package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApSearchHistory;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 搜索历史表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApSearchHistoryService extends IService<ApSearchHistory> {

    Integer deleteByUid(Long id);

    Page<ApSearchHistory> querySelective(Long userId, String keyword, Integer page, Integer limit, String sort, String order);
}
