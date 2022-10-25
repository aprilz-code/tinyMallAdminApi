package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.plugin.delay.interfaces.impl.GrouponRuleExpiredTimerTrigger;
import com.aprilz.tiny.common.plugin.delay.model.TimeTriggerMsg;
import com.aprilz.tiny.common.utils.DateUtil;
import com.aprilz.tiny.mall.GrouponConstant;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.mbg.entity.ApGrouponRules;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApGoodsService;
import com.aprilz.tiny.service.IApGrouponRulesService;
import com.aprilz.tiny.service.IApGrouponService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/groupon")
@Validated
@Slf4j
@Api("团购管理")
public class ApGrouponController {

    @Autowired
    private IApGrouponRulesService rulesService;
    @Autowired
    private IApGoodsService goodsService;
    @Autowired
    private IApGrouponService grouponService;

    @Autowired
    private GrouponRuleExpiredTimerTrigger grouponRuleExpiredTimerTrigger;

    @PreAuthorize("hasAuthority('admin:groupon:read')")
    @ApiOperation("推广管理-团购管理-细查询")
    @GetMapping("/listRecord")
    public CommonResult listRecord(Long grouponRuleId,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer limit,
                                   @RequestParam(defaultValue = "create_time") String sort,
                                   @RequestParam(defaultValue = "desc") String order) {
        Page<Map<String, Object>> mapPage = grouponService.listRecord(grouponRuleId, page, limit, sort, order);


        return CommonResult.success(mapPage);
    }

    @PreAuthorize("hasAuthority('admin:groupon:list')")
    @ApiOperation("推广管理-团购管理-查询")
    @GetMapping("/list")
    public CommonResult list(Long goodsId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApGrouponRules> rulesList = rulesService.querySelective(goodsId, page, limit, sort, order);
        return CommonResult.success(rulesList);
    }


    @PreAuthorize("hasAuthority('admin:groupon:update')")
    @ApiOperation("推广管理-团购管理-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApGrouponRules grouponRules) {
        ApGrouponRules rules = rulesService.getById(grouponRules.getId());
        if (rules == null) {
            return CommonResult.error();
        }
        if (!rules.getStatus().equals(GrouponConstant.RULE_STATUS_ON)) {
            return CommonResult.error(ResultCode.GROUPON_GOODS_OFFLINE);
        }

        Long goodsId = grouponRules.getGoodsId();
        ApGoods goods = goodsService.getById(goodsId);
        if (goods == null) {
            return CommonResult.error();
        }

        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());

        if (!rulesService.updateById(grouponRules)) {
            return CommonResult.error("编辑异常");
        }

        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:groupon:create')")
    @ApiOperation("推广管理-团购管理-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApGrouponRules grouponRules) {

        Long goodsId = grouponRules.getGoodsId();
        ApGoods goods = goodsService.getById(goodsId);
        if (goods == null) {
            return CommonResult.error(ResultCode.GROUPON_GOODS_UNKNOWN);
        }
        if (rulesService.countByGoodsId(goodsId)) {
            return CommonResult.error(ResultCode.GROUPON_GOODS_EXISTED);
        }

        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());
        grouponRules.setStatus(GrouponConstant.RULE_STATUS_ON);
        rulesService.save(grouponRules);

        Date expire = grouponRules.getExpireTime();
        Long delayTime = DateUtil.getDelayTime(expire.getTime());
        // 团购过期任务
        TimeTriggerMsg timeTriggerMsg = new TimeTriggerMsg(delayTime, "grouponRuleExpiredTimeTriggerExecutor", grouponRules.getId());
        grouponRuleExpiredTimerTrigger.add(timeTriggerMsg);

        return CommonResult.success(grouponRules);
    }

    @PreAuthorize("hasAuthority('admin:groupon:delete')")
    @ApiOperation("推广管理-团购管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam param) {
        rulesService.removeById(param.getId());
        return CommonResult.success();
    }
}
