package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.express.ExpressService;
import com.aprilz.tiny.param.*;
import com.aprilz.tiny.service.IApOrderService;
import com.aprilz.tiny.vo.OrderVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Validated
@Slf4j
@Api("订单管理")
public class ApOrderController {

    @Autowired
    private IApOrderService orderService;
    @Autowired
    private ExpressService expressService;

    /**
     * 查询订单
     *
     * @param orderSn
     * @param orderStatusArray
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @PreAuthorize("hasAuthority('admin:order:list')")
    @ApiOperation("商场管理-订单管理-查询")
    @GetMapping("/list")
    public CommonResult list(String nickname, String consignee, String orderSn,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date start,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date end,
                             @RequestParam(required = false) List<Integer> orderStatusArray,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<OrderVo> page1 = orderService.querySelective(nickname, consignee, orderSn, start, end, orderStatusArray, page, limit, sort, order);
        return CommonResult.success(page1);
    }

    /**
     * 查询物流公司
     *
     * @return
     */
    @GetMapping("/channel")
    public CommonResult channel() {
        return CommonResult.success(expressService.getVendors());
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('admin:order:read')")
    @ApiOperation("商场管理-订单管理-详情")
    @GetMapping("/detail")
    public CommonResult<Map<String, Object>> detail(@NotNull Long id) {
        return CommonResult.success(orderService.adminDetail(id));
    }

    /**
     * 订单退款
     *
     * @return 订单退款操作结果
     */
    @PreAuthorize("hasAuthority('admin:order:refund')")
    @ApiOperation("商场管理-订单管理-订单退款")
    @PostMapping("/refund")
    public CommonResult refund(@RequestBody OrderRefundParam param) {
        return orderService.doRefundWithOid(param.getOrderId());
    }

    /**
     * 发货
     *
     * @param param
     * @return 订单操作结果
     */
    @PreAuthorize("hasAuthority('admin:order:ship')")
    @ApiOperation("商场管理-订单管理-订单发货")
    @PostMapping("/ship")
    public CommonResult ship(@RequestBody OrderShipParam param) {
        orderService.ship(param);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:order:pay')")
    @ApiOperation("商场管理-订单管理-订单收款")
    @PostMapping("/pay")
    public CommonResult pay(@RequestBody OrderPrepayParam body) {
        return orderService.pay(body);
    }

    /**
     * 删除订单
     *
     * @param param 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PreAuthorize("hasAuthority('admin:order:delete')")
    @ApiOperation("商场管理-订单管理-订单删除")
    @PostMapping("/delete")
    public CommonResult backDelete(@RequestBody OrderDeleteParam param) {
        return orderService.backDelete(param);
    }

    /**
     * 回复订单商品
     *
     * @param param 订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    @PreAuthorize("hasAuthority('admin:order:reply')")
    @ApiOperation("商场管理-订单管理-订单商品回复")
    @PostMapping("/reply")
    public CommonResult reply(@RequestBody OrderReplyParam param) {
        return orderService.reply(param);
    }
}
