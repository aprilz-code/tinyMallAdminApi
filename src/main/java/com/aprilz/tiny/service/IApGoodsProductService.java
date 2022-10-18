package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApGoodsProduct;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 商品货品表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApGoodsProductService extends IService<ApGoodsProduct> {

    int reduceStock(Long productId, Integer number);

    int addStock(Long productId, Integer number);
}
