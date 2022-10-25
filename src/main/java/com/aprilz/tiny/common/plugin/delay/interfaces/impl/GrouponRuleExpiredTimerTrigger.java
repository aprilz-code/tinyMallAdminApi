package com.aprilz.tiny.common.plugin.delay.interfaces.impl;

import cn.hutool.json.JSONUtil;
import com.aprilz.tiny.common.plugin.delay.core.quene.GrouponRuleExpiredDelayQueue;
import com.aprilz.tiny.common.plugin.delay.interfaces.TimeTrigger;
import com.aprilz.tiny.common.plugin.delay.model.TimeTriggerMsg;
import com.aprilz.tiny.common.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * redis order 延时任务
 *
 * @author Chopper
 * @version v1.0
 * 2021-06-09 11:00
 */
@Component
@Slf4j
public class GrouponRuleExpiredTimerTrigger implements TimeTrigger {

    @Autowired
    private GrouponRuleExpiredDelayQueue grouponRuleExpiredDelayQueue;

    @Override
    public void add(TimeTriggerMsg timeTriggerMsg) {
        //计算延迟时间 执行时间-当前时间
        Integer delaySecond = Math.toIntExact(timeTriggerMsg.getTriggerTime() - DateUtil.getDateline());
        //设置延时任务
        if (Boolean.TRUE.equals(grouponRuleExpiredDelayQueue.addJob(JSONUtil.toJsonStr(timeTriggerMsg), delaySecond))) {
            log.info("定时执行在【" + DateUtil.toString(timeTriggerMsg.getTriggerTime(), "yyyy-MM-dd HH:mm:ss") + "】，消费【" + timeTriggerMsg.getParam().toString() + "】");
        } else {
            log.error("团购规则过期任务自动取消--延时任务添加失败:{}", timeTriggerMsg);
        }
    }
}
