package com.wenying.infrastructure.persistent.repository;

import com.wenying.domain.strategy.model.entity.StrategyAwardEntity;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.infrastructure.persistent.dao.IStrategyAwardDao;
import com.wenying.infrastructure.persistent.po.StrategyAward;
import com.wenying.infrastructure.persistent.redis.IRedisService;
import com.wenying.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 策略仓储实现
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
       String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;//前缀+id
        //先看看redis缓存有没有
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        if(null!=strategyAwardEntities && !strategyAwardEntities.isEmpty()){
            //有就返回
            return strategyAwardEntities;
        }
        //没有就从库中读取数据
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        //实例化一个对象
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for(StrategyAward strategyAward : strategyAwards){
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                        .strategyId(strategyAward.getStrategyId())
                        .awardId(strategyAward.getAwardId())
                        .awardCount(strategyAward.getAwardCount())
                        .awardCountSurplus(strategyAward.getAwardCountSurplus())
                        .awardRate(strategyAward.getAwardRate())
                        .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey,strategyAwardEntities);
        return strategyAwardEntities;
    }

    /**
     * 往redis里面存
     * @param strategyId
     * @param rateRange
     * @param shuffleStrategyAwardSearchRateTable
     */
    @Override
    public void storeStrategyAwardSearchRateTable(Long strategyId,Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        // 1. 存储抽奖策略范围值，如10000，用于生成1000以内的随机数(范围值)
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId,rateRange);
        // 2. 存储概率查找表(生成好的map)
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, Integer rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }

}
