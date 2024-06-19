package com.wenying.domain.strategy.service.rule.chain.impl;

import com.wenying.domain.strategy.service.armory.IStrategyDispatch;
import com.wenying.domain.strategy.service.rule.chain.AbstractLogicChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 兜底责任链节点（不是黑名单或者权重就走兜底）
 */
@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {

    @Resource
    protected IStrategyDispatch strategyDispatch;//默认的抽奖方法
    @Override
    public Integer logic(String userId, Long strategyId) {//抽奖操作
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId: {} strategyId: {} ruleModel:{} awardId: {}",userId,strategyId,ruleModel(),awardId);
        return awardId;//返回奖品id
    }

    @Override
    protected String ruleModel() {
        return "default";
    }
}
