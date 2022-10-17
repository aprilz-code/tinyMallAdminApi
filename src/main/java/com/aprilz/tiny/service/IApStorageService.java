package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApStorage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 文件存储表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApStorageService extends IService<ApStorage> {

    ApStorage findByKey(String key);

    boolean deleteByKey(String key);

    Page<ApStorage> querySelective(String key, String name, Integer page, Integer limit, String sort, String order);
}
