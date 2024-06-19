package com.wenying.domain.strategy.service.rule.chain;

/**
 * 责任链接口
 */
public interface ILogicChain extends ILogicChainArmory {
    /**
     * 责任链接口
     * @param userId     用户id
     * @param strategyId 策略id
     * @return 奖品id
     */
    Integer logic(String userId,Long strategyId);

}
