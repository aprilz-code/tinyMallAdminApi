package com.aprilz.tiny.common.plugin.delay.interfaces.execute;

import com.alibaba.fastjson.util.TypeUtils;
import com.aprilz.tiny.common.exception.ServiceException;
import com.aprilz.tiny.common.plugin.delay.interfaces.TimeTriggerExecutor;
import com.aprilz.tiny.mall.GrouponConstant;
import com.aprilz.tiny.mall.utils.OrderUtil;
import com.aprilz.tiny.mbg.entity.ApGroupon;
import com.aprilz.tiny.mbg.entity.ApGrouponRules;
import com.aprilz.tiny.mbg.entity.ApOrder;
import com.aprilz.tiny.mbg.entity.ApOrderGoods;
import com.aprilz.tiny.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 团购规则过期任务执行器
 *
 * @author Chopper
 * @version v1.0
 * 2021-06-09 10:49
 */
@Component
@Slf4j
public class GrouponRuleExpiredTimeTriggerExecutor implements TimeTriggerExecutor {

    @Autowired
    private IApOrderService orderService;
    @Autowired
    private IApGrouponService grouponService;

    @Autowired
    private IApGrouponRulesService grouponRulesService;


    @Override
    public void execute(Object object) {
        Long grouponRuleId = TypeUtils.castToLong(object);
        log.info("系统开始处理延时任务---团购规则过期---" + grouponRuleId);


        ApGrouponRules grouponRules = grouponRulesService.getById(grouponRuleId);
        if(grouponRules == null){
            return;
        }
        if(!grouponRules.getStatus().equals(GrouponConstant.RULE_STATUS_ON)){
            return;
        }

        // 团购活动取消
        grouponRules.setStatus(GrouponConstant.RULE_STATUS_DOWN_EXPIRE);
        grouponRulesService.updateById(grouponRules);

        List<ApGroupon> grouponList = grouponService.lambdaQuery().eq(ApGroupon::getRulesId, grouponRuleId)
                .eq(ApGroupon::getDeleteFlag, false).list();
        // 用户团购处理
        for(ApGroupon groupon : grouponList){
            Integer status = groupon.getStatus();
            ApOrder order = orderService.getById(groupon.getOrderId());
            if(status.equals(GrouponConstant.STATUS_NONE)){
                groupon.setStatus(GrouponConstant.STATUS_FAIL);
                grouponService.updateById(groupon);
            }
            else if(status.equals(GrouponConstant.STATUS_ON)){
                // 如果团购进行中
                // (1) 团购设置团购失败等待退款状态
                groupon.setStatus(GrouponConstant.STATUS_FAIL);
                grouponService.updateById(groupon);
                // (2) 团购订单申请退款
                if(OrderUtil.isPayStatus(order)) {
                    order.setOrderStatus(OrderUtil.STATUS_REFUND);
                    orderService.updateWithOptimisticLocker(order);
                }
            }
        }
        log.info("系统结束处理延时任务---团购规则过期---" + grouponRuleId);
    }
}
