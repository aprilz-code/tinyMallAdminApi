package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApCartMapper;
import com.aprilz.tiny.mbg.entity.ApCart;
import com.aprilz.tiny.service.IApCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 购物车商品表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApCartServiceImpl extends ServiceImpl<ApCartMapper, ApCart> implements IApCartService {

    @Override
    public List<ApCart> queryByUid(Long userId) {
        return this.lambdaQuery().eq(ApCart::getUserId, userId).eq(ApCart::getDeleteFlag, false)
                .list();
    }

    @Override
    public ApCart queryExist(Long goodsId, Long productId, Long userId) {
        return this.lambdaQuery().eq(ApCart::getGoodsId, goodsId).eq(ApCart::getUserId, userId).eq(ApCart::getProductId, productId)
                .eq(ApCart::getDeleteFlag, false).one();
    }

    @Override
    public ApCart findById(Long userId, Long id) {
        return this.lambdaQuery().eq(ApCart::getId, id).eq(ApCart::getUserId, userId).eq(ApCart::getDeleteFlag, false)
                .one();
    }

    @Override
    public void updateCheck(Long userId, List<Integer> productIds, Boolean isChecked) {
        this.lambdaUpdate().set(ApCart::getChecked, isChecked).eq(ApCart::getUserId, userId)
                .in(ApCart::getProductId, productIds).eq(ApCart::getDeleteFlag, false)
                .update();
    }

    @Override
    public void deleteByPidsAndUid(List<Integer> productIds, Long userId) {
        this.lambdaUpdate().set(ApCart::getDeleteFlag, false).eq(ApCart::getUserId, userId)
                .in(ApCart::getProductId, productIds).eq(ApCart::getDeleteFlag, false)
                .update();
    }

    @Override
    public List<ApCart> queryByUidAndChecked(Long userId) {
        return this.lambdaQuery().eq(ApCart::getUserId, userId).eq(ApCart::getChecked, true)
                .eq(ApCart::getDeleteFlag, false).list();
    }

    @Override
    public boolean clearGoods(Long userId) {
        return this.lambdaUpdate().set(ApCart::getDeleteFlag, false).eq(ApCart::getUserId, userId)
                .eq(ApCart::getDeleteFlag, false).eq(ApCart::getChecked, true)
                .update();
    }

    @Override
    public boolean deleteById(Long cartId) {
        return this.lambdaUpdate().set(ApCart::getDeleteFlag, false).eq(ApCart::getId, cartId)
                .eq(ApCart::getDeleteFlag, false).eq(ApCart::getChecked, true)
                .update();
    }

    @Override
    public void updateProduct(Long productId, String goodsSn, String goodsName, BigDecimal price, String url) {
        this.lambdaUpdate().set(ApCart::getPrice, price)
                .set(ApCart::getPicUrl, url)
                .set(ApCart::getGoodsSn, goodsSn)
                .set(ApCart::getGoodsName, price)
                .eq(ApCart::getProductId, productId).update();
    }
}
