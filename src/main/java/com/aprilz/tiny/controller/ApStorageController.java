package com.aprilz.tiny.controller;

import cn.hutool.core.util.StrUtil;
import com.aprilz.tiny.common.api.CommonResult;
import com.aprilz.tiny.common.storage.StorageService;
import com.aprilz.tiny.mbg.entity.ApStorage;
import com.aprilz.tiny.service.IApStorageService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@RestController
@RequestMapping("/storage")
@Validated
@Slf4j
@Api(tags = "存储管理")
public class ApStorageController {


    @Autowired
    private StorageService storageService;
    @Autowired
    private IApStorageService iApStorageService;

    @PreAuthorize("hasAuthority('admin:storage:list')")
    @ApiOperation("系统管理-对象存储-查询")
    @GetMapping("/list")
    public CommonResult list(String key, String name,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "create_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        Page<ApStorage> storageList = iApStorageService.querySelective(key, name, page, limit, sort, order);
        return CommonResult.success(storageList);
    }

    @PreAuthorize("hasAuthority('admin:storage:create')")
    @ApiOperation("系统管理-对象存储-上传")
    @PostMapping("/create")
    public CommonResult create(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        ApStorage ApStorage = storageService.store(file.getInputStream(), file.getSize(),
                file.getContentType(), originalFilename);
        return CommonResult.success(ApStorage);
    }

    @PreAuthorize("hasAuthority('admin:storage:read')")
    @ApiOperation("系统管理-对象存储-详情")
    @PostMapping("/read")
    public CommonResult read(@NotNull Integer id) {
        ApStorage storageInfo = iApStorageService.getById(id);
        if (storageInfo == null) {
            return CommonResult.error();
        }
        return CommonResult.success(storageInfo);
    }

    @PreAuthorize("hasAuthority('admin:storage:update')")
    @ApiOperation("系统管理-对象存储-编辑")
    @PostMapping("/update")
    public CommonResult update(@RequestBody ApStorage apStorage) {
        Long id = apStorage.getId();
        if (id == null) {
            return CommonResult.paramsError();
        }
        if (!iApStorageService.updateById(apStorage)) {
            return CommonResult.error("编辑异常");
        }
        return CommonResult.success(apStorage);
    }

    @PreAuthorize("hasAuthority('admin:storage:delete')")
    @ApiOperation("系统管理-对象存储-删除")
    @PostMapping("/delete")
    public CommonResult delete(@RequestBody ApStorage apStorage) {
        String key = apStorage.getKey();
        if (StrUtil.isBlank(key)) {
            return CommonResult.error();
        }
        iApStorageService.deleteByKey(key);
        storageService.delete(key);
        return CommonResult.success();
    }


    /**
     * 访问存储对象
     *
     * @param key 存储对象key
     * @return
     */
    @GetMapping("/fetch/{key:.+}")
    public ResponseEntity<Resource> fetch(@PathVariable String key) {
        ApStorage apStorage = iApStorageService.findByKey(key);
        if (key == null) {
            return ResponseEntity.notFound().build();
        }
        if (key.contains("../")) {
            return ResponseEntity.badRequest().build();
        }
        String type = apStorage.getType();
        MediaType mediaType = MediaType.parseMediaType(type);

        Resource file = storageService.loadAsResource(key);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(mediaType).body(file);
    }
}
