package com.wenying.domain.strategy.service.armory;

import com.wenying.domain.strategy.model.entity.StrategyAwardEntity;
import com.wenying.domain.strategy.model.entity.StrategyEntity;
import com.wenying.domain.strategy.model.entity.StrategyRuleEntity;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.types.common.Constants;
import com.wenying.types.enums.ResponseCode;
import com.wenying.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * 策略装配库（兵工厂），负责初始化策略计算
 */
@Slf4j
@Service

public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {

    @Resource
    private IStrategyRepository repository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public boolean assembleLotteryStrategyByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);//根据活动id查策略id，再根据策略id查策略配置
        return assembleLotteryStrategy(strategyId);
    }

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 1. 查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);

        // 2.缓存奖品库存【用于decr扣减库存使用】
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            Integer awardCount = strategyAward.getAwardCount();
            cacheStrategyAwardCount(strategyId,awardId,awardCount);
        }

        // 3.1 默认策略装配【全量抽奖概率】
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntities);

        // 3.2 权重策略配置 - 适用于 rule_weight 权重规则配置
        //从仓库中查询指定策略ID的策略实体（strategyEntity），获取其权重规则（ruleWeight）。
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        //如果 ruleWeight 为空，直接返回 true，表示无需进一步处理
        if (null == ruleWeight) return true;

        //否则，从仓库中查询策略规则实体（strategyRuleEntity）
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId, ruleWeight);
        //如果查询结果为空，抛出自定义异常 AppException，提示权重规则为空。
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        //获取策略规则中的权重值映射（ruleWeightValueMap），遍历映射的键集合（keys）。
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValueMap.keySet();
        for (String key : keys) {
            //对于每一个键，获取对应的权重值列表（ruleWeightValues）
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            //克隆一份原始的奖品实体列表（strategyAwardEntitiesClone），移除那些不在权重值列表中的奖品实体。
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(entity -> !ruleWeightValues.contains(entity.getAwardId()));
            //调用 assembleLotteryStrategy 方法，传入带有键后缀的新策略ID（strategyId_key）和过滤后的奖品实体列表。
            assembleLotteryStrategy(String.valueOf(strategyId).concat(Constants.UNDERLINE).concat(key), strategyAwardEntitiesClone);
        }

        return true;
    }

    /**
     * 库存的处理
     * @param strategyId
     * @param awardId
     * @param awardCount
     */
    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
       String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
      repository.cacheStrategyAwardCount(cacheKey,awardCount);
    }

    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        // 1. 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 2. 获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 用总和/最小概率(如：1 % 0.0001) 获得概率范围，百分位、千分位、万分位
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        // 4. 生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            // 计算出每个概率值需要存放到查找表的数量，循环填充
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        // 5. 对存储的奖品进行乱序操作
        Collections.shuffle(strategyAwardSearchRateTables);

        // 6. 生成出Map集合，key值，对应的就是后续的概率值。通过概率来获得对应的奖品ID
        Map<Integer, Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 7. 存放到 Redis
        repository.storeStrategyAwardSearchRateTable(key, shuffleStrategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);
    }

    /**
     * 随机策略抽奖
     * @param strategyId 策略ID
     * @return
     */
    @Override
    public Integer getRandomAwardId(Long strategyId) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rateRange = repository.getRateRange(strategyId);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(String.valueOf(strategyId), new SecureRandom().nextInt(rateRange));
    }

    /**
     * 权重策略抽奖
     * @param strategyId
     * @param ruleWeightValue
     * @return
     */
    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat(Constants.UNDERLINE).concat(ruleWeightValue);
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rateRange = repository.getRateRange(key);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return repository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rateRange));
    }

    /**
     * 库存扣减
     * @param strategyId 策略id
     * @param awardId    奖品id
     * @return
     */
    @Override
    public Boolean subtractionAwardStock(Long strategyId, Integer awardId,Date endDateTime) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        return repository.subtractionAwardStock(cacheKey);
    }

}
