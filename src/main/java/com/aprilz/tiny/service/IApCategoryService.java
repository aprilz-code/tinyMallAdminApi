package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 类目表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApCategoryService extends IService<ApCategory> {


    List<Map> queryIndex();

    List<ApCategory> queryByPid(Long pid);

    List<ApCategory> queryL1();
}
