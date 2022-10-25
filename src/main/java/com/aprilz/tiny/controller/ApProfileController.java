package com.aprilz.tiny.controller;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.utils.UserUtil;
import com.aprilz.tiny.mbg.entity.ApAdmin;
import com.aprilz.tiny.mbg.entity.ApNotice;
import com.aprilz.tiny.mbg.entity.ApNoticeAdmin;
import com.aprilz.tiny.param.BatchReceptParam;
import com.aprilz.tiny.param.CatNoticeParam;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.param.ModifyPassParam;
import com.aprilz.tiny.service.IApAdminService;
import com.aprilz.tiny.service.IApNoticeAdminService;
import com.aprilz.tiny.service.IApNoticeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@Validated
@Slf4j
@Api("用户中心管理")
public class ApProfileController {

    @Autowired
    private IApAdminService adminService;
    @Autowired
    private IApNoticeService noticeService;
    @Autowired
    private IApNoticeAdminService noticeAdminService;


    @PostMapping("/password")
    public CommonResult create(@RequestBody ModifyPassParam param) {
        String oldPassword = param.getOldPassword();
        String newPassword = param.getNewPassword();

        ApAdmin user = UserUtil.getUser();
        Long id = user.getId();
        user = adminService.getById(id);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            CommonResult.error(ResultCode.OLD_PASS_ERROR);
        }
        String encodedNewPassword = encoder.encode(newPassword);
        user.setPassword(encodedNewPassword);

        adminService.updateById(user);
        return CommonResult.success();
    }


    @GetMapping("/nnotice")
    public CommonResult nNotice() {
        ApAdmin user = UserUtil.getUser();
        Long count = noticeAdminService.countUnread(user.getId());
        return CommonResult.success(count);
    }

    @GetMapping("/lsnotice")
    public CommonResult lsNotice(String title, String type,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer limit,
                                 @RequestParam(defaultValue = "create_time") String sort,
                                 @RequestParam(defaultValue = "desc") String order) {
        ApAdmin user = UserUtil.getUser();
        Page<ApNoticeAdmin> noticeList = noticeAdminService.querySelective(title, type, user.getId(), page, limit, sort, order);
        return CommonResult.success(noticeList);
    }

    @PostMapping("/catnotice")
    public CommonResult catNotice(@RequestBody CatNoticeParam param) {
        Long noticeId = param.getNoticeId();
        ApAdmin user = UserUtil.getUser();


        ApNoticeAdmin noticeAdmin = noticeAdminService.lambdaQuery()
                .eq(ApNoticeAdmin::getAdminId, user.getId())
                .eq(ApNoticeAdmin::getNoticeId, noticeId)
                .eq(ApNoticeAdmin::getDeleteFlag, false).one();
        if (noticeAdmin == null) {
            return CommonResult.error();
        }
        // 更新通知记录中的时间
        noticeAdmin.setReadTime(new Date());
        noticeAdminService.updateById(noticeAdmin);

        // 返回通知的相关信息
        Map<String, Object> data = new HashMap<>();
        ApNotice notice = noticeService.getById(noticeId);
        data.put("title", notice.getTitle());
        data.put("content", notice.getContent());
        data.put("time", notice.getUpdateTime());
        Long adminId = notice.getAdminId();
        if (adminId.equals(0)) {
            data.put("admin", "系统");
        } else {
            ApAdmin admin = adminService.getById(notice.getAdminId());
            data.put("admin", admin.getUsername());
            data.put("avatar", admin.getAvatar());
        }
        return CommonResult.success(data);
    }

    @PostMapping("/bcatnotice")
    public CommonResult bcatNotice(@RequestBody BatchReceptParam param) {
        List<Long> ids = param.getIds();
        ApAdmin user = UserUtil.getUser();
        noticeAdminService.markReadByIds(ids, user.getId());
        return CommonResult.success();
    }

    @PostMapping("/rmnotice")
    public CommonResult rmNotice(@RequestBody DeleteParam param) {
        Long id = param.getId();
        ApAdmin user = UserUtil.getUser();
        noticeAdminService.deleteById(id, user.getId());
        return CommonResult.success();
    }

    @PostMapping("/brmnotice")
    public CommonResult brmNotice(@RequestBody BatchReceptParam param) {
        List<Long> ids = param.getIds();
        ApAdmin user = UserUtil.getUser();
        noticeAdminService.deleteByIds(ids, user.getId());
        return CommonResult.success();
    }

}
