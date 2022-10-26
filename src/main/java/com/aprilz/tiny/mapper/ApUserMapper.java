package com.aprilz.tiny.mapper;

import com.aprilz.tiny.mbg.entity.ApUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author aprilz
 * @since 2022-07-13
 */
public interface ApUserMapper extends BaseMapper<ApUser> {

    List<Map> statUser();

}
