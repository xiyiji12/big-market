package com.wenying.domain.strategy.service;

import com.wenying.domain.strategy.model.entity.RaffleAwardEntity;
import com.wenying.domain.strategy.model.entity.RaffleFactorEntity;
import com.wenying.domain.strategy.model.entity.StrategyAwardEntity;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.armory.IStrategyDispatch;
import com.wenying.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.wenying.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.wenying.types.enums.ResponseCode;
import com.wenying.types.exception.AppException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

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
    protected DefaultChainFactory defaultChainFactory;//工厂:设置成protected是为了子类能使用，private子类不能使用
    protected DefaultTreeFactory defaultTreeFactory;//工厂

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
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

        //2.责任链抽奖计算【这步拿到的是初步的抽奖ID，之后需要根据ID处理抽奖】注意；现根据用户id和策略id查到初步奖品id
        DefaultChainFactory.StrategyAwardVO chainStrategyAwardVO = raffleLogicChain(userId,strategyId);//走子类实现的方法
        log.info("抽奖策略计算-责任链 {} {} {} {}",userId,strategyId,chainStrategyAwardVO.getAwardId(),chainStrategyAwardVO.getLogicModel());
        if(!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainStrategyAwardVO.getLogicModel())){//黑名单、权重等非默认抽奖的直接返回抽奖结果
            //TODO awardConfig 暂时为空。黑名单指定积分奖品，后续要在库表中配置上对应的1积分值，并获取到
            return buildRaffleAwardEntity(strategyId,chainStrategyAwardVO.getAwardId(),null);
        }

        //3.如果默认抽奖(有兜底奖品的话)就走规则树,规则树抽奖过滤,【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】再根据用户id和策略id和奖品id判断最终的结果
        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(userId, strategyId, chainStrategyAwardVO.getAwardId(), raffleFactorEntity.getEndDateTime());
        log.info("抽奖策略计算-规则树 {} {} {} {}", userId, strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());

        //4.规则树抽完返回奖品奖励
        return buildRaffleAwardEntity(strategyId,treeStrategyAwardVO.getAwardId(),treeStrategyAwardVO.getAwardRuleValue());
    }

    //返回抽奖结果的方法
    private RaffleAwardEntity buildRaffleAwardEntity(Long strategyId, Integer awardId, String awardConfig) {
        StrategyAwardEntity strategyAward = repository.queryStrategyAwardEntity(strategyId,awardId);
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .awardTitle(strategyAward.getAwardTitle())
                .awardConfig(awardConfig)
                .sort(strategyAward.getSort())
                .build();
    }

    /**
     * 抽奖计算，责任链抽象方法
     * @param userId     用户id
     * @param strategyId 策略id
     * @return  奖品id
     */
    public abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId,Long strategyId);

    /**
     * 抽奖结果过滤，决策树抽象方法
     * @param userId     用户id
     * @param strategyId 策略id
     * @param awardId  奖品id
     * @return  过滤结果【奖品id，会根据抽奖次数判断、库存判断、兜底返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);

    /**
     * 抽奖结果过滤，决策树抽象方法
     * @param userId     用户id
     * @param strategyId 策略id
     * @param awardId  奖品id
     * @param endDateTime 活动结束时间
     * @return  过滤结果【奖品id，会根据抽奖次数判断、库存判断、兜底返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId, Date endDateTime);

}
