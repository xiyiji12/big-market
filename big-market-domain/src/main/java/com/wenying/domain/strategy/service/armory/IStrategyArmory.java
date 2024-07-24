package com.wenying.domain.strategy.service.armory;

/**
 * 策略装配库（兵工厂），负责初始化策略计算
 */
public interface IStrategyArmory {
    //根据活动id进行装配
    /**
     * 装配抽奖策略配置【触发的时机可以为活动审核通过后进行调用】
     * @param activityId 活动id
     * @return 装配结果
     */
    boolean assembleLotteryStrategyByActivityId(Long activityId);

    //根据策略id进行装配
    /**
     * 装配抽奖策略配置「触发的时机可以为活动审核通过后进行调用」
     *
     * @param strategyId 策略ID
     * @return 装配结果
     */
    boolean assembleLotteryStrategy(Long strategyId);

}
