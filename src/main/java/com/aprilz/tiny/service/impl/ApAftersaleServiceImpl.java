package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApAftersaleMapper;
import com.aprilz.tiny.mbg.entity.ApAftersale;
import com.aprilz.tiny.service.IApAftersaleService;
import com.aprilz.tiny.vo.ApAftersaleListVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

/**
 * <p>
 * 售后表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApAftersaleServiceImpl extends ServiceImpl<ApAftersaleMapper, ApAftersale> implements IApAftersaleService {

    @Override
    public IPage<ApAftersaleListVo> queryList(Long userId, Short status, Integer page, Integer size, String sort, String order) {
        Page<ApAftersaleListVo> pages = new Page(page, size);
        QueryWrapper<ApAftersaleListVo> queryWrapper = new QueryWrapper<>();
        if ("desc".equals(order)) {
            queryWrapper.orderByDesc(sort);
        } else {
            queryWrapper.orderByAsc(sort);
        }

        return this.baseMapper.queryList(pages, queryWrapper);

    }

    @Override
    public void deleteByOrderId(Long userId, Long orderId) {
        this.lambdaUpdate().set(ApAftersale::getDeleteFlag, false)
                .eq(ApAftersale::getOrderId, orderId).eq(ApAftersale::getUserId, userId).eq(ApAftersale::getDeleteFlag, false)
                .update();
    }

    @Override
    // TODO 这里应该产生一个唯一的编号，但是实际上这里仍然存在两个售后编号相同的可能性
    public String generateAftersaleSn(Long userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
        String now = df.format(LocalDate.now());
        String aftersaleSn = now + getRandomNum(6);
        while (countByAftersaleSn(userId, aftersaleSn) != 0) {
            aftersaleSn = now + getRandomNum(6);
        }
        return aftersaleSn;

    }

    @Override
    public Page<ApAftersale> querySelective(Integer orderId, String aftersaleSn, Short status, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApAftersale> query = this.lambdaQuery();

        if (Objects.nonNull(orderId)) {
            query.eq(ApAftersale::getOrderId, order);
        }

        if (StrUtil.isNotBlank(aftersaleSn)) {
            query.eq(ApAftersale::getAftersaleSn, aftersaleSn);
        }

        if (Objects.nonNull(status)) {
            query.eq(ApAftersale::getStatus, status);
        }

        query.eq(ApAftersale::getDeleteFlag, false);

        Page<ApAftersale> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);

    }

    public long countByAftersaleSn(Long userId, String aftersaleSn) {
        return this.lambdaQuery().eq(ApAftersale::getUserId, userId).eq(ApAftersale::getAftersaleSn, aftersaleSn)
                .count();
    }


    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
