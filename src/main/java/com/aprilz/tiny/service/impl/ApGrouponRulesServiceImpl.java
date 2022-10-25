package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mall.GrouponConstant;
import com.aprilz.tiny.mapper.ApGrouponRulesMapper;
import com.aprilz.tiny.mbg.entity.ApGrouponRules;
import com.aprilz.tiny.service.IApGrouponRulesService;
import com.aprilz.tiny.vo.GrouponRuleVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 团购规则表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApGrouponRulesServiceImpl extends ServiceImpl<ApGrouponRulesMapper, ApGrouponRules> implements IApGrouponRulesService {

    @Override
    public Page<GrouponRuleVo> queryPage(Integer page, Integer size) {
        return this.queryPage(page, size, "create_time", "desc");
    }

    @Override
    public Page<GrouponRuleVo> queryPage(Integer page, Integer size, String sort, String order) {
        Page<GrouponRuleVo> pages = new Page(page, size);
        QueryWrapper<GrouponRuleVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u.delete_flag", false);
        queryWrapper.eq("g.delete_flag", false);
        queryWrapper.eq("u.status", GrouponConstant.RULE_STATUS_ON);
        queryWrapper.gt("u.expire_time", new Date());

        if ("desc".equals(order)) {
            queryWrapper.orderByDesc("u." + sort);
        } else {
            queryWrapper.orderByAsc("u." + sort);
        }
        return this.baseMapper.queryPage(pages, queryWrapper);
    }

    @Override
    public Page<ApGrouponRules> querySelective(Long goodsId, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApGrouponRules> query = this.lambdaQuery();
        if (Objects.nonNull(goodsId)) {
            query.eq(ApGrouponRules::getGoodsId, goodsId);
        }
        query.eq(ApGrouponRules::getDeleteFlag, false);
        Page<ApGrouponRules> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }

    @Override
    public boolean countByGoodsId(Long goodsId) {
        return  this.lambdaQuery().eq(ApGrouponRules::getGoodsId, goodsId).eq(ApGrouponRules::getStatus, GrouponConstant.RULE_STATUS_ON)
                .eq(ApGrouponRules::getDeleteFlag, false).exists();
    }




}
