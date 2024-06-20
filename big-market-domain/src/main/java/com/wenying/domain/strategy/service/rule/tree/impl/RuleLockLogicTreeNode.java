package com.wenying.domain.strategy.service.rule.tree.impl;

import com.wenying.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.wenying.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.wenying.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 次数锁节点
 */
@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        //次数锁第一个节点放行
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}
