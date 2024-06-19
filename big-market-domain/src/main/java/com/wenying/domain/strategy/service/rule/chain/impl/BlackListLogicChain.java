package com.wenying.domain.strategy.service.rule.chain.impl;

import com.wenying.domain.strategy.repository.IStrategyRepository;
import com.wenying.domain.strategy.service.armory.IStrategyDispatch;
import com.wenying.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.wenying.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 黑名单过滤规则具体实现
 */
@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicChain {
    @Resource
    private IStrategyRepository repository;//从库里查询
    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("抽奖责任链-黑名单开始 userId: {} strategyId: {} ruleModel:{} ",userId,strategyId,ruleModel());
        String ruleValue = repository.queryStrategyRuleValue(strategyId,ruleModel());//awardId可以为null
        //根据冒号拆分
        String[] splitRuleValue = ruleValue.split(Constants.COLON);//拆成["100","user001,user002,user003"]
        Integer awardId = Integer.parseInt(splitRuleValue[0]);//awardId是100，黑名单的奖品id

        // 100:user001,user002,user003过滤黑名单用户
        // 过滤其他规则
        //分割黑名单字符串
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);//拆成["user001","user002","user003"]
        //遍历黑名单用户ID
        for (String userBlackId : userBlackIds) {
            //检查用户是否在黑名单中
            if (userId.equals(userBlackId)) {
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel:{} awardId: {}",userId,strategyId,ruleModel(),awardId);
                return awardId;//100:user001,user002,user003,返回100
            }
        }
        //用户不在黑名单，过滤其他责任链
        log.info("抽奖责任链-黑名单放行 userId: {} strategyId: {} ruleModel:{} ",userId,strategyId,ruleModel());
        return next().logic(userId,strategyId);
    }

    @Override
    protected String ruleModel() {
        return "rule_blacklist";
    }
}
