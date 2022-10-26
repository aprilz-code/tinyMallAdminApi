package com.aprilz.tiny.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApCategory;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApCategoryService;
import com.aprilz.tiny.vo.CategoryVo;
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
@RequestMapping("/category")
@Validated
@Slf4j
@Api("类目管理")
public class ApCategoryController {


    @Autowired
    private IApCategoryService categoryService;

    @PreAuthorize("hasAuthority('admin:category:list')")
    @ApiOperation("商城管理-类目管理-查询")
    @GetMapping("/list")
    public CommonResult list() {
        List<ApCategory> categoryList = categoryService.queryByPid(0L);
        if (CollUtil.isEmpty(categoryList)) {
            CommonResult.success(CollUtil.newArrayList());
        }

        List<CategoryVo> classes = CollUtil.newArrayList();
        for (ApCategory category : categoryList) {
            CategoryVo categoryVO = new CategoryVo();
            BeanUtil.copyProperties(category, categoryVO);
            categoryVO.setChildren(categoryService.queryByPid(category.getId()));
            classes.add(categoryVO);
        }
        return CommonResult.success(classes);
    }


    @PreAuthorize("hasAuthority('admin:category:create')")
    @ApiOperation("商城管理-类目管理-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApCategory category) {

        categoryService.save(category);
        return CommonResult.success(category);
    }

    @PreAuthorize("hasAuthority('admin:category:read')")
    @ApiOperation("商城管理-类目管理-详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApCategory category = categoryService.getById(id);
        return CommonResult.success(category);
    }

    @PreAuthorize("hasAuthority('admin:category:update')")
    @ApiOperation("商城管理-类目管理-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApCategory category) {
        Long id = category.getId();
        if (id == null) {
            return CommonResult.paramsError();
        }

        if (!categoryService.updateById(category)) {
            return CommonResult.error("编辑异常");
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:category:delete')")
    @ApiOperation("商城管理-类目管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam param) {

        categoryService.removeById(param.getId());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:category:list')")
    @ApiOperation("商城管理-类目管理-所有1级分类")
    @GetMapping("/l1")
    public CommonResult catL1() {
        // 所有一级分类目录
        List<ApCategory> l1CatList = categoryService.queryL1();
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (ApCategory category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }
        return CommonResult.success(data);
    }
}
