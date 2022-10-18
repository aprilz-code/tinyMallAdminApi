package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApGoods;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品基本信息表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApGoodsService extends IService<ApGoods> {

    List<ApGoods> queryByNew(Integer offset, Integer limit);

    List<ApGoods> queryByHot(Integer offset, Integer limit);

    Page<ApGoods> querySelective(Integer categoryId, Integer brandId, String keyword, Boolean isHot, Boolean isNew, Integer page, Integer limit, String sort, String order);

    List<Long> getCategoryIds(Integer categoryId, Integer brandId, String keyword, Boolean isHot, Boolean isNew);

}
