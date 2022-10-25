package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.param.GoodsAllinoneParam;
import com.aprilz.tiny.service.IApGoodsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/goods")
@Validated
@Slf4j
@Api("商品管理")
public class ApGoodsController {

    @Autowired
    private IApGoodsService goodsService;

    /**
     * 查询商品
     *
     * @param goodsId
     * @param goodsSn
     * @param name
     * @param page
     * @param limit
     * @param sort
     * @param order
     * @return
     */
    @PreAuthorize("hasAuthority('admin:goods:list')")
    @ApiOperation("商品管理-商品管理-查询")
    @GetMapping("/list")
    public CommonResult list(Integer goodsId, String goodsSn, String name,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApGoods> goodsList = goodsService.querySelective(goodsId, goodsSn, name, page, limit, sort, order);
        return CommonResult.success(goodsList);
    }

    @GetMapping("/catAndBrand")
    public CommonResult catAndBrand() {
        return CommonResult.success(goodsService.catAndBrand());
    }

    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
    @PreAuthorize("hasAuthority('admin:goods:update')")
    @ApiOperation("商品管理-商品管理-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody GoodsAllinoneParam goodsAllinone) {
        return goodsService.update(goodsAllinone);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @PreAuthorize("hasAuthority('admin:goods:delete')")
    @ApiOperation("商品管理-商品管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam goods) {
        goodsService.delete(goods);
        return CommonResult.success();
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
    @PreAuthorize("hasAuthority('admin:goods:create')")
    @ApiOperation("商品管理-商品管理-上架")
    @PostMapping("/create")
    public CommonResult create(@RequestBody GoodsAllinoneParam goodsAllinone) {
        return goodsService.create(goodsAllinone);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @PreAuthorize("hasAuthority('admin:goods:read')")
    @ApiOperation("商品管理-商品管理-详情")
    @GetMapping("/detail")
    public CommonResult detail(@NotNull Integer id) {
        return CommonResult.success(goodsService.detail(id));

    }

}
