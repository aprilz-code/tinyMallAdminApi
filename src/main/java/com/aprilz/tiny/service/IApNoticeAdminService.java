package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApNoticeAdmin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 通知管理员表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-10-25
 */
public interface IApNoticeAdminService extends IService<ApNoticeAdmin> {

    List<ApNoticeAdmin> queryByNoticeId(Long noticeId);

    boolean countReadByNoticeId(Long noticeId);

    Long countUnread(Long id);

    Page<ApNoticeAdmin> querySelective(String title, String type, Long id, Integer page, Integer limit, String sort, String order);

    void markReadByIds(List<Long> ids, Long userId);

    void deleteById(Long id, Long userId);

    void deleteByIds(List<Long> ids, Long id);
}
