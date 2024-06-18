package com.wenying.domain.strategy.service.rule.impl;

import com.wenying.domain.strategy.model.entity.RuleActionEntity;
import com.wenying.domain.strategy.model.entity.RuleMatterEntity;
import com.wenying.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.annotation.LogicStrategy;
import com.wenying.domain.strategy.service.rule.ILogicFilter;
import com.wenying.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户抽奖n次后，对应奖品可以解锁抽奖
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RaffleCenterEntity> {

    @Resource
    private IStrategyRepository repository;

    private Long userRaffleCount = 0L;//先随便给个次数值，测试的时候再给具体的

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleCenterEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        // 查询规则值配置
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        Long raffleCount = Long.parseLong(ruleValue);//字符串转为整型

        //次数满足要求就解锁放行
        if (userRaffleCount >= raffleCount) {
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        //次数不满足就拦截不让
        return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }
}
