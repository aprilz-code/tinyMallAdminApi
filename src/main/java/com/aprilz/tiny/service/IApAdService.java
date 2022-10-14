package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApAd;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 广告表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-18
 */
public interface IApAdService extends IService<ApAd> {

    Page<ApAd> querySelective(String name, String content, Integer page, Integer limit, String sort, String order);
}
