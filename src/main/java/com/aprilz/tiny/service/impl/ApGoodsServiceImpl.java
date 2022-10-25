package com.aprilz.tiny.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.api.PageVO;
import com.aprilz.tiny.common.api.ResultCode;
import com.aprilz.tiny.common.utils.PageUtil;
import com.aprilz.tiny.mapper.ApGoodsMapper;
import com.aprilz.tiny.mbg.entity.*;
import com.aprilz.tiny.param.DeleteParam;
import com.aprilz.tiny.param.GoodsAllinoneParam;
import com.aprilz.tiny.service.*;
import com.aprilz.tiny.vo.CatVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品基本信息表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApGoodsServiceImpl extends ServiceImpl<ApGoodsMapper, ApGoods> implements IApGoodsService {

    @Autowired
    private IApCategoryService categoryService;
    @Autowired
    private IApBrandService brandService;

    @Autowired
    private QCodeService qCodeService;

    @Autowired
    private IApCartService cartService;

    @Autowired
    private IApGoodsSpecificationService specificationService;

    @Autowired
    private IApGoodsAttributeService attributeService;

    @Autowired
    private IApGoodsProductService productService;

    @Override
    public List<ApGoods> queryByNew(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoods::getIsNew, true).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false)
                .orderByDesc(ApGoods::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }

    @Override
    public List<ApGoods> queryByHot(Integer offset, Integer limit) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApGoods::getIsHot, true).eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false)
                .orderByDesc(ApGoods::getCreateTime).last("limit " + offset + "," + limit);
        return this.list(queryWrapper);
    }

    @Override
    public Page<ApGoods> querySelective(Integer categoryId, Integer brandId, String keyword, Boolean isHot, Boolean isNew, Integer page, Integer limit, String sort, String order) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(categoryId) && categoryId != 0) {
            queryWrapper.eq(ApGoods::getCategoryId, categoryId);
        }
        if (Objects.nonNull(brandId)) {
            queryWrapper.eq(ApGoods::getBrandId, brandId);
        }
        if (BooleanUtil.isTrue(isHot)) {
            queryWrapper.eq(ApGoods::getIsHot, isHot);
        }

        if (BooleanUtil.isTrue(isNew)) {
            queryWrapper.eq(ApGoods::getIsNew, isNew);
        }

        //搜索keywords以及goodName
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(qw -> qw.like(ApGoods::getName, keyword).or().like(ApGoods::getKeywords, keyword));
        }
        queryWrapper.eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false);

        Page<ApGoods> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));

        return this.baseMapper.selectPage(pages, queryWrapper);

    }

    @Override
    public List<Long> getCategoryIds(Integer categoryId, Integer brandId, String keyword, Boolean isHot, Boolean isNew) {
        LambdaQueryWrapper<ApGoods> queryWrapper = new LambdaQueryWrapper<>();
        if (Objects.nonNull(categoryId) && categoryId != 0) {
            queryWrapper.eq(ApGoods::getCategoryId, categoryId);
        }
        if (Objects.nonNull(brandId)) {
            queryWrapper.eq(ApGoods::getBrandId, brandId);
        }
        if (BooleanUtil.isTrue(isHot)) {
            queryWrapper.eq(ApGoods::getIsHot, isHot);
        }

        if (BooleanUtil.isTrue(isNew)) {
            queryWrapper.eq(ApGoods::getIsNew, isNew);
        }

        //搜索keywords以及goodName
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(qw -> qw.like(ApGoods::getName, keyword).or().like(ApGoods::getKeywords, keyword));
        }
        queryWrapper.eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false);

        queryWrapper.select(ApGoods::getCategoryId);
        return this.list(queryWrapper).stream().map(ApGoods::getCategoryId).collect(Collectors.toList());
    }

    @Override
    public Page<ApGoods> querySelective(Integer goodsId, String goodsSn, String name, Integer page, Integer limit, String sort, String order) {
        LambdaQueryChainWrapper<ApGoods> query = this.lambdaQuery();
        if (Objects.nonNull(goodsId)) {
            query.eq(ApGoods::getId, goodsId);
        }
        if (StrUtil.isNotBlank(goodsSn)) {
            query.eq(ApGoods::getGoodsSn, goodsSn);
        }
        if (StrUtil.isNotBlank(name)) {
            query.like(ApGoods::getName, name);
        }

        query.eq(ApGoods::getDeleteFlag, false);
        Page<ApGoods> pages = PageUtil.initPage(new PageVO().setPageNumber(page).setPageSize(limit).setSort(sort).setOrder(order));
        return query.page(pages);
    }

    @Override
    public Object catAndBrand() {
        // http://element-cn.eleme.io/#/zh-CN/component/cascader
        // 管理员设置“所属分类”
        List<ApCategory> l1CatList = categoryService.queryL1();
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

        for (ApCategory l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());

            List<ApCategory> l2CatList = categoryService.queryByPid(l1.getId());
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (ApCategory l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }

        // http://element-cn.eleme.io/#/zh-CN/component/select
        // 管理员设置“所属品牌商”
        List<ApBrand> list = brandService.lambdaQuery().eq(ApBrand::getDeleteFlag, false).list();
        List<Map<String, Object>> brandList = new ArrayList<>(l1CatList.size());
        for (ApBrand brand : list) {
            Map<String, Object> b = new HashMap<>(2);
            b.put("value", brand.getId());
            b.put("label", brand.getName());
            brandList.add(b);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        data.put("brandList", brandList);
        return data;
    }

    /**
     * 编辑商品
     * <p>
     * NOTE：
     * 由于商品涉及到四个表，特别是ap_goods_product表依赖ap_goods_specification表，
     * 这导致允许所有字段都是可编辑会带来一些问题，因此这里商品编辑功能是受限制：
     * （1）ap_goods表可以编辑字段；
     * （2）ap_goods_specification表只能编辑pic_url字段，其他操作不支持；
     * （3）ap_goods_product表只能编辑price, number和url字段，其他操作不支持；
     * （4）ap_goods_attribute表支持编辑、添加和删除操作。
     * <p>
     * NOTE2:
     * 前后端这里使用了一个小技巧：
     * 如果前端传来的update_time字段是空，则说明前端已经更新了某个记录，则这个记录会更新；
     * 否则说明这个记录没有编辑过，无需更新该记录。
     * <p>
     * NOTE3:
     * （1）购物车缓存了一些商品信息，因此需要及时更新。
     * 目前这些字段是goods_sn, goods_name, price, pic_url。
     * （2）但是订单里面的商品信息则是不会更新。
     * 如果订单是未支付订单，此时仍然以旧的价格支付。
     */
    @Transactional
    @Override
    public CommonResult update(GoodsAllinoneParam goodsAllinone) {
        CommonResult error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        ApGoods goods = goodsAllinone.getGoods();
        ApGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        ApGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        ApGoodsProduct[] products = goodsAllinone.getProducts();

        //将生成的分享图片地址写入数据库
        String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
        goods.setShareUrl(url);

        // 商品表里面有一个字段retailPrice记录当前商品的最低价
        BigDecimal retailPrice = new BigDecimal(Integer.MAX_VALUE);
        for (ApGoodsProduct product : products) {
            BigDecimal productPrice = product.getPrice();
            if (retailPrice.compareTo(productPrice) == 1) {
                retailPrice = productPrice;
            }
        }
        goods.setRetailPrice(retailPrice);

        // 商品基本信息表ap_goods
        if (!this.updateById(goods)) {
            throw new RuntimeException("更新数据失败");
        }


        // 商品规格表ap_goods_specification
        for (ApGoodsSpecification specification : specifications) {
            // 目前只支持更新规格表的图片字段
            if (specification.getUpdateTime() == null) {
                specification.setSpecification(null);
                specification.setValue(null);
                specificationService.updateById(specification);
            }
        }

        // 商品货品表ap_product
        for (ApGoodsProduct product : products) {
            if (product.getUpdateTime() == null) {
                productService.updateById(product);
            }
        }

        // 商品参数表ap_goods_attribute
        for (ApGoodsAttribute attribute : attributes) {
            if (attribute.getId() == null || attribute.getId().equals(0)) {
                attribute.setGoodsId(goods.getId());
                attributeService.save(attribute);
            } else if (attribute.getDeleteFlag()) {
                attributeService.removeById(attribute.getId());
            } else if (attribute.getUpdateTime() == null) {
                attributeService.updateById(attribute);
            }
        }

        // 这里需要注意的是购物车ap_cart有些字段是拷贝商品的一些字段，因此需要及时更新
        // 目前这些字段是goods_sn, goods_name, price, pic_url
        for (ApGoodsProduct product : products) {
            cartService.updateProduct(product.getId(), goods.getGoodsSn(), goods.getName(), product.getPrice(), product.getUrl());
        }

        return CommonResult.success();
    }

    @Override
    public void delete(DeleteParam goods) {
        Long gid = goods.getId();
        this.removeById(gid);
        specificationService.removeById(gid);
        attributeService.removeById(gid);
        productService.removeById(gid);
    }

    @Override
    public CommonResult create(GoodsAllinoneParam goodsAllinone) {
        CommonResult error = validate(goodsAllinone);
        if (error != null) {
            return error;
        }

        ApGoods goods = goodsAllinone.getGoods();
        ApGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        ApGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        ApGoodsProduct[] products = goodsAllinone.getProducts();

        String name = goods.getName();
        if (this.checkExistByName(name)) {
            return CommonResult.error(ResultCode.GOODS_NAME_EXIST);
        }

        // 商品表里面有一个字段retailPrice记录当前商品的最低价
        BigDecimal retailPrice = new BigDecimal(Integer.MAX_VALUE);
        for (ApGoodsProduct product : products) {
            BigDecimal productPrice = product.getPrice();
            if (retailPrice.compareTo(productPrice) == 1) {
                retailPrice = productPrice;
            }
        }
        goods.setRetailPrice(retailPrice);

        // 商品基本信息表litemall_goods
        this.save(goods);

        //将生成的分享图片地址写入数据库
        String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
        if (StrUtil.isNotBlank(url)) {
            goods.setShareUrl(url);
            if (!this.updateById(goods)) {
                throw new RuntimeException("更新数据失败");
            }
        }

        // 商品规格表litemall_goods_specification
        for (ApGoodsSpecification specification : specifications) {
            specification.setGoodsId(goods.getId());
            specificationService.save(specification);
        }

        // 商品参数表litemall_goods_attribute
        for (ApGoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attributeService.save(attribute);
        }

        // 商品货品表litemall_product
        for (ApGoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            productService.save(product);
        }
        return CommonResult.success();
    }

    @Override
    public Object detail(Integer id) {
        ApGoods goods = this.getById(id);
        List<ApGoodsProduct> products = productService.lambdaQuery().eq(ApGoodsProduct::getGoodsId, id)
                .eq(ApGoodsProduct::getDeleteFlag, false).list();
        List<ApGoodsSpecification> specifications = specificationService.lambdaQuery().eq(ApGoodsSpecification::getGoodsId, id)
                .eq(ApGoodsSpecification::getDeleteFlag, false).list();
        List<ApGoodsAttribute> attributes = attributeService.lambdaQuery().eq(ApGoodsAttribute::getGoodsId, id)
                .eq(ApGoodsAttribute::getDeleteFlag, false).list();

        Long categoryId = goods.getCategoryId();
        ApCategory category = categoryService.getById(categoryId);
        Long[] categoryIds = new Long[]{};
        if (category != null) {
            Long parentCategoryId = category.getPid();
            categoryIds = new Long[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("specifications", specifications);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);

        return data;
    }

    private boolean checkExistByName(String name) {
        return this.lambdaQuery().eq(ApGoods::getName, name)
                .eq(ApGoods::getIsOnSale, true).eq(ApGoods::getDeleteFlag, false).exists();
    }


    private CommonResult validate(GoodsAllinoneParam goodsAllinone) {
        ApGoods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        if (StrUtil.isBlank(name)) {
            return CommonResult.validateFailed();
        }
        String goodsSn = goods.getGoodsSn();
        if (StrUtil.isBlank(goodsSn)) {
            return CommonResult.validateFailed();
        }
        // 品牌商可以不设置，如果设置则需要验证品牌商存在
        Long brandId = goods.getBrandId();
        if (brandId != null && brandId != 0) {
            if (brandService.getById(brandId) == null) {
                return CommonResult.validateFailed();
            }
        }
        // 分类可以不设置，如果设置则需要验证分类存在
        Long categoryId = goods.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            if (categoryService.getById(categoryId) == null) {
                return CommonResult.validateFailed();
            }
        }

        ApGoodsAttribute[] attributes = goodsAllinone.getAttributes();
        for (ApGoodsAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            if (StrUtil.isBlank(attr)) {
                return CommonResult.validateFailed();
            }
            String value = attribute.getValue();
            if (StrUtil.isBlank(value)) {
                return CommonResult.validateFailed();
            }
        }

        ApGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        for (ApGoodsSpecification specification : specifications) {
            String spec = specification.getSpecification();
            if (StrUtil.isBlank(spec)) {
                return CommonResult.validateFailed();
            }
            String value = specification.getValue();
            if (StrUtil.isBlank(value)) {
                return CommonResult.validateFailed();
            }
        }

        ApGoodsProduct[] products = goodsAllinone.getProducts();
        for (ApGoodsProduct product : products) {
            Integer number = product.getNumber();
            if (number == null || number < 0) {
                return CommonResult.validateFailed();
            }

            BigDecimal price = product.getPrice();
            if (price == null) {
                return CommonResult.validateFailed();
            }

            String[] productSpecifications = product.getSpecifications();
            if (productSpecifications.length == 0) {
                return CommonResult.validateFailed();
            }
        }

        return null;
    }


}
