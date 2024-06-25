package com.wenying.infrastructure.persistent.dao;

import com.wenying.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 抽奖策略奖品明细配置 - 概率、规则
 */
@Mapper

public interface IStrategyAwardDao {
    List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);//奖品列表

    String queryStrategyAwardRuleModels(StrategyAward strategyAward);

    void updateStrategyAwardStock(StrategyAward strategyAward);

    StrategyAward queryStrategyAward(StrategyAward strategyAwardReq);//奖品信息
    // void updateStrategyAwardStock(Long strategyId,Integer awardId);
}
