package com.aprilz.tiny.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @description: 新增修改AD
 * @author: Aprilz
 * @since: 2022/10/14
 **/
@Data
public class CreateOrUpdateAdParam {

    private Long id;

    @ApiModelProperty(value = "广告标题", required = true)
    @NotEmpty(message = "广告标题不能为空")
    private String name;

    @ApiModelProperty(value = "活动内容", required = true)
    @NotEmpty(message = "活动内容不能为空")
    private String content;

    @ApiModelProperty("所广告的商品页面或者活动页面链接地址")
    private String link;

    @ApiModelProperty("广告宣传图片")
    private String url;

    @ApiModelProperty("广告位置：1则是首页")
    private Integer position;


    @ApiModelProperty("广告开始时间")
    private Date startTime;

    @ApiModelProperty("广告结束时间")
    private Date endTime;

    @ApiModelProperty("是否启动")
    private Boolean enabled;
}
