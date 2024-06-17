package com.wenying.domain.strategy.service.rule;

import com.wenying.domain.strategy.model.entity.RuleActionEntity;
import com.wenying.domain.strategy.model.entity.RuleMatterEntity;

/**
 * 抽奖规则过滤接口：通用抽奖规则的流程
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {
    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}
