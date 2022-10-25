package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApCart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 购物车商品表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApCartService extends IService<ApCart> {

    List<ApCart> queryByUid(Long userId);

    ApCart queryExist(Long goodsId, Long productId, Long userId);

    ApCart findById(Long userId, Long id);

    void updateCheck(Long userId, List<Integer> productIds, Boolean isChecked);

    void deleteByPidsAndUid(List<Integer> productIds, Long userId);

    List<ApCart> queryByUidAndChecked(Long userId);

    boolean clearGoods(Long userId);

    boolean deleteById(Long cartId);

    void updateProduct(Long productId, String goodsSn, String goodsName, BigDecimal price, String url);
}
