package com.wenying.domain.strategy.repository;

import com.wenying.domain.strategy.model.entity.StrategyAwardEntity;
import com.wenying.domain.strategy.model.entity.StrategyEntity;
import com.wenying.domain.strategy.model.entity.StrategyRuleEntity;
import com.wenying.domain.strategy.model.valobj.RuleTreeVO;
import com.wenying.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.wenying.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 仓储策略
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String key, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);
    int getRateRange(String key);

    Integer getStrategyAwardAssemble(String key, Integer rateKey);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);
    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId);

    /**
     * 根据规则树id，查询树结构信息
     * @param treeId 规则树id
     * @return  树结构信息
     */
    RuleTreeVO queryRuleTreeVOByTreeId(String treeId);

    /**
     * 缓存奖品库存
     * @param cacheKey   key
     * @param awardCount 库存值
     */
    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    Boolean subtractionAwardStock(String cacheKey);
    Boolean subtractionAwardStock(String cacheKey, Date endDateTime);

    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    StrategyAwardStockKeyVO takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId);

    Long queryStrategyIdByActivityId(Long activityId);

    Integer queryTodayUserRaffleCount(String userId, Long strategyId);

    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);
}


