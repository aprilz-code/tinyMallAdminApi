package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApFootprintMapper;
import com.aprilz.tiny.mbg.entity.ApFootprint;
import com.aprilz.tiny.service.IApFootprintService;
import com.aprilz.tiny.vo.ApFootprintVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 用户浏览足迹表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApFootprintServiceImpl extends ServiceImpl<ApFootprintMapper, ApFootprint> implements IApFootprintService {

    @Override
    public Page<ApFootprintVo> queryByCreateTime(Long userId, Integer page, Integer limit) {
        Page<ApFootprintVo> pages = new Page(page, limit);

        QueryWrapper<ApFootprintVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("p.user_id", userId);
        queryWrapper.eq("p.delete_flag", false);
        queryWrapper.eq("g.delete_flag", false);
        queryWrapper.orderByDesc("p.create_time");

        return this.baseMapper.getPageVo(pages, queryWrapper);
    }

    @Override
    public Page<ApFootprint> querySelective(Integer userId, Integer goodsId, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApFootprint> query = this.lambdaQuery();
        if (Objects.nonNull(userId)) {
            query.eq(ApFootprint::getUserId, userId);
        }

        if (Objects.nonNull(userId)) {
            query.eq(ApFootprint::getUserId, userId);
        }
        query.eq(ApFootprint::getDeleteFlag, false);
        Page<ApFootprint> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }
}
