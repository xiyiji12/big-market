package com.wenying.domain.strategy.service.rule.impl;

import com.wenying.domain.strategy.model.entity.RuleActionEntity;
import com.wenying.domain.strategy.model.entity.RuleMatterEntity;
import com.wenying.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.annotation.LogicStrategy;
import com.wenying.domain.strategy.service.rule.ILogicFilter;
import com.wenying.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.wenying.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 黑名单用户过滤规则
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
public class RuleBlackListLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;

    public RuleBlackListLogicFilter(IStrategyRepository repository) {
        this.repository = repository;
    }

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-黑名单 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();

        // 查询规则值配置
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        //根据冒号拆分
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 100:user001,user002,user003过滤黑名单用户
        // 过滤其他规则
        //分割黑名单字符串
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        //遍历黑名单用户ID
        for (String userBlackId : userBlackIds) {
            //检查用户是否在黑名单中
            if (userId.equals(userBlackId)) {
                //如果用户在黑名单中，返回一个规则操作实体
                return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                        .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                        .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                        .data(RuleActionEntity.RaffleBeforeEntity.builder()
                                .strategyId(ruleMatterEntity.getStrategyId())
                                .awardId(awardId).build())
                        .build();

            }
        }


        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();

    }
}
