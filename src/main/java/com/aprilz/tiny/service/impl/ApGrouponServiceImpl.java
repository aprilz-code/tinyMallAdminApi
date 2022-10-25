package com.aprilz.tiny.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mall.GrouponConstant;
import com.aprilz.tiny.mapper.ApGrouponMapper;
import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.mbg.entity.ApGroupon;
import com.aprilz.tiny.mbg.entity.ApGrouponRules;
import com.aprilz.tiny.service.IApGoodsService;
import com.aprilz.tiny.service.IApGrouponRulesService;
import com.aprilz.tiny.service.IApGrouponService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 团购活动表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApGrouponServiceImpl extends ServiceImpl<ApGrouponMapper, ApGroupon> implements IApGrouponService {


    @Autowired
    private IApGrouponRulesService rulesService;
    @Autowired
    private IApGoodsService goodsService;
    @Autowired
    private IApGrouponService grouponService;

    @Override
    public List<ApGroupon> queryMyGroupon(Long userId) {
        return this.lambdaQuery().eq(ApGroupon::getUserId, userId).eq(ApGroupon::getCreatorUserId, userId)
                .eq(ApGroupon::getDeleteFlag, false).eq(ApGroupon::getGrouponId, 0L)
                .eq(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .orderByDesc(ApGroupon::getCreateTime).list();

    }

    @Override
    public List<ApGroupon> queryMyJoinGroupon(Long userId) {
        return this.lambdaQuery().eq(ApGroupon::getUserId, userId)
                .eq(ApGroupon::getDeleteFlag, false).eq(ApGroupon::getGrouponId, 0L)
                .eq(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .orderByDesc(ApGroupon::getCreateTime).list();
    }

    @Override
    public ApGroupon queryByOrderId(Long id) {
        return this.lambdaQuery().eq(ApGroupon::getOrderId, id)
                .eq(ApGroupon::getDeleteFlag, false).one();
    }

    @Override
    public Long countGroupon(Long grouponLinkId) {
        return this.lambdaQuery().eq(ApGroupon::getGrouponId, grouponLinkId)
                .ne(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .eq(ApGroupon::getDeleteFlag, false).count();
    }

    @Override
    public boolean hasJoin(Long userId, Long grouponLinkId) {
        return this.lambdaQuery().eq(ApGroupon::getUserId, userId)
                .eq(ApGroupon::getGrouponId, grouponLinkId)
                .ne(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .eq(ApGroupon::getDeleteFlag, false).exists();
    }

    @Override
    public List<ApGroupon> queryJoinRecord(Long grouponId) {
        return this.lambdaQuery().eq(ApGroupon::getGrouponId, grouponId)
                .ne(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                .eq(ApGroupon::getDeleteFlag, false).orderByDesc(ApGroupon::getCreateTime)
                .list();
    }

    @Override
    public Page<ApGroupon> querySelective(Long grouponRuleId, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApGroupon> query = this.lambdaQuery();
        if (Objects.nonNull(grouponRuleId)) {
            query.eq(ApGroupon::getRulesId, grouponRuleId);
        }
        query.eq(ApGroupon::getDeleteFlag, false).notIn(ApGroupon::getStatus, GrouponConstant.STATUS_NONE)
                //如果是开团用户，则groupon_id是0
                .eq(ApGroupon::getGrouponId, 0);
        Page<ApGroupon> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }

    @Override
    public Page<Map<String, Object>> listRecord(Long grouponRuleId, Integer page, Integer limit, String sort, String order) {
        Page<ApGroupon> apGrouponPage = this.querySelective(grouponRuleId, page, limit, sort, order);
        List<ApGroupon> records = apGrouponPage.getRecords();
        List<Map<String, Object>> groupons = new ArrayList<>();
        if (CollUtil.isNotEmpty(records)) {
            for (ApGroupon groupon : records) {
                Map<String, Object> recordData = new HashMap<>();
                List<ApGroupon> subGrouponList = grouponService.queryJoinRecord(groupon.getId());
                ApGrouponRules rules = rulesService.getById(groupon.getRulesId());
                ApGoods goods = goodsService.getById(rules.getGoodsId());
                recordData.put("groupon", groupon);
                recordData.put("subGroupons", subGrouponList);
                recordData.put("rules", rules);
                recordData.put("goods", goods);
                groupons.add(recordData);
            }
        }

        Page<Map<String, Object>> mapPage = new Page<>();
        mapPage.setTotal(apGrouponPage.getTotal());
        mapPage.setSize(apGrouponPage.getSize());
        mapPage.setPages(apGrouponPage.getPages());
        mapPage.setCurrent(apGrouponPage.getCurrent());
        mapPage.setRecords(groupons);
        return mapPage;
    }
}
