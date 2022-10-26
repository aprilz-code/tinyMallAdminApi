package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApKeyword;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApKeywordService;
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
@RequestMapping("/keyword")
@Validated
@Slf4j
@Api("关键词管理")
public class ApKeywordController {

    @Autowired
    private IApKeywordService keywordService;

    @PreAuthorize("hasAuthority('admin:keyword:list')")
    @ApiOperation("商场管理-关键词-查询")
    @GetMapping("/list")
    public CommonResult list(String keyword, String url,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApKeyword> keywordList = keywordService.querySelective(keyword, url, page, limit, sort, order);
        return CommonResult.success(keywordList);
    }


    @PreAuthorize("hasAuthority('admin:keyword:create')")
    @ApiOperation("商场管理-关键词-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApKeyword keyword) {
        keywordService.save(keyword);
        return CommonResult.success(keyword);
    }

    @PreAuthorize("hasAuthority('admin:keyword:read')")
    @ApiOperation("商场管理-关键词-详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApKeyword keyword = keywordService.getById(id);
        return CommonResult.success(keyword);
    }

    @PreAuthorize("hasAuthority('admin:keyword:update')")
    @ApiOperation("商场管理-关键词-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApKeyword keyword) {
        if (!keywordService.updateById(keyword)) {
            return CommonResult.error("编辑异常");
        }
        return CommonResult.success(keyword);
    }

    @PreAuthorize("hasAuthority('admin:keyword:delete')")
    @ApiOperation("商场管理-关键词-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam param) {
        keywordService.removeById(param.getId());
        return CommonResult.success();
    }

}
