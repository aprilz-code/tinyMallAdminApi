package com.aprilz.tiny.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.plugin.notify.NotifyService;
import com.aprilz.tiny.common.plugin.notify.NotifyType;

import com.aprilz.tiny.common.utils.CurrencyUtil;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.config.wx.WxPayV3Properties;
import com.aprilz.tiny.express.ExpressService;
import com.aprilz.tiny.express.dao.ExpressInfo;
import com.aprilz.tiny.mall.AftersaleConstant;
import com.aprilz.tiny.mall.CouponUserConstant;
import com.aprilz.tiny.mall.WxResponseCode;
import com.aprilz.tiny.mall.utils.OrderHandleOption;
import com.aprilz.tiny.mall.utils.OrderUtil;
import com.aprilz.tiny.mapper.ApOrderMapper;
import com.aprilz.tiny.mbg.entity.*;
import com.aprilz.tiny.param.*;
import com.aprilz.tiny.service.*;
import com.aprilz.tiny.vo.OrderDetailVo;
import com.aprilz.tiny.vo.OrdersListVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ijpay.core.IJPayHttpResponse;
import com.ijpay.core.enums.RequestMethod;
import com.ijpay.core.kit.PayKit;
import com.ijpay.core.kit.WxPayKit;
import com.ijpay.wxpay.WxPayApi;
import com.ijpay.wxpay.enums.WxApiType;
import com.ijpay.wxpay.enums.WxDomain;
import com.ijpay.wxpay.model.v3.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Slf4j
@Service
public class ApOrderServiceImpl extends ServiceImpl<ApOrderMapper, ApOrder> implements IApOrderService {

    @Autowired
    private IApGrouponService grouponService;

    @Autowired
    private IApOrderGoodsService orderGoodsService;

    @Autowired
    private ExpressService expressService;


    @Autowired
    private IApCouponUserService couponUserService;

    @Autowired
    private IApGoodsProductService productService;

    @Autowired
    private NotifyService notifyService;


    @Resource
    private WxPayV3Properties wxPayV3Properties;

    private String serialNo;

    @Autowired
    private IApAftersaleService aftersaleService;

    @Autowired
    private IApCommentService commentService;

    @Override
    public void updateAftersaleStatus(Long orderId, Integer statusRequest) {
        ApOrder order = new ApOrder();
        order.setId(orderId);
        order.setAftersaleStatus(statusRequest);
        this.updateById(order);
    }

    @Override
    public Map<String, Integer> orderInfo(Long userId) {
        List<ApOrder> orders = this.lambdaQuery().eq(ApOrder::getUserId, userId).eq(ApOrder::getDeleteFlag, false)
                .select(ApOrder::getComments, ApOrder::getOrderStatus).list();

        int unpaid = 0;
        int unship = 0;
        int unrecv = 0;
        int uncomment = 0;
        for (ApOrder order : orders) {
            if (OrderUtil.isCreateStatus(order)) {
                unpaid++;
            } else if (OrderUtil.isPayStatus(order)) {
                unship++;
            } else if (OrderUtil.isShipStatus(order)) {
                unrecv++;
            } else if (OrderUtil.isConfirmStatus(order) || OrderUtil.isAutoConfirmStatus(order)) {
                uncomment += order.getComments();
            } else {
                // do nothing
            }
        }

        Map<String, Integer> orderInfo = new HashMap();
        orderInfo.put("unpaid", unpaid);
        orderInfo.put("unship", unship);
        orderInfo.put("unrecv", unrecv);
        orderInfo.put("uncomment", uncomment);
        return orderInfo;

    }

