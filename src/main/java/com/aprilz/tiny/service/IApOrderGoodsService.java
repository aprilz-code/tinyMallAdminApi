package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApOrderGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 订单商品表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApOrderGoodsService extends IService<ApOrderGoods> {

    List<ApOrderGoods> queryByOid(Long orderId);

    Long getComments(Long orderId);
}
