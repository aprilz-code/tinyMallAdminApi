package com.aprilz.tiny.controller;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.mall.AftersaleConstant;
import com.aprilz.tiny.mbg.entity.ApAftersale;
import com.aprilz.tiny.param.BatchReceptParam;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApAftersaleService;
import com.aprilz.tiny.service.IApOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/admin/aftersale")
@Validated
@Slf4j
@Api(tags = "售后管理")
public class ApAftersaleController {

    @Autowired
    private IApAftersaleService aftersaleService;
    @Autowired
    private IApOrderService orderService;

    @PreAuthorize("hasAuthority('admin:aftersale:list')")
    @ApiOperation("商城管理-售后管理查询")
    @GetMapping("/list")
    public CommonResult list(Integer orderId, String aftersaleSn, Short status,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApAftersale> aftersaleList = aftersaleService.querySelective(orderId, aftersaleSn, status, page, limit, sort, order);
        return CommonResult.success(aftersaleList);
    }

    @PreAuthorize("hasAuthority('admin:aftersale:recept')")
    @ApiOperation("商城管理-售后管理-审核通过")
    @PostMapping("/recept")
    public CommonResult recept(@RequestBody ApAftersale afterSale) {
        Long id = afterSale.getId();
        ApAftersale aftersaleOne = aftersaleService.getById(id);
        if (aftersaleOne == null) {
            return CommonResult.error(ResultCode.AFTERSALE_NOT_EXIST);
        }
        Integer status = aftersaleOne.getStatus();
        if (!AftersaleConstant.STATUS_REQUEST.equals(status)) {
            return CommonResult.error(ResultCode.AFTERSALE_NOT_ALLOWED);
        }
        aftersaleOne.setStatus(AftersaleConstant.STATUS_RECEPT);
        aftersaleOne.setHandleTime(new Date());
        aftersaleService.updateById(aftersaleOne);

        // 订单也要更新售后状态
        orderService.updateAftersaleStatus(aftersaleOne.getOrderId(), AftersaleConstant.STATUS_RECEPT);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:aftersale:batch-recept')")
    @ApiOperation("商城管理-售后管理-批量通过")
    @PostMapping("/batch-recept")
    public CommonResult batchRecept(@RequestBody BatchReceptParam param) {
        List<Integer> ids = param.getIds();
        // NOTE
        // 批量操作中，如果一部分数据项失败，应该如何处理
        // 这里采用忽略失败，继续处理其他项。
        // 当然开发者可以采取其他处理方式，具体情况具体分析，例如利用事务回滚所有操作然后返回用户失败信息
        for (Integer id : ids) {
            ApAftersale aftersale = aftersaleService.getById(id);
            if (aftersale == null) {
                continue;
            }
            Integer status = aftersale.getStatus();
            if (!status.equals(AftersaleConstant.STATUS_REQUEST)) {
                continue;
            }
            aftersale.setStatus(AftersaleConstant.STATUS_RECEPT);
            aftersale.setHandleTime(new Date());
            aftersaleService.updateById(aftersale);

            // 订单也要更新售后状态
            orderService.updateAftersaleStatus(aftersale.getOrderId(), AftersaleConstant.STATUS_RECEPT);
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:aftersale:reject')")
    @ApiOperation("商城管理-售后管理-审核拒绝")
    @PostMapping("/reject")
    public CommonResult reject(@RequestBody DeleteParam aftersale) {
        Long id = aftersale.getId();
        ApAftersale aftersaleOne = aftersaleService.getById(id);
        if (aftersaleOne == null) {
            return CommonResult.error();
        }
        Integer status = aftersaleOne.getStatus();
        if (!status.equals(AftersaleConstant.STATUS_REQUEST)) {
            return CommonResult.error(ResultCode.AFTERSALE_NOT_ALLOWED);
        }
        aftersaleOne.setStatus(AftersaleConstant.STATUS_REJECT);
        aftersaleOne.setHandleTime(new Date());
        aftersaleService.updateById(aftersaleOne);

        // 订单也要更新售后状态
        orderService.updateAftersaleStatus(aftersaleOne.getOrderId(), AftersaleConstant.STATUS_REJECT);
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:aftersale:batch-reject')")
    @ApiOperation("商城管理-售后管理-批量拒绝")
    @PostMapping("/batch-reject")
    public Object batchReject(@RequestBody BatchReceptParam param) {
        List<Integer> ids = param.getIds();
        for (Integer id : ids) {
            ApAftersale aftersale = aftersaleService.getById(id);
            if (aftersale == null) {
                continue;
            }
            Integer status = aftersale.getStatus();
            if (!status.equals(AftersaleConstant.STATUS_REQUEST)) {
                continue;
            }
            aftersale.setStatus(AftersaleConstant.STATUS_REJECT);
            aftersale.setHandleTime(new Date());
            aftersaleService.updateById(aftersale);

            // 订单也要更新售后状态
            orderService.updateAftersaleStatus(aftersale.getOrderId(), AftersaleConstant.STATUS_REJECT);
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:aftersale:refund')")
    @ApiOperation("商城管理-售后管理-退款")
    @PostMapping("/refund")
    public CommonResult refund(@RequestBody ApAftersale afterSale) {
        Long id = afterSale.getId();
        ApAftersale aftersaleOne = aftersaleService.getById(id);
        if (aftersaleOne == null) {
            return CommonResult.validateFailed();
        }
        if (!aftersaleOne.getStatus().equals(AftersaleConstant.STATUS_RECEPT)) {
            return CommonResult.error(ResultCode.AFTERSALE_NOT_ALLOWED_REFUND);
        }


        return orderService.doRefund(aftersaleOne);

    }
}
