package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApTopic;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 专题表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApTopicService extends IService<ApTopic> {

    List<ApTopic> query(Integer offset, Integer limit);

    Page<ApTopic> queryList(Integer page, Integer limit, String sort, String order);

    Page<ApTopic> queryRelatedList(Long id, int offset, int limit);

    Page<ApTopic> querySelective(String title, String subtitle, Integer page, Integer limit, String sort, String order);
}
