package com.wenying.domain.activity.service.quota.rule;

import com.wenying.domain.activity.model.entity.ActivityCountEntity;
import com.wenying.domain.activity.model.entity.ActivityEntity;
import com.wenying.domain.activity.model.entity.ActivitySkuEntity;

/**
 * 下单规则过滤接口
 */
public interface IActionChain extends IActionChainArmory {
    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);
}
