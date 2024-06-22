package com.wenying.domain.strategy.service.rule.chain.impl;

import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.armory.IStrategyDispatch;
import com.wenying.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.wenying.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.wenying.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 权重责任链
 */
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyRepository repository;
    @Resource
    protected IStrategyDispatch strategyDispatch;//默认的抽奖方法
    private Long userScore = 0L;//模拟用户积分值

    /**
     * 权重责任链规则过滤；
     * 1. 权重规则格式；4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     * 2. 解析数据格式；判断哪个范围符合用户的特定抽奖范围
     */
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId:{} strategyId:{} ruleModel:{}", userId, strategyId, ruleModel());
        // 查询规则值配置
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());

        // 1. 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (null == analyticalValueGroup || analyticalValueGroup.isEmpty()) {//没有这个值就直接放行
            return null;
        }
        // 2. 转换Keys值，并默认排序，set是无序的不好比对
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
        Long nextValue = analyticalSortedKeys.stream()
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);

        //4.如果最小值存在，就接管返回奖品id
        if(null != nextValue){
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId,analyticalValueGroup.get(nextValue));
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel:{} awardId: {}",userId,strategyId,ruleModel(),awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }

        //5.否则过滤其他责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel:{} ",userId,strategyId,ruleModel());
        return next().logic(userId,strategyId);

    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode();
    }
    /**
     * 分割字符串为键和值，方便做比对处理
     * @param ruleValue
     * @return
     */
    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        //先根据空格分割成一组一组的
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            // 检查输入是否为空
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }
            // 根据冒号分割字符串以获取键和值4000和102,103,104,105
            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);//key:4000;ruleValueKey:4000:102,103,104,105
        }
        return ruleValueMap;
    }
}
