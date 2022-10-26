package com.aprilz.tiny.vo;

import com.aprilz.tiny.mbg.entity.ApCategory;
import lombok.Data;

import java.util.List;

/**
 * @description: 分类vo
 * @author: Aprilz
 * @since: 2022/10/18
 **/
@Data
public class CategoryVo  extends  ApCategory{

    private List<ApCategory> children;
}
