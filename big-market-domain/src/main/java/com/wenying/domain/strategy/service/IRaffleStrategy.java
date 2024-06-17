package com.wenying.domain.strategy.service;

import com.wenying.domain.strategy.model.entity.RaffleAwardEntity;
import com.wenying.domain.strategy.model.entity.RaffleFactorEntity;

/**
 * 抽奖策略接口
 */
public interface IRaffleStrategy {

    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
