package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApGoodsProductMapper;
import com.aprilz.tiny.mbg.entity.ApGoodsProduct;
import com.aprilz.tiny.service.IApGoodsProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品货品表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApGoodsProductServiceImpl extends ServiceImpl<ApGoodsProductMapper, ApGoodsProduct> implements IApGoodsProductService {

    @Override
    public int reduceStock(Long productId, Integer number) {
        return this.baseMapper.reduceStock(productId, number);
    }

    @Override
    public int addStock(Long productId, Integer number) {

        return this.baseMapper.addStock(productId, number);
    }
}
