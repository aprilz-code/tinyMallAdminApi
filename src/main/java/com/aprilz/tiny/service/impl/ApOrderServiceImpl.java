package com.aprilz.tiny.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.exception.ServiceException;
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
import com.aprilz.tiny.vo.OrderVo;
import com.aprilz.tiny.vo.OrdersListVo;
import com.aprilz.tiny.vo.UserVo;
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
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * ????????? ???????????????
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
    private IApUserService userService;


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


        // ????????????????????????????????????????????????
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
            // ?????????????????????????????????
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
            return CommonResult.error(WxResponseCode.ORDER_INVALID_OPERATION, "??????????????????");
        }

        // ???????????????????????????
        boolean update = this.lambdaUpdate().set(ApOrder::getOrderStatus, OrderUtil.STATUS_CANCEL)
                .set(ApOrder::getEndTime, new Date())
                .eq(ApOrder::getId, orderId).eq(ApOrder::getUpdateTime, order.getUpdateTime())
                .update();
        if (!update) {
            throw new RuntimeException("?????????????????????");
        }

        // ????????????????????????
        List<ApOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
        for (ApOrderGoods orderGoods : orderGoodsList) {
            Long productId = orderGoods.getProductId();
            Integer number = orderGoods.getNumber();
            if (productService.addStock(productId, number) == 0) {
                throw new RuntimeException("??????????????????????????????");
            }
        }

        // ???????????????
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
            // ?????????????????????
            X509Certificate certificate = PayKit.getCertificate(FileUtil.getInputStream(wxPayV3Properties.getCertPath()));
            serialNo = certificate.getSerialNumber().toString(16).toUpperCase();

