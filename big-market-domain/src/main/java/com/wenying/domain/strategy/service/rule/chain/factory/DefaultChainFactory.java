package com.wenying.domain.strategy.service.rule.chain.factory;

import com.wenying.domain.strategy.model.entity.StrategyEntity;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.rule.chain.ILogicChain;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainGroup;//map注入
    private final IStrategyRepository repository;

    public DefaultChainFactory(Map<String, ILogicChain> logicChainGroup, IStrategyRepository repository) {
        this.logicChainGroup = logicChainGroup;
        this.repository = repository;
    }

    /**
     * 开启责任链的方法
     *
     * @param strategyId
     * @return
     */
    public ILogicChain openLogicChain(Long strategyId) {
        //查配置
        StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategy.ruleModels();

        if (null == ruleModels || 0 == ruleModels.length) return logicChainGroup.get("default");//如果没有走默认

        //有就填充链
        ILogicChain logicChain = logicChainGroup.get(ruleModels[0]);//填充链，把第0个链拿出来
        ILogicChain current = logicChain;//记录第0个链
        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain nextChain = logicChainGroup.get(ruleModels[i]);
            current = current.appendNext(nextChain);
        }

        current.appendNext(logicChainGroup.get("default"));
        return logicChain;//返回责任链里的第一个节点：如：rule_weight

    }
}
