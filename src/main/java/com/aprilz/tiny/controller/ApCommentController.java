package com.aprilz.tiny.controller;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApComment;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Validated
@Slf4j
@Api("评论管理")
public class ApCommentController {

    @Autowired
    private IApCommentService commentService;

    @PreAuthorize("hasAuthority('admin:comment:list')")
    @ApiOperation("商品管理-评论管理-查询")
    @GetMapping("/list")
    public CommonResult list(String userId, String valueId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApComment> commentList = commentService.querySelective(userId, valueId, page, limit, sort, order);
        return CommonResult.success(commentList);
    }

    @PreAuthorize("hasAuthority('admin:comment:delete')")
    @ApiOperation("商品管理-评论管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam comment) {
        commentService.removeById(comment.getId());
        return CommonResult.success();
    }

}
