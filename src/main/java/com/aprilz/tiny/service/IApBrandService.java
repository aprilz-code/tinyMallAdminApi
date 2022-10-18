package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApBrand;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 品牌商表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApBrandService extends IService<ApBrand> {

    List<ApBrand> query(Integer offset, Integer limit);

    Page<ApBrand> query(Integer page, Integer limit, String sort, String order);

    Page<ApBrand> querySelective(String id, String name, Integer page, Integer limit, String sort, String order);
}
