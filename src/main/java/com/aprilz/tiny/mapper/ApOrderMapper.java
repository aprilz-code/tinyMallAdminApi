package com.aprilz.tiny.mapper;

import com.aprilz.tiny.mbg.entity.ApOrder;
import com.aprilz.tiny.vo.OrderVo;
import com.aprilz.tiny.vo.OrdersListVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface ApOrderMapper extends BaseMapper<ApOrder> {

    @Select("SELECT id,order_sn,order_status,actual_price,aftersale_status FROM `ap_order`  ${ew.customSqlSegment}")
    Page<OrdersListVo> pageVo(Page<OrdersListVo> pages, @Param(Constants.WRAPPER) QueryWrapper<OrdersListVo> queryWrapper);

    List<Map> statOrder();

    @Select("select o.id, o.order_sn, o.order_status, o.actual_price, o.freight_price, o.create_time, o.message,\n" +
            "        o.consignee, o.address, o.mobile, o.pay_time, o.order_price, o.ship_channel, o.ship_sn,\n" +
            "        u.id user_id, u.nickname user_name, u.avatar user_avatar, o.integral_price,\n" +
            "        og.id ogid, og.goods_id, og.product_id, og.goods_name, og.pic_url goods_picture,\n" +
            "        og.specifications goods_specifications, og.number goods_number, og.price goods_price\n" +
            "        from ap_order o\n" +
            "        left join ap_user u\n" +
            "        on o.user_id = u.id\n" +
            "        left join ap_order_goods og\n" +
            "        on o.id = og.order_id\n" +
            "        left join ap_goods g\n" +
            "        on og.goods_id = g.id  ${ew.customSqlSegment}")
    Page<OrderVo> queryPage(Page<OrderVo> pages, @Param(Constants.WRAPPER)  QueryWrapper<OrderVo> query);
}