    @Override
    public Page<OrdersListVo> pageVo(Integer showType, Integer page, Integer limit, String sort, String order) {
        Long userId = UserUtil.getUser().getId();
        Page<OrdersListVo> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        List<Integer> orderStatus = OrderUtil.orderStatus(showType);
        QueryWrapper<OrdersListVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_flag", false);
        queryWrapper.eq("user_id", userId);
        queryWrapper.in("order_status", orderStatus);
        Page<OrdersListVo> orderPages = this.baseMapper.pageVo(pages, queryWrapper);
        orderPages.getRecords().stream().forEach(orders -> {
            orders.setOrderStatusText(OrderUtil.orderStatusText(orders));
            orders.setHandleOption(OrderUtil.build(orders.getOrderStatus()));
            ApGroupon groupon = grouponService.queryByOrderId(orders.getId());
            if (groupon != null) {
                orders.setGroupin(true);
            } else {
                orders.setGroupin(false);
            }

            List<ApOrderGoods> orderGoodsList = orderGoodsService
                    .lambdaQuery().eq(ApOrderGoods::getOrderId, orders.getId()).eq(ApOrderGoods::getDeleteFlag, false).list();
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            for (ApOrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> orderGoodsVo = new HashMap<>();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("orderId", orderGoods.getOrderId());
                orderGoodsVo.put("goodsId", orderGoods.getGoodsId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("retailPrice", orderGoods.getPrice());
                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                orderGoodsVo.put("goodsSpecificationValues", orderGoods.getSpecifications());
                orderGoodsVoList.add(orderGoodsVo);
            }
            orders.setGoodsList(orderGoodsVoList);

        });

        return null;
    }

    @Override
    public Map<String, Object> detail(Long orderId) {
        Long userId = UserUtil.getUser().getId();
        Map<String, Object> result = new HashMap<>();
        ApOrder order = this.lambdaQuery().eq(ApOrder::getUserId, userId).eq(ApOrder::getId, orderId)
                .eq(ApOrder::getDeleteFlag, false).one();
        if (Objects.isNull(order)) {
            return result;
        }

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setId(order.getId());
        orderDetailVo.setOrderSn(order.getOrderSn());
        orderDetailVo.setMessage(order.getMessage());
        orderDetailVo.setCreateTime(order.getCreateTime());
        orderDetailVo.setConsignee(order.getConsignee());
        orderDetailVo.setMobile(order.getMobile());
        orderDetailVo.setAddress(order.getAddress());
        orderDetailVo.setGoodsPrice(order.getGoodsPrice());
        orderDetailVo.setCouponPrice(order.getCouponPrice());
        orderDetailVo.setFreightPrice(order.getFreightPrice());
        orderDetailVo.setActualPrice(order.getActualPrice());
        orderDetailVo.setOrderStatusText(OrderUtil.orderStatusText(order));
        orderDetailVo.setHandleOption(OrderUtil.build(order.getOrderStatus()));
        orderDetailVo.setAftersaleStatus(order.getAftersaleStatus());
        orderDetailVo.setExpCode(order.getShipChannel());
        orderDetailVo.setExpName(expressService.getVendorName(order.getShipChannel()));
        orderDetailVo.setExpNo(order.getShipSn());

        List<ApOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());

        result.put("orderInfo", orderDetailVo);
        result.put("orderGoods", orderGoodsList);


        // 订单状态为已发货且物流信息不为空
        //"YTO", "800669400640887922"
        if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
            if (ei == null) {
                result.put("expressInfo", new ArrayList<>());
            } else {
                result.put("expressInfo", ei);
            }
        } else {
            result.put("expressInfo", new ArrayList<>());
        }

