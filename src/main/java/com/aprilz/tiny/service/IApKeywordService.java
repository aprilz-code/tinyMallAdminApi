package com.aprilz.tiny.service;

import com.aprilz.tiny.mbg.entity.ApKeyword;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 关键字表 服务类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
public interface IApKeywordService extends IService<ApKeyword> {

    Page<ApKeyword> querySelective(String keyword, String url, Integer page, Integer limit, String sort, String order);
}
