package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApGoodsSpecification;
import com.aprilz.tiny.vo.GoodsSpecificationVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 商品规格表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApGoodsSpecificationService extends IService<ApGoodsSpecification> {

    List<GoodsSpecificationVo> getSpecificationVoList(Long goodId);
}
