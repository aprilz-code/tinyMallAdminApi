package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApLogService extends IService<ApLog> {

    Page<ApLog> querySelective(String name, Integer page, Integer limit, String sort, String order);
}
