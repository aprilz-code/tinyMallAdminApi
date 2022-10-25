package com.aprilz.tiny.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @description: 订单回复
 * @author: Aprilz
 * @since: 2022/8/2
 **/
@Data
public class OrderReplyParam {
    @NotNull
    private Long commentId;

    @NotBlank
    private String  content;


}
