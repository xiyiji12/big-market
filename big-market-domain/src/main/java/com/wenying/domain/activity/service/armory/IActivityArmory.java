package com.wenying.domain.activity.service.armory;

/**
 * 活动装配预热
 */
public interface IActivityArmory {

    boolean assembleActivitySkuByActivityId(Long activityId);

    boolean assembleActivitySku(Long sku);

}
