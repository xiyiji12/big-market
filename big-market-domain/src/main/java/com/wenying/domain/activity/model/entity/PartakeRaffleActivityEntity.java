package com.wenying.domain.activity.model.entity;

import lombok.Data;

/**
 * 参与抽奖活动的实体对象
 */
@Data
public class PartakeRaffleActivityEntity {


    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;

}
