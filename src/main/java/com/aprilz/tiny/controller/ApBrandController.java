package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApBrand;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApBrandService;
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
@RequestMapping("/brand")
@Validated
@Slf4j
@Api("品牌管理")
public class ApBrandController {

    @Autowired
    private IApBrandService brandService;

    @PreAuthorize("hasAuthority('admin:brand:list')")
    @ApiOperation("商城管理-品牌管理-查询")
    @GetMapping("/list")
    public CommonResult list(String id, String name,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApBrand> brandList = brandService.querySelective(id, name, page, limit, sort, order);
        return CommonResult.success(brandList);
    }


    @PreAuthorize("hasAuthority('admin:brand:create')")
    @ApiOperation("商城管理-品牌管理-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApBrand brand) {
        brandService.save(brand);
        return CommonResult.success(brand);
    }

    @PreAuthorize("hasAuthority('admin:brand:read')")
    @ApiOperation("商城管理-品牌管理-详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApBrand brand = brandService.getById(id);
        return CommonResult.success(brand);
    }

    @PreAuthorize("hasAuthority('admin:brand:update')")
    @ApiOperation("商城管理-品牌管理-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApBrand brand) {
        Long id = brand.getId();
        if (id == null) {
            return CommonResult.paramsError();
        }
        if (!brandService.updateById(brand)) {
            return CommonResult.paramsError();
        }
        return CommonResult.success(brand);
    }

    @PreAuthorize("hasAuthority('admin:brand:delete')")
    @ApiOperation("商城管理-品牌管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam param) {
        brandService.removeById(param.getId());
        return CommonResult.success();
    }

}
