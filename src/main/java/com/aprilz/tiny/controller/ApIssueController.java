package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApIssue;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApIssueService;
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
@RequestMapping("/issue")
@Validated
@Slf4j
@Api("通用问题管理")
public class ApIssueController {

    @Autowired
    private IApIssueService issueService;

    @PreAuthorize("hasAuthority('admin:issue:list')")
    @ApiOperation("商场管理-通用问题-查询")
    @GetMapping("/list")
    public CommonResult list(String question,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApIssue> issueList = issueService.querySelective(question, page, limit, sort, order);
        return CommonResult.success(issueList);
    }


    @PreAuthorize("hasAuthority('admin:issue:create')")
    @ApiOperation("商场管理-通用问题-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApIssue issue) {
        issueService.save(issue);
        return CommonResult.success(issue);
    }

    @PreAuthorize("hasAuthority('admin:issue:read')")
    @ApiOperation("商场管理-通用问题-添加")
    @GetMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApIssue issue = issueService.getById(id);
        return CommonResult.success(issue);
    }

    @PreAuthorize("hasAuthority('admin:issue:update')")
    @ApiOperation("商场管理-通用问题-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApIssue issue) {
        if (!issueService.updateById(issue)) {
            return CommonResult.error("编辑异常");
        }
        return CommonResult.success(issue);
    }

    @PreAuthorize("hasAuthority('admin:issue:delete')")
    @ApiOperation("商场管理-通用问题-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam param) {
        issueService.removeById(param.getId());
        return CommonResult.success();
    }

}
