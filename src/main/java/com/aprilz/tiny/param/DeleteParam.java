package com.aprilz.tiny.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 删除使用的param
 * @author: Aprilz
 * @since: 2022/10/17
 **/
@Data
public class DeleteParam {

    @NotNull
    private Long id;
}
