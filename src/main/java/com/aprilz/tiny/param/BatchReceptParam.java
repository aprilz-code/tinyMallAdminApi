package com.aprilz.tiny.param;

import lombok.Data;

import java.util.List;

/**
 * @description: 商城管理-售后管理-批量通过 param
 * @author: Aprilz
 * @since: 2022/10/18
 **/
@Data
public class BatchReceptParam {

    private List<Integer> ids;
}
