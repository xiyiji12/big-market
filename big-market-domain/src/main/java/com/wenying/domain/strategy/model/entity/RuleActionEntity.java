package com.wenying.domain.strategy.model.entity;

import com.wenying.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * 过滤完规则后应该返回的实体（抽奖前中后返回的结果有差别）
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {//结果返回的东西继承类
    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();
    private String ruleModel;
    private T data;

    static public class RaffleEntity {//定义返回结果实体类：相当于一个规范

    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    //抽奖前
    static public class RaffleBeforeEntity extends RaffleEntity {
        /**
         * 抽奖策略ID
         */
        private Long strategyId;
        /**
         * 权重值key:用于抽奖时可以选择权重抽奖
         */
        private String ruleWeightValueKey;
        /**
         * 抽奖奖品ID - 内部流转使用: 如果是黑名单直继返回奖品不用抽
         */
        private Integer awardId;
    }

    //抽奖中
    static public class RaffleCenterEntity extends RaffleEntity {

    }

    //抽奖后
    static public class RaffleAfterEntity extends RaffleEntity {

    }
}
