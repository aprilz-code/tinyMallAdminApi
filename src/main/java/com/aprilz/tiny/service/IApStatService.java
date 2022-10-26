package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApUser;
import com.aprilz.tiny.vo.Token;
import com.aprilz.tiny.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 统计service
 * </p>
 *
 * @author aprilz
 * @since 2022-07-13
 */
public interface IApStatService  {

    List<Map> statUser();

    List<Map> statOrder();

    List<Map> statGoods();
}
