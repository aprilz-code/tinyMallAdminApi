package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApAddress;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 收货地址表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-18
 */
public interface IApAddressService extends IService<ApAddress> {

    void resetDefault(Long userId);

    ApAddress query(Long userId, Long addressId);

    ApAddress findDefault(Long userId);

    Page<ApAddress> querySelective(Integer userId, String name, Integer page, Integer limit, String sort, String order);
}
