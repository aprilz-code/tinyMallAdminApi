package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApAftersale;
import com.aprilz.tiny.vo.ApAftersaleListVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 售后表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApAftersaleService extends IService<ApAftersale> {

    IPage<ApAftersaleListVo> queryList(Long userId, Short status, Integer page, Integer limit, String sort, String order);

    void deleteByOrderId(Long userId, Long orderId);

    String generateAftersaleSn(Long id);

    Page<ApAftersale> querySelective(Integer orderId, String aftersaleSn, Short status, Integer page, Integer limit, String sort, String order);
}
