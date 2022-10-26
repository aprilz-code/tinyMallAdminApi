package com.aprilz.tiny.mapper;

import com.aprilz.tiny.mbg.entity.ApOrderGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单商品表 Mapper 接口
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface ApOrderGoodsMapper extends BaseMapper<ApOrderGoods> {

    List<Map> statGoods();
}
