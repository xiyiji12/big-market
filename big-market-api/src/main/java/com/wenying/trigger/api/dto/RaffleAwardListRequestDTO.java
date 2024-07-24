package com.wenying.trigger.api.dto;

import lombok.Data;

/**
 * 抽奖奖品列表，请求对象，方便后期更改
 */
@Data
public class RaffleAwardListRequestDTO {

    @Deprecated
    private Long strategyId;

    // 用户ID
    private String userId;
    // 抽奖活动ID
    private Long activityId;

}
