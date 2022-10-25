package com.aprilz.tiny.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 商城管理-售后管理-批量通过 param
 * @author: Aprilz
 * @since: 2022/10/18
 **/
@Data
public class OrderShipParam {

    @NotNull
    private Integer orderId;

    @ApiModelProperty("发货编号")
    @NotBlank
    private String shipSn;

    @ApiModelProperty("发货快递公司")
    @NotBlank
    private String shipChannel;
}
