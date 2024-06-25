package com.wenying.domain.strategy.service.raffle;

import com.wenying.domain.strategy.model.entity.StrategyAwardEntity;
import com.wenying.domain.strategy.model.valobj.RuleTreeVO;
import com.wenying.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.wenying.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.AbstractRaffleStrategy;
import com.wenying.domain.strategy.service.IRaffleAward;
import com.wenying.domain.strategy.service.IRaffleStock;
import com.wenying.domain.strategy.service.armory.IStrategyDispatch;
import com.wenying.domain.strategy.service.rule.chain.ILogicChain;
import com.wenying.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.wenying.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.wenying.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 继承抽象类AbstractRaffleStrategy的子类，实现默认的抽奖策略实现
 */
@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleAward, IRaffleStock {


    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        super(repository, strategyDispatch, defaultChainFactory, defaultTreeFactory);
    }//父类的东西提供回去

    /**
     * 实现责任链抽象方法的子类
     * @param userId     用户id
     * @param strategyId 策略id
     * @return
     */
    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);//开启责任链判断是否有链，有的话返回责任链里的第一个节点；没有就返回默认的
        return logicChain.logic(userId,strategyId);//走对应的默认、黑名单、权重责任链
    }

    /**
     * 实现规则树抽象方法的子类
     * @param userId     用户id
     * @param strategyId 策略id
     * @param awardId  奖品id
     * @return
     */
    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModelVO(strategyId, awardId);//根据策略id和奖品id查找rulModels
        if (null == strategyAwardRuleModelVO) {//如果ruleModels为空
            return DefaultTreeFactory.StrategyAwardVO.builder().awardId(awardId).build();
        }
        RuleTreeVO ruleTreeVO = repository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());//如果ruleModels不为空去查返回整个树
        if (null == ruleTreeVO) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + strategyAwardRuleModelVO.getRuleModels());
        }
        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);//规则树工厂
        return treeEngine.process(userId, strategyId, awardId);//规则树抽奖，走整个树的判断流程最终返回抽奖信息
    }

    /**
     *
     * 实现查队列里的值
     * @return
     * @throws InterruptedException
     */
    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    /**
     * 实现更新数据库的功能
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     */
    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {

        repository.updateStrategyAwardStock(strategyId,awardId);

    }

    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId) {
        return repository.queryStrategyAwardList(strategyId);
    }
}
