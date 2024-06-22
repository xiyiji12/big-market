package com.wenying.domain.strategy.service.rule.tree.factory;

import com.wenying.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.wenying.domain.strategy.model.valobj.RuleTreeVO;
import com.wenying.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.wenying.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import com.wenying.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import com.wenying.domain.strategy.service.rule.tree.impl.RuleLockLogicTreeNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 规则树工厂
 */
@Service
public class DefaultTreeFactory {
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeGroup) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
    }

    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO){
        return new DecisionTreeEngine(logicTreeNodeGroup,ruleTreeVO);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity{//决策完是放行了还是拦截了，返回结果

        private RuleLogicCheckTypeVO ruleLogicCheckTypeVO;
        private StrategyAwardVO strategyAwardVO;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO{//决策完最终返回的结果

        /** 奖品id */
        private Integer awardId;
        /** 抽奖奖品规则 */
        private String awardRuleValue;
    }
}
