package com.aprilz.tiny.param;


import com.aprilz.tiny.mbg.entity.ApGoods;
import com.aprilz.tiny.mbg.entity.ApGoodsAttribute;
import com.aprilz.tiny.mbg.entity.ApGoodsProduct;
import com.aprilz.tiny.mbg.entity.ApGoodsSpecification;

public class GoodsAllinoneParam {
    //商品基本信息表
    ApGoods goods;
    //商品规格表
    ApGoodsSpecification[] specifications;
    //商品参数表
    ApGoodsAttribute[] attributes;
    //商品货品表
    ApGoodsProduct[] products;

    public ApGoods getGoods() {
        return goods;
    }

    public void setGoods(ApGoods goods) {
        this.goods = goods;
    }

    public ApGoodsProduct[] getProducts() {
        return products;
    }

    public void setProducts(ApGoodsProduct[] products) {
        this.products = products;
    }

    public ApGoodsSpecification[] getSpecifications() {
        return specifications;
    }

    public void setSpecifications(ApGoodsSpecification[] specifications) {
        this.specifications = specifications;
    }

    public ApGoodsAttribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(ApGoodsAttribute[] attributes) {
        this.attributes = attributes;
    }

}
