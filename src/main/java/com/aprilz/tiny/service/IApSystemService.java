package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApSystem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 系统配置表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
public interface IApSystemService extends IService<ApSystem> {

    Map<String, String> listForType(String type);

    void updateConfig(Map<String, String> map);

}
