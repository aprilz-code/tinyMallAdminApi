package com.aprilz.tiny.controller;


import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.mbg.entity.ApRegion;
import com.aprilz.tiny.service.IApRegionService;
import com.aprilz.tiny.vo.RegionVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/region")
@Validated
@Slf4j
@Api("地区管理")
public class ApRegionController {

    @Autowired
    private IApRegionService regionService;

    @GetMapping("/clist")
    public CommonResult clist(@NotNull Long id) {
        List<ApRegion> regionList = regionService.lambdaQuery().eq(ApRegion::getPid, id)
                .eq(ApRegion::getDeleteFlag, false).list();
        return CommonResult.success(regionList);
    }

    @GetMapping("/list")
    public CommonResult list() {
        List<RegionVo> regionVoList = new ArrayList<>();

        List<ApRegion> litemallRegions = regionService.list();
        Map<Integer, List<ApRegion>> collect = litemallRegions.stream().collect(Collectors.groupingBy(ApRegion::getType));
        byte provinceType = 1;
        List<ApRegion> provinceList = collect.get(provinceType);
        byte cityType = 2;
        List<ApRegion> city = collect.get(cityType);
        Map<Long, List<ApRegion>> cityListMap = city.stream().collect(Collectors.groupingBy(ApRegion::getPid));
        byte areaType = 3;
        List<ApRegion> areas = collect.get(areaType);
        Map<Long, List<ApRegion>> areaListMap = areas.stream().collect(Collectors.groupingBy(ApRegion::getPid));

        for (ApRegion province : provinceList) {
            RegionVo provinceVO = new RegionVo();
            provinceVO.setId(province.getId());
            provinceVO.setName(province.getName());
            provinceVO.setCode(province.getCode());
            provinceVO.setType(province.getType());

            List<ApRegion> cityList = cityListMap.get(province.getId());
            List<RegionVo> cityVOList = new ArrayList<>();
            for (ApRegion cityVo : cityList) {
                RegionVo cityVO = new RegionVo();
                cityVO.setId(cityVo.getId());
                cityVO.setName(cityVo.getName());
                cityVO.setCode(cityVo.getCode());
                cityVO.setType(cityVo.getType());

                List<ApRegion> areaList = areaListMap.get(cityVo.getId());
                List<RegionVo> areaVOList = new ArrayList<>();
                for (ApRegion area : areaList) {
                    RegionVo areaVO = new RegionVo();
                    areaVO.setId(area.getId());
                    areaVO.setName(area.getName());
                    areaVO.setCode(area.getCode());
                    areaVO.setType(area.getType());
                    areaVOList.add(areaVO);
                }

                cityVO.setChildren(areaVOList);
                cityVOList.add(cityVO);
            }
            provinceVO.setChildren(cityVOList);
            regionVoList.add(provinceVO);
        }

        return CommonResult.success(regionVoList);
    }
}
