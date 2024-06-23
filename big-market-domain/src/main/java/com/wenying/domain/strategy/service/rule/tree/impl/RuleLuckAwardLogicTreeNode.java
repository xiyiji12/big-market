package com.wenying.domain.strategy.service.rule.tree.impl;

import com.wenying.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.wenying.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.wenying.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.wenying.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 兜底奖励节点
 */
@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId,String ruleValue) {//rule_luck_award的rule_value: 101:1,100
        //随机奖励的时候拦截给兜底奖品
        log.info("规则过滤-兜底奖品 userId:{} strategyId:{} awardId:{} ruleValue:{}", userId, strategyId, awardId, ruleValue);
        String[] split = ruleValue.split(Constants.COLON);//:拆分成split[0]=101,split[1]=1,100
        if (split.length == 0) {//rule_value=null
            log.error("规则过滤-兜底奖品，兜底奖品未配置告警 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
            throw new RuntimeException("兜底奖品未配置 " + ruleValue);
        }
        // 兜底奖励配置
        Integer luckAwardId = Integer.valueOf(split[0]);//奖品id：split[0]=101
        String awardRuleValue = split.length > 1 ? split[1] : "";//奖品是：split[1]=1,100
        // 返回兜底奖品
        log.info("规则过滤-兜底奖品 userId:{} strategyId:{} awardId:{} awardRuleValue:{}", userId, strategyId, luckAwardId, awardRuleValue);
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(luckAwardId)
                        .awardRuleValue(awardRuleValue)
                        .build())
                .build();
    }

}
