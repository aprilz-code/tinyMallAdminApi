package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.mbg.entity.ApTopic;
import com.aprilz.tiny.param.BatchReceptParam;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApGoodsService;
import com.aprilz.tiny.service.IApTopicService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topic")
@Validated
@Slf4j
@Api("专题管理")
public class ApTopicController {

    @Autowired
    private IApTopicService topicService;
    @Autowired
    private IApGoodsService goodsService;

    @PreAuthorize("hasAuthority('admin:topic:user')")
    @ApiOperation("推广管理-专题管理-查询")
    @GetMapping("/list")
    public CommonResult list(String title, String subtitle,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApTopic> topicList = topicService.querySelective(title, subtitle, page, limit, sort, order);
        return CommonResult.success(topicList);
    }


    @PreAuthorize("hasAuthority('admin:topic:create')")
    @ApiOperation("推广管理-专题管理-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApTopic topic) {
        topicService.save(topic);
        return CommonResult.success(topic);
    }

    @PreAuthorize("hasAuthority('admin:topic:read')")
    @ApiOperation("推广管理-专题管理-详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApTopic topic = topicService.getById(id);
        Long[] goodsIds = topic.getGoods();
        List<ApGoods> goodsList = null;
        if (goodsIds == null || goodsIds.length == 0) {
            goodsList = new ArrayList<>();
        } else {
            goodsList = goodsService.lambdaQuery().in(ApGoods::getId,goodsIds)
            .eq(ApGoods::getIsOnSale, topic).eq(ApGoods::getDeleteFlag, false).list();
        }
        Map<String, Object> data = new HashMap<>(2);
        data.put("topic", topic);
        data.put("goodsList", goodsList);
        return CommonResult.success(data);
    }

    @PreAuthorize("hasAuthority('admin:topic:update')")
    @ApiOperation("推广管理-专题管理-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApTopic topic) {
        if (!topicService.updateById(topic)) {
            return CommonResult.error("编辑异常");
        }
        return CommonResult.success(topic);
    }

    @PreAuthorize("hasAuthority('admin:topic:delete')")
    @ApiOperation("推广管理-专题管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam param) {
        topicService.removeById(param.getId());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:topic:batch-delete')")
    @ApiOperation("推广管理-专题管理-批量删除")
    @PostMapping("/batch-delete")
    public CommonResult batchDelete(@RequestBody BatchReceptParam param) {
        List<Long> ids = param.getIds();
        topicService.removeBatchByIds(ids);
        return CommonResult.success();
    }
}
