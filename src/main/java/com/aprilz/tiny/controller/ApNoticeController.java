package com.aprilz.tiny.controller;

import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.aprilz.tiny.mbg.entity.ApNotice;
import com.aprilz.tiny.mbg.entity.ApNoticeAdmin;
import com.aprilz.tiny.param.BatchReceptParam;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.service.IApAdminService;
import com.aprilz.tiny.service.IApNoticeAdminService;
import com.aprilz.tiny.service.IApNoticeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/notice")
@Validated
@Slf4j
@Api("通知管理")
public class ApNoticeController {

    @Autowired
    private IApNoticeService noticeService;
    @Autowired
    private IApAdminService adminService;
    @Autowired
    private IApNoticeAdminService noticeAdminService;

    @PreAuthorize("hasAuthority('admin:notice:list')")
    @ApiOperation("系统管理-通知管理-查询")
    @GetMapping("/list")
    public CommonResult list(String title, String content,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApNotice> noticeList = noticeService.querySelective(title, content, page, limit, sort, order);
        return CommonResult.success(noticeList);
    }


    @PreAuthorize("hasAuthority('admin:notice:create')")
    @ApiOperation("系统管理-通知管理-添加")
    @PostMapping("/create")
    public CommonResult create(@RequestBody ApNotice notice) {
        ApAdmin user = UserUtil.getUser();
        // 1. 添加通知记录
        notice.setAdminId(user.getId());
        noticeService.save(notice);
        // 2. 添加管理员通知记录
        List<ApAdmin> adminList = adminService.list();
        ApNoticeAdmin noticeAdmin = new ApNoticeAdmin();
        noticeAdmin.setNoticeId(notice.getId());
        noticeAdmin.setNoticeTitle(notice.getTitle());
        for (ApAdmin admin : adminList) {
            noticeAdmin.setAdminId(admin.getId());
            noticeAdminService.save(noticeAdmin);
        }
        return CommonResult.success(notice);
    }

    @PreAuthorize("hasAuthority('admin:notice:read')")
    @ApiOperation("系统管理-通知管理-详情")
    @GetMapping("/read")
    public CommonResult read(@NotNull Long id) {
        ApNotice notice = noticeService.getById(id);
        List<ApNoticeAdmin> noticeAdminList = noticeAdminService.queryByNoticeId(id);
        Map<String, Object> data = new HashMap<>(2);
        data.put("notice", notice);
        data.put("noticeAdminList", noticeAdminList);
        return CommonResult.success(data);
    }

    @PreAuthorize("hasAuthority('admin:notice:update')")
    @ApiOperation("系统管理-通知管理-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApNotice notice) {
        ApNotice originalNotice = noticeService.getById(notice.getId());
        if (originalNotice == null) {
            return CommonResult.validateFailed();
        }
        // 如果通知已经有人阅读过，则不支持编辑
        if (noticeAdminService.countReadByNoticeId(notice.getId())) {
            return CommonResult.error(ResultCode.NOTICE_UPDATE_NOT_ALLOWED);
        }
        ApAdmin user = UserUtil.getUser();
        // 1. 更新通知记录
        notice.setAdminId(user.getId());
        noticeService.updateById(notice);
        // 2. 更新管理员通知记录
        if (!originalNotice.getTitle().equals(notice.getTitle())) {
            noticeAdminService.lambdaUpdate().set(ApNoticeAdmin::getNoticeTitle, notice.getTitle())
                    .eq(ApNoticeAdmin::getNoticeId, notice.getId()).update();
        }
        return CommonResult.success(notice);
    }


    @PreAuthorize("hasAuthority('admin:notice:delete')")
    @ApiOperation("系统管理-通知管理-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody DeleteParam notice) {
        // 1. 删除通知管理员记录
        noticeAdminService.lambdaUpdate()
                .eq(ApNoticeAdmin::getNoticeId, notice.getId()).remove();
        // 2. 删除通知记录
        noticeService.removeById(notice.getId());
        return CommonResult.success();
    }

    @PreAuthorize("hasAuthority('admin:notice:batch-delete')")
    @ApiOperation("系统管理-通知管理-批量删除")
    @PostMapping("/batch-delete")
    public CommonResult batchDelete(@RequestBody BatchReceptParam body) {
        List<Long> ids = body.getIds();
        // 1. 删除通知管理员记录
        noticeAdminService.lambdaUpdate().in(ApNoticeAdmin::getNoticeId, ids).remove();
        // 2. 删除通知记录
        noticeService.removeBatchByIds(ids);
        return CommonResult.success();
    }
}
