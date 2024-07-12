package com.wenying.domain.activity.repository;

import com.wenying.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.wenying.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.wenying.domain.activity.model.entity.*;
import com.wenying.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;

/**
 * 活动仓储接口,定义规范，在持久层实现
 */
public interface IActivityRepository {

    ActivitySkuEntity queryActivitySku(Long sku);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);

    void doSaveOrder(CreateQuotaOrderAggregate createOrderAggregate);

    void cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);

    UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);

    ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId);

    ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId,String month);

    ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId,String day);
}
