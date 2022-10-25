package com.aprilz.tiny.vo;

import lombok.Data;

import java.util.List;

@Data
public class RegionVo {
    private Long id;
    private String name;
    private Integer type;
    private Integer code;

    private List<RegionVo> children;

}
