package com.aprilz.tiny.service;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApAftersale;
import com.aprilz.tiny.mbg.entity.ApOrder;
import com.aprilz.tiny.param.*;
import com.aprilz.tiny.vo.OrderVo;
import com.aprilz.tiny.vo.OrdersListVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApOrderService extends IService<ApOrder> {

    void updateAftersaleStatus(Long orderId, Integer statusRequest);

    Map<String, Integer> orderInfo(Long userId);

    Page<OrdersListVo> pageVo(Integer showType, Integer page, Integer limit, String sort, String order);

    Map<String, Object> detail(Long orderId);


    boolean updateWithOptimisticLocker(ApOrder order);

    void releaseCoupon(Long orderId);

    CommonResult cancel(OrderCancelParam param);

    CommonResult delete(OrderDeleteParam param);

    CommonResult goods(Integer ogid);

    CommonResult comment(OrderCommentParam param);

    CommonResult doRefund(ApAftersale aftersaleOne);

    Object adminDetail(Long id);

    Page<OrderVo> querySelective(String nickname, String consignee, String orderSn, LocalDateTime start, LocalDateTime end, List<Integer> orderStatusArray, Integer page, Integer limit, String sort, String order);

    CommonResult doRefundWithOid(Long orderId);


    void ship(OrderShipParam param);

    CommonResult pay(OrderPrepayParam body);

    CommonResult backDelete(OrderDeleteParam param);

    CommonResult reply(OrderReplyParam param);
}
