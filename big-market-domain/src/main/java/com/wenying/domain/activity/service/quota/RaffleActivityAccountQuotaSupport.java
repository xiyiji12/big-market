package com.wenying.domain.activity.service.quota;

import com.wenying.domain.activity.model.entity.ActivityCountEntity;
import com.wenying.domain.activity.model.entity.ActivityEntity;
import com.wenying.domain.activity.model.entity.ActivitySkuEntity;
import com.wenying.domain.activity.repository.IActivityRepository;
import com.wenying.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

/**
 * 抽象活动的支撑类，给抽象类提供一些基本信息（查询操作、数据封装等）让抽象类继承
 */
public class RaffleActivityAccountQuotaSupport {

    protected DefaultActivityChainFactory defaultActivityChainFactory;
    protected IActivityRepository activityRepository;

    public RaffleActivityAccountQuotaSupport(DefaultActivityChainFactory defaultActivityChainFactory, IActivityRepository activityRepository) {
        this.defaultActivityChainFactory = defaultActivityChainFactory;
        this.activityRepository = activityRepository;
    }

    // 1. 通过sku查询活动信息
    public ActivitySkuEntity queryActivitySku(Long sku){
        return activityRepository.queryActivitySku(sku);
    }
    // 2. 查询活动信息
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId){
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }
    // 3. 查询次数信息（用户在活动上可参与的次数）
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId){
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }

}
