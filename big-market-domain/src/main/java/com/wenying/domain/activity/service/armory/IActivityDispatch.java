package com.wenying.domain.activity.service.armory;

import java.util.Date;

/**
 * 活动调度【扣减库存】
 */
public interface IActivityDispatch {
    /**
     * 根据策略id和奖品id，扣减奖品缓存库存
     * @param sku 活动sku
     * @param endDateTime 活动结束时间，根据结束时间设置枷锁的key为结束时间
     * @return 扣减结果
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);

}
