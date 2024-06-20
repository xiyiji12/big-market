package com.wenying.domain.strategy.service;

import com.wenying.domain.strategy.model.entity.RaffleAwardEntity;
import com.wenying.domain.strategy.model.entity.RaffleFactorEntity;
import com.wenying.domain.strategy.model.entity.RuleActionEntity;
import com.wenying.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.wenying.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.armory.IStrategyDispatch;
import com.wenying.domain.strategy.service.rule.chain.ILogicChain;
import com.wenying.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.wenying.types.enums.ResponseCode;
import com.wenying.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 抽奖策略抽象类:实现IRaffleStrategy接口对方法进行定制
 * 模板方法
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    // 策略仓储服务 -> domain层像一个大厨，仓储层提供米面粮油
    protected IStrategyRepository repository;
    // 策略调度服务 -> 只负责抽奖处理，通过新增接口的方式，隔离职责，不需要使用方关心或者调用抽奖的初始化
    protected IStrategyDispatch strategyDispatch;
    private DefaultChainFactory defaultChainFactory;//工厂

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
    }


    //performRaffle实现了执行抽奖的方法模板
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1. 参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        //2.责任链处理抽奖
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);//开启责任链,返回第一个责任链节点
        Integer awardId = logicChain.logic(userId, strategyId);//完成责任链抽奖


        //3.查询奖品规则：抽奖中（拿到奖品ID时，过滤规则）、抽奖后（扣减完奖品库存后过滤，抽奖中拦截和无库存则走兜底）
       StrategyAwardRuleModelVO strategyAwardRuleModelVo = repository.queryStrategyAwardRuleModel(strategyId, awardId);

       //4.抽奖中 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionCenterEntity = this.doCheckRaffleCenterLogic(RaffleFactorEntity.builder()
                .userId(userId)
                .strategyId(strategyId)
                .awardId(awardId)
                .build(),strategyAwardRuleModelVo.raffleCenterRuleModelList());

        //被拦截走兜底奖品奖励
        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionCenterEntity.getCode())){
            log.info("【临时日志】中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。")
                    .build();
        }

        //返回奖品奖励
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }
   //抽象类里定义了一个抽奖前的抽象方法 子类实现这个抽象方法
    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);

    //抽象类里定义了一个抽奖中的抽象方法 子类实现这个抽象方法
    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics);

}
