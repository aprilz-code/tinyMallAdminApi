package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApGrouponRules;
import com.aprilz.tiny.vo.GrouponRuleVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 团购规则表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApGrouponRulesService extends IService<ApGrouponRules> {

    Page<GrouponRuleVo> queryPage(Integer page, Integer size);

    Page<GrouponRuleVo> queryPage(Integer page, Integer size, String sort, String order);

    Page<ApGrouponRules> querySelective(Long goodsId, Integer page, Integer limit, String sort, String order);

    boolean countByGoodsId(Long goodsId);

}
