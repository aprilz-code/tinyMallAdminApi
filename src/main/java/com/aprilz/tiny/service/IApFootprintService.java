package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApFootprint;
import com.aprilz.tiny.vo.ApFootprintVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户浏览足迹表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApFootprintService extends IService<ApFootprint> {

    Page<ApFootprintVo> queryByCreateTime(Long userId, Integer page, Integer limit);

    Page<ApFootprint> querySelective(Integer userId, Integer goodsId, Integer page, Integer limit, String sort, String order);
}
