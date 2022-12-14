package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApGroupon;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 团购活动表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApGrouponService extends IService<ApGroupon> {

    List<ApGroupon> queryMyGroupon(Long userId);

    List<ApGroupon> queryMyJoinGroupon(Long userId);

    ApGroupon queryByOrderId(Long id);

    Long countGroupon(Long grouponLinkId);

    boolean hasJoin(Long userId, Long grouponLinkId);

    List<ApGroupon> queryJoinRecord(Long grouponId);

    Page<ApGroupon> querySelective(Long grouponRuleId, Integer page, Integer limit, String sort, String order);

    Page<Map<String, Object>> listRecord(Long grouponRuleId, Integer page, Integer limit, String sort, String order);
}
