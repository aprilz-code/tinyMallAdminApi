package com.aprilz.tiny.service.impl;

import com.aprilz.tiny.mapper.ApSystemMapper;
import com.aprilz.tiny.mbg.entity.ApSystem;
import com.aprilz.tiny.service.IApSystemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统配置表 服务实现类
 * </p>
 *
 * @author aprilz
 * @since 2022-07-19
 */
@Service
public class ApSystemServiceImpl extends ServiceImpl<ApSystemMapper, ApSystem> implements IApSystemService {

    @Override
    public Map<String, String> listForType(String type) {
        List<ApSystem> results = this.lambdaQuery().eq(ApSystem::getDeleteFlag, false).like(ApSystem::getKeyName, type)
                .list();
        Map<String, String> data = new HashMap<>();
        results.stream().forEach(result -> {
            data.put(result.getKeyName(), result.getKeyValue());
        });
        return data;
    }


    @Override
    public void updateConfig(Map<String, String> map) {
        for(Map.Entry<String, String> entry:map.entrySet()) {
            //map.entrySet()  返回此映射中包含的映射关系的Set视图
            //Map.Entry<Integer, String> 映射项 (键值对)
            this.lambdaUpdate().set(ApSystem::getKeyValue, entry.getValue()).eq(ApSystem::getKeyName,entry.getKey())
                    .eq(ApSystem::getDeleteFlag,false).update();
        }

    }


}
