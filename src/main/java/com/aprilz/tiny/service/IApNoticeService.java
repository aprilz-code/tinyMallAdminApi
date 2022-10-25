package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApNotice;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 通知表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-10-25
 */
public interface IApNoticeService extends IService<ApNotice> {

    Page<ApNotice> querySelective(String title, String content, Integer page, Integer limit, String sort, String order);
}
