package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApFeedback;
import com.aprilz.tiny.service.IApFeedbackService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/feedback")
@Validated
@Slf4j
@Api("意见反馈管理")
public class ApFeedbackController {

    @Autowired
    private IApFeedbackService feedbackService;

    @PreAuthorize("hasAuthority('admin:feedback:list')")
    @ApiOperation("用户管理-意见反馈-查询")
    @GetMapping("/list")
    public CommonResult list(Integer userId, String username,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApFeedback> feedbackList = feedbackService.querySelective(userId, username, page, limit, sort,
                order);
        return CommonResult.success(feedbackList);
    }
}
