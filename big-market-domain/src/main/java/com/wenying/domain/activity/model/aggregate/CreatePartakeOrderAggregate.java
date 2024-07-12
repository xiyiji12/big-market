package com.wenying.domain.activity.model.aggregate;

import com.wenying.domain.activity.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参与活动订单聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePartakeOrderAggregate {
    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /**
     * 账户总额度
     */
    private ActivityAccountEntity activityAccountEntity;

    private boolean isExistAccountMonth = true;
    /**
     * 账户月额度
     */
    private ActivityAccountMonthEntity activityAccountMonthEntity;

    private boolean isExistAccountDay = true;
    /**
     * 账户日额度
     */
    private ActivityAccountDayEntity activityAccountDayEntity;
    /**
     * 订单实体对象
     */
    private UserRaffleOrderEntity userRaffleOrderEntity;
}
