package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApGoodsSpecificationMapper;
import com.aprilz.tiny.mbg.entity.ApGoodsSpecification;
import com.aprilz.tiny.service.IApGoodsSpecificationService;
import com.aprilz.tiny.vo.GoodsSpecificationVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品规格表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-20
 */
@Service
public class ApGoodsSpecificationServiceImpl extends ServiceImpl<ApGoodsSpecificationMapper, ApGoodsSpecification> implements IApGoodsSpecificationService {

    @Override
    public List<GoodsSpecificationVo> getSpecificationVoList(Long goodId) {
        LambdaQueryWrapper<ApGoodsSpecification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoodsSpecification::getGoodsId, goodId).eq(ApGoodsSpecification::getDeleteFlag, false);
        List<ApGoodsSpecification> goodsSpecificationList = this.list(queryWrapper);

        Map<String, GoodsSpecificationVo> map = new HashMap<>();
        List<GoodsSpecificationVo> specificationVoList = new ArrayList<>();

        goodsSpecificationList.stream().forEach(goodsSpecification -> {
            String specification = goodsSpecification.getSpecification();
            GoodsSpecificationVo goodsSpecificationVo = map.get(specification);
            if (goodsSpecificationVo == null) {
                goodsSpecificationVo = new GoodsSpecificationVo();
                goodsSpecificationVo.setName(specification);
                List<ApGoodsSpecification> valueList = new ArrayList<>();
                valueList.add(goodsSpecification);
                goodsSpecificationVo.setValueList(valueList);
                map.put(specification, goodsSpecificationVo);
                specificationVoList.add(goodsSpecificationVo);
            } else {
                List<ApGoodsSpecification> valueList = goodsSpecificationVo.getValueList();
                valueList.add(goodsSpecification);
            }
        });

        return specificationVoList;
    }
}
