package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApOrderGoodsMapper;
import com.aprilz.tiny.mbg.entity.ApOrderGoods;
import com.aprilz.tiny.service.IApOrderGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 订单商品表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApOrderGoodsServiceImpl extends ServiceImpl<ApOrderGoodsMapper, ApOrderGoods> implements IApOrderGoodsService {

    @Override
    public List<ApOrderGoods> queryByOid(Long orderId) {
        return this.lambdaQuery().eq(ApOrderGoods::getOrderId, orderId).eq(ApOrderGoods::getDeleteFlag, false).list();
    }

    @Override
    public Long getComments(Long orderId) {
        return this.lambdaQuery().eq(ApOrderGoods::getOrderId, orderId)
                .eq(ApOrderGoods::getDeleteFlag, false)
                .count();
    }
}