//            System.out.println("??????????????????:\n" + certificate.toString());
//            // ????????????????????????????????????????????????
//            System.out.println("???????????????:" + certificate.getSerialNumber().toString(16));
//            System.out.println("?????????:" + certificate.getVersion());
//            System.out.println("????????????" + certificate.getIssuerDN());
//            System.out.println("?????????????????????" + certificate.getNotBefore());
//            System.out.println("?????????????????????" + certificate.getNotAfter());
//            System.out.println("????????????" + certificate.getSubjectDN());
//            System.out.println("???????????????" + certificate.getSigAlgName());
//            System.out.println("?????????" + certificate.getSignature().toString());
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
            return CommonResult.error(WxResponseCode.ORDER_INVALID_OPERATION, "??????????????????");
        }

        this.lambdaUpdate().set(ApOrder::getDeleteFlag, false)
                .eq(ApOrder::getId, order.getId()).eq(ApOrder::getUpdateTime, order.getUpdateTime())
                .update();

        // ?????????????????????
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
            return CommonResult.error(WxResponseCode.ORDER_INVALID_OPERATION, "????????????????????????");
        }
        if (!order.getUserId().equals(userId)) {
            return CommonResult.error(WxResponseCode.ORDER_INVALID, "???????????????????????????");
        }
        Long commentId = orderGoods.getComment();
        if (commentId == -1L) {
            return CommonResult.error(WxResponseCode.ORDER_COMMENT_EXPIRED, "????????????????????????????????????");
        }
        if (commentId != 0L) {
            return CommonResult.error(WxResponseCode.ORDER_COMMENTED, "?????????????????????");
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

        // 1. ????????????
        ApComment comment = new ApComment();
        comment.setUserId(userId);
        comment.setType(0);
        comment.setValueId(orderGoods.getGoodsId());
        comment.setStar(star);
        comment.setContent(content);
        comment.setHasPicture(hasPicture);
        comment.setPicUrls(picUrls.toArray(new String[]{}));
        commentService.save(comment);

        // 2. ?????????????????????????????????
        orderGoods.setComment(comment.getId());
        orderGoodsService.updateById(orderGoods);

        // 3. ??????????????????????????????????????????????????????
        Integer commentCount = order.getComments();
        if (commentCount > 0) {
            commentCount--;
        }
        order.setComments(commentCount);
        this.updateById(order);
        return CommonResult.success();
    }

    /**
     * ????????????
     * <p>
     * 1. ????????????????????????????????????;
     * 2. ??????????????????;
     * 3. ?????????????????????????????????
     * 4. ???????????????????????????
     * <p>
     * TODO
     * ???????????????????????????API???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * 1. ?????????????????????????????????????????????????????????????????????
     * 2. ???????????????litemall???????????????????????????????????????????????????????????????????????????
     *
     * @param orderId ???????????????{ orderId???xxx }
     * @return ????????????????????????
     */
    @Override
    @Transactional
    public CommonResult doRefundWithOid(Long orderId) {

        ApOrder order = this.getById(orderId);

//        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
//            return CommonResult.error("??????????????????");
//        }

        // ????????????????????????????????????????????????
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            return CommonResult.error(ResultCode.ORDER_CONFIRM_NOT_ALLOWED);
        }

        List<ApOrderGoods> goods = orderGoodsService.queryByOid(orderId);

        List<RefundGoodsDetail> list = new ArrayList<>();

        // ??????????????????????????????
        goods.stream().forEach(good -> {
            // ????????????
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
                    .setOut_refund_no("refund_" + order.getOrderSn())
                    .setReason("IJPay ????????????")
                    .setNotify_url(wxPayV3Properties.getDomain().concat("/v3/refundNotify"))
                    .setAmount(new RefundAmount()
                            .setRefund(CurrencyUtil.fen(order.getActualPrice()))
                            .setTotal(CurrencyUtil.fen(order.getActualPrice()))
                            .setCurrency("CNY"))
                    .setGoods_detail(list);

            log.info("???????????? {}", JSONUtil.toJsonStr(refundModel));
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

            // ???????????????????????????????????????????????????????????????
            if (response.getStatus() == HttpStatus.HTTP_OK) {
                boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Properties.getPlatformCertPath());
                log.info("verifySignature: {}", verifySignature);
                log.info("???????????? {}", response);
                if (verifySignature) {
                    //????????????
                    String body = response.getBody();

                    // ????????????????????????
                    order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
                    order.setEndTime(new Date());
                    // ??????????????????????????????
                    order.setRefundAmount(order.getActualPrice());
                    order.setRefundType("??????????????????");
                    order.setRefundContent(response.getBody());
                    order.setRefundTime(new Date());
                    if (!this.updateWithOptimisticLocker(order)) {
                        throw new RuntimeException("?????????????????????");
                    }

                    // ????????????????????????
                    for (ApOrderGoods orderGoods : goods) {
                        Long productId = orderGoods.getProductId();
                        Integer number = orderGoods.getNumber();
                        if (productService.addStock(productId, number) == 0) {
                            throw new RuntimeException("??????????????????????????????");
                        }
                    }

                    // ???????????????
                    List<ApCouponUser> couponUsers = couponUserService.lambdaQuery().eq(ApCouponUser::getOrderId, orderId)
                            .eq(ApCouponUser::getDeleteFlag, false).list();
                    for (ApCouponUser couponUser : couponUsers) {
                        // ?????????????????????????????????
                        couponUser.setStatus(CouponUserConstant.STATUS_USABLE);
                        couponUser.setUpdateTime(new Date());
                        couponUserService.updateById(couponUser);
                    }

                    // ?????????????????????????????????????????????
                    // ????????????????????????, ????????????????????????????????? [ ??????:{1} ] ???????????????????????????????????????
                    // TODO ????????????????????????6???
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

    @Override
    public void ship(OrderShipParam param) {
        Integer orderId = param.getOrderId();
        String shipSn = param.getShipSn();
        String shipChannel = param.getShipChannel();

        ApOrder order = this.getById(orderId);
        if (order == null) {
            throw new ServiceException("???????????????");
        }

        // ???????????????????????????????????????????????????
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            throw new ServiceException("???????????????????????????");
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipTime(new Date());
        if (!this.updateWithOptimisticLocker(order)) {
            throw new ServiceException("????????????");
        }

        //TODO ??????????????????????????????????????????????????????
        // ????????????????????????????????????:          *
        // "??????????????????????????????????????? {1}???????????? {2} ??????????????????"
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.SHIP, new String[]{shipChannel, shipSn});

    }

    @Override
    public CommonResult pay(OrderPrepayParam body) {
        Long orderId = body.getOrderId();


        ApOrder order = this.getById(orderId);
        if (order == null) {
            throw new ServiceException("???????????????");
        }
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_CREATE)) {
            return CommonResult.error(ResultCode.ORDER_PAY_FAILED);
        }

        order.setActualPrice(order.getActualPrice());
        order.setOrderStatus(OrderUtil.STATUS_PAY);
        if (!this.updateWithOptimisticLocker(order)) {
            return CommonResult.error("?????????????????????");
        }

        return CommonResult.success();
    }

    @Override
    public CommonResult backDelete(OrderDeleteParam param) {
        Long orderId = param.getOrderId();
        ApOrder order = this.getById(orderId);
        if (order == null) {
            return CommonResult.error("???????????????");
        }

        // ??????????????????????????????(????????????????????????????????????????????????????????????????????????)??????????????????
        Integer status = order.getOrderStatus();
        if (!status.equals(OrderUtil.STATUS_CANCEL) && !status.equals(OrderUtil.STATUS_AUTO_CANCEL) &&
                !status.equals(OrderUtil.STATUS_CONFIRM) && !status.equals(OrderUtil.STATUS_AUTO_CONFIRM) &&
                !status.equals(OrderUtil.STATUS_REFUND_CONFIRM)) {
            return CommonResult.error(ResultCode.ORDER_DELETE_FAILED);
        }
        // ????????????
        this.removeById(orderId);
        // ??????????????????
        orderGoodsService.lambdaUpdate().eq(ApOrderGoods::getOrderId, orderId).remove();
        // logHelper.logOrderSucceed("??????", "???????????? " + order.getOrderSn());
        return CommonResult.success();
    }

    @Override
    public CommonResult reply(OrderReplyParam param) {
        Long commentId = param.getCommentId();

        // ???????????????????????????
        ApComment comment = commentService.getById(commentId);
        if (comment == null) {
            return CommonResult.error("???????????????");
        }
        if (!StringUtils.isEmpty(comment.getAdminContent())) {
            return CommonResult.error(ResultCode.ORDER_REPLY_EXIST);
        }
        String content = param.getContent();
        // ??????????????????
        comment.setAdminContent(content);
        commentService.updateById(comment);
        return CommonResult.success();
    }

    @Override
    @Transactional
    public CommonResult doRefund(ApAftersale aftersaleOne) {
        Long orderId = aftersaleOne.getOrderId();
        ApOrder order = this.getById(orderId);

        List<ApOrderGoods> goods = orderGoodsService.queryByOid(orderId);

        List<RefundGoodsDetail> list = new ArrayList<>();

        // ??????????????????????????????
        goods.stream().forEach(good -> {
            // ????????????
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
                    .setOut_refund_no("refund_" + order.getOrderSn())
                    .setReason("IJPay ????????????")
                    .setNotify_url(wxPayV3Properties.getDomain().concat("/v3/refundNotify"))
                    .setAmount(new RefundAmount()
                            .setRefund(CurrencyUtil.fen(aftersaleOne.getAmount()))
                            .setTotal(CurrencyUtil.fen(order.getActualPrice()))
                            .setCurrency("CNY"))
                    .setGoods_detail(list);

            log.info("???????????? {}", JSONUtil.toJsonStr(refundModel));
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

            // ???????????????????????????????????????????????????????????????
            if (response.getStatus() == HttpStatus.HTTP_OK) {
                boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Properties.getPlatformCertPath());
                log.info("verifySignature: {}", verifySignature);
                log.info("???????????? {}", response);
                if (verifySignature) {
                    //????????????
                    String body = response.getBody();
                    //TODO ??????????????????debug??????  ??????????????????????????????  +?????????????????????
                    aftersaleOne.setStatus(AftersaleConstant.STATUS_REFUND);
                    aftersaleOne.setHandleTime(new Date());
                    aftersaleService.updateById(aftersaleOne);

                    this.updateAftersaleStatus(orderId, AftersaleConstant.STATUS_REFUND);

                    // NOTE
                    // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    // ????????????????????????????????????????????????????????????????????????????????????
                    if (aftersaleOne.getType().equals(AftersaleConstant.TYPE_GOODS_REQUIRED)) {
                        List<ApOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
                        for (ApOrderGoods orderGoods : orderGoodsList) {
                            Long productId = orderGoods.getProductId();
                            Integer number = orderGoods.getNumber();
                            productService.addStock(productId, number);
                        }
                    }

                    // ?????????????????????????????????????????????
                    // ????????????????????????, ????????????????????????????????? [ ??????:{1} ] ???????????????????????????????????????
                    // TODO ????????????????????????6???
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

    @Override
    public Map<String, Object> adminDetail(Long id) {
        ApOrder order = this.getById(id);
        List<ApOrderGoods> orderGoods = orderGoodsService.queryByOid(id);
        UserVo user = userService.findUserVoById(order.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderGoods", orderGoods);
        data.put("user", user);
        return data;
    }

    @Override
    public Page<OrderVo> querySelective(String nickname, String consignee, String orderSn, Date start, Date end, List<Integer> orderStatusArray, Integer page, Integer limit, String sort, String order) {
        Page<OrderVo> pages = new Page(page, limit);
        QueryWrapper<OrderVo> query = new QueryWrapper<>();
       if(StrUtil.isNotBlank(nickname)){
           query.like("u.nickname", nickname);
       }
        if(StrUtil.isNotBlank(consignee)){
            query.like("u.consignee", consignee);
        }

        if(StrUtil.isNotBlank(orderSn)){
            query.eq("u.order_sn", consignee);
        }

        if(Objects.nonNull(start)){
            query.ge("o.create_time", start);
        }

        if ("desc".equals(order)) {
            query.orderByDesc("o." + sort);
        } else {
            query.orderByAsc("u." + sort);
        }
        query.orderByDesc("o.id");


        if(Objects.nonNull(end)){
            query.le("o.create_time", start);
        }
        if(CollUtil.isNotEmpty(orderStatusArray)){
            query.in("o.order_status", orderStatusArray);
        }
        query.eq("o.delete_flag", false);
        query.eq("og.delete_flag", false);

        return this.getBaseMapper().queryPage(pages,query);
    }


}
