package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApKeywordMapper;
import com.aprilz.tiny.mbg.entity.ApKeyword;
import com.aprilz.tiny.service.IApKeywordService;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 关键字表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApKeywordServiceImpl extends ServiceImpl<ApKeywordMapper, ApKeyword> implements IApKeywordService {

    @Override
    public Page<ApKeyword> querySelective(String keyword, String url, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApKeyword> query = this.lambdaQuery();

        if (StrUtil.isNotBlank(keyword)) {
            query.eq(ApKeyword::getKeyword, keyword);
        }
        if (StrUtil.isNotBlank(url)) {
            query.like(ApKeyword::getUrl, url);
        }
        query.eq(ApKeyword::getDeleteFlag, false);
        Page<ApKeyword> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }
}