        return result;
    }


    @Override
    public boolean updateWithOptimisticLocker(ApOrder order) {
        return this.lambdaUpdate().eq(ApOrder::getUpdateTime, new Date())
                .eq(ApOrder::getId, order.getId()).update(order);
    }

    @Override
    public void releaseCoupon(Long orderId) {
        List<ApCouponUser> couponUsers = couponUserService.lambdaQuery().eq(ApCouponUser::getDeleteFlag, false).eq(ApCouponUser::getOrderId, orderId).list();
        couponUsers.stream().forEach(couponUser -> {
            // 优惠券状态设置为可使用
            couponUserService.lambdaUpdate().set(ApCouponUser::getStatus, CouponUserConstant.STATUS_USABLE)
                    .eq(ApCouponUser::getId, couponUser.getId()).update();
        });

    }

    @Override
    @Transactional
    public CommonResult cancel(OrderCancelParam param) {
        ApAdmin user = UserUtil.getUser();
        Long userId = user.getId();
        ApOrder order = this.lambdaQuery().eq(ApOrder::getUserId, userId)
                .eq(ApOrder::getId, param.getOrderId()).eq(ApOrder::getDeleteFlag, false).one();
        if (Objects.isNull(order)) {
            return CommonResult.error();
        }
        Long orderId = order.getId();

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isCancel()) {
            return CommonResult.error(WxResponseCode.ORDER_INVALID_OPERATION, "订单不能取消");
        }

        // 设置订单已取消状态
        boolean update = this.lambdaUpdate().set(ApOrder::getOrderStatus, OrderUtil.STATUS_CANCEL)
                .set(ApOrder::getEndTime, new Date())
                .eq(ApOrder::getId, orderId).eq(ApOrder::getUpdateTime, order.getUpdateTime())
                .update();
        if (!update) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        List<ApOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
        for (ApOrderGoods orderGoods : orderGoodsList) {
            Long productId = orderGoods.getProductId();
            Integer number = orderGoods.getNumber();
            if (productService.addStock(productId, number) == 0) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }

        // 返还优惠券
        releaseCoupon(orderId);

        return CommonResult.success();
    }


    public Long countByOrderSn(Long userId, String orderSn) {
        return this.lambdaQuery().eq(ApOrder::getUserId, userId).eq(ApOrder::getOrderSn, orderSn)
                .eq(ApOrder::getDeleteFlag, false).count();
    }

    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }


    private String getSerialNumber() {
        if (StrUtil.isEmpty(serialNo)) {
            // 获取证书序列号
            X509Certificate certificate = PayKit.getCertificate(FileUtil.getInputStream(wxPayV3Properties.getCertPath()));
            serialNo = certificate.getSerialNumber().toString(16).toUpperCase();

//            System.out.println("输出证书信息:\n" + certificate.toString());
//            // 输出关键信息，截取部分并进行标记
//            System.out.println("证书序列号:" + certificate.getSerialNumber().toString(16));
//            System.out.println("版本号:" + certificate.getVersion());
//            System.out.println("签发者：" + certificate.getIssuerDN());
//            System.out.println("有效起始日期：" + certificate.getNotBefore());
//            System.out.println("有效终止日期：" + certificate.getNotAfter());
//            System.out.println("主体名：" + certificate.getSubjectDN());
//            System.out.println("签名算法：" + certificate.getSigAlgName());
//            System.out.println("签名：" + certificate.getSignature().toString());
        }
        System.out.println("serialNo:" + serialNo);
        return serialNo;
    }


    @Override
    public CommonResult delete(OrderDeleteParam param) {
        ApAdmin user = UserUtil.getUser();
        Long userId = user.getId();
        ApOrder order = this.lambdaQuery().eq(ApOrder::getId, param.getOrderId()).eq(ApOrder::getUserId, userId).eq(ApOrder::getDeleteFlag, false).one();
        if (order == null) {
            return CommonResult.error();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isDelete()) {
            return CommonResult.error(WxResponseCode.ORDER_INVALID_OPERATION, "订单不能删除");
        }

        this.lambdaUpdate().set(ApOrder::getDeleteFlag, false)
                .eq(ApOrder::getId, order.getId()).eq(ApOrder::getUpdateTime, order.getUpdateTime())
                .update();

        // 售后也同时删除
        aftersaleService.lambdaUpdate().set(ApAftersale::getDeleteFlag, false)
                .eq(ApAftersale::getOrderId, order.getId()).eq(ApAftersale::getUserId, userId)
                .update();

        return CommonResult.success();
    }

    @Override
    public CommonResult goods(Integer ogid) {
        ApAdmin user = UserUtil.getUser();
        Long userId = user.getId();
        ApOrderGoods orderGoods = orderGoodsService.lambdaQuery()
                .eq(ApOrderGoods::getId, ogid).eq(ApOrderGoods::getDeleteFlag, false).one();

        if (orderGoods != null) {
            Long orderId = orderGoods.getOrderId();
            ApOrder order = this.lambdaQuery().eq(ApOrder::getId, orderId).eq(ApOrder::getUserId, userId).eq(ApOrder::getDeleteFlag, false).one();
            if (!order.getUserId().equals(userId)) {
                return CommonResult.error();
            }
        }
        return CommonResult.success(orderGoods);
    }

    @Override
    public CommonResult comment(OrderCommentParam param) {
        ApAdmin user = UserUtil.getUser();
        Long userId = user.getId();

        ApOrderGoods orderGoods = orderGoodsService.lambdaQuery()
                .eq(ApOrderGoods::getId, param.getOrderGoodsId()).eq(ApOrderGoods::getDeleteFlag, false).one();
        if (orderGoods == null) {
            return CommonResult.error();
        }
        Long orderId = orderGoods.getOrderId();
        ApOrder order = this.lambdaQuery().eq(ApOrder::getId, orderId).eq(ApOrder::getUserId, userId).eq(ApOrder::getDeleteFlag, false).one();
        if (order == null) {
            return CommonResult.error();
        }
        if (!OrderUtil.isConfirmStatus(order) && !OrderUtil.isAutoConfirmStatus(order)) {
            return CommonResult.error(WxResponseCode.ORDER_INVALID_OPERATION, "当前商品不能评价");
        }
        if (!order.getUserId().equals(userId)) {
            return CommonResult.error(WxResponseCode.ORDER_INVALID, "当前商品不属于用户");
        }
        Long commentId = orderGoods.getComment();
        if (commentId == -1L) {
            return CommonResult.error(WxResponseCode.ORDER_COMMENT_EXPIRED, "当前商品评价时间已经过期");
        }
        if (commentId != 0L) {
            return CommonResult.error(WxResponseCode.ORDER_COMMENTED, "订单商品已评价");
        }

        String content = param.getContent();
        Integer star = param.getStar();
        if (star == null || star < 0 || star > 5) {
            return CommonResult.error();
        }
        Boolean hasPicture = param.getHasPicture();
        List<String> picUrls = param.getPicUrls();
        if (hasPicture == null || !hasPicture) {
            picUrls = new ArrayList<>(0);
        }

        // 1. 创建评价
        ApComment comment = new ApComment();
        comment.setUserId(userId);
        comment.setType(0);
        comment.setValueId(orderGoods.getGoodsId());
        comment.setStar(star);
        comment.setContent(content);
        comment.setHasPicture(hasPicture);
        comment.setPicUrls(picUrls.toArray(new String[]{}));
        commentService.save(comment);

        // 2. 更新订单商品的评价列表
        orderGoods.setComment(comment.getId());
        orderGoodsService.updateById(orderGoods);

        // 3. 更新订单中未评价的订单商品可评价数量
        Integer commentCount = order.getComments();
        if (commentCount > 0) {
            commentCount--;
        }
        order.setComments(commentCount);
        this.updateById(order);
        return CommonResult.success();
    }

    @Override
    @Transactional
    public CommonResult doRefund(ApAftersale aftersaleOne) {
        Long orderId = aftersaleOne.getOrderId();
        ApOrder order = this.getById(orderId);

        List<ApOrderGoods> goods = orderGoodsService.queryByOid(orderId);

        List<RefundGoodsDetail> list = new ArrayList<>();

        // 订单金额：单位（分）
        goods.stream().forEach(good -> {
            // 微信退款
            RefundGoodsDetail refundGoodsDetail = new RefundGoodsDetail()
                    .setMerchant_goods_id(good.getGoodsSn())
                    .setGoods_name(good.getGoodsName())
                    .setUnit_price(CurrencyUtil.fen(good.getPrice()))
                    .setRefund_amount(CurrencyUtil.fen(good.getPrice()) * good.getNumber())
                    .setRefund_quantity(good.getNumber());
            list.add(refundGoodsDetail);
        });

        try {
            RefundModel refundModel = new RefundModel()
                    .setOut_trade_no(order.getOrderSn())
                    .setOut_refund_no(order.getOrderSn())
                    .setReason("IJPay 测试退款")
                    .setNotify_url(wxPayV3Properties.getDomain().concat("/v3/refundNotify"))
                    .setAmount(new RefundAmount()
                            .setRefund(CurrencyUtil.fen(aftersaleOne.getAmount()))
                            .setTotal(CurrencyUtil.fen(order.getActualPrice()))
                            .setCurrency("CNY"))
                    .setGoods_detail(list);

            log.info("退款参数 {}", JSONUtil.toJsonStr(refundModel));
            IJPayHttpResponse response = WxPayApi.v3(
                    RequestMethod.POST,
                    WxDomain.CHINA.toString(),
                    WxApiType.DOMESTIC_REFUNDS.toString(),
                    wxPayV3Properties.getMchId(),
                    getSerialNumber(),
                    null,
                    wxPayV3Properties.getKeyPath(),
                    JSONUtil.toJsonStr(refundModel)
            );

            // 根据证书序列号查询对应的证书来验证签名结果
            if (response.getStatus() == HttpStatus.HTTP_OK) {
                boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Properties.getPlatformCertPath());
                log.info("verifySignature: {}", verifySignature);
                log.info("退款响应 {}", response);
                if (verifySignature) {
                    //校验成功
                    String body = response.getBody();
                    //TODO 退款这里需要debug，改  还有释放优惠卷呢？？  +一张退款流水表
                    aftersaleOne.setStatus(AftersaleConstant.STATUS_REFUND);
                    aftersaleOne.setHandleTime(new Date());
                    aftersaleService.updateById(aftersaleOne);

                    this.updateAftersaleStatus(orderId, AftersaleConstant.STATUS_REFUND);

                    // NOTE
                    // 如果是“退货退款”类型的售后，这里退款说明用户的货已经退回，则需要商品货品数量增加
                    // 开发者也可以删除一下代码，在其他地方增加商品货品入库操作
                    if (aftersaleOne.getType().equals(AftersaleConstant.TYPE_GOODS_REQUIRED)) {
                        List<ApOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
                        for (ApOrderGoods orderGoods : orderGoodsList) {
                            Long productId = orderGoods.getProductId();
                            Integer number = orderGoods.getNumber();
                            productService.addStock(productId, number);
                        }
                    }

                    // 发送短信通知，这里采用异步发送
                    // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
                    // TODO 注意订单号只发后6位
                    notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND,
                            new String[]{order.getOrderSn().substring(8, 14)});
                    return CommonResult.success();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return CommonResult.error(ResultCode.ORDER_REFUND_FAILED);

    }
}
