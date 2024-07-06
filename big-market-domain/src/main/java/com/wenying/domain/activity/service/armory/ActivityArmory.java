package com.wenying.domain.activity.service.armory;

import com.wenying.domain.activity.model.entity.ActivitySkuEntity;
import com.wenying.domain.activity.repository.IActivityRepository;
import com.wenying.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 实现装配活动接口
 */
@Slf4j
@Service
public class ActivityArmory implements IActivityArmory,IActivityDispatch {

    @Resource
    private IActivityRepository activityRepository;
    @Override
    public boolean assembleActivitySku(Long sku) {
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(sku);
        //缓存库存
        cacheActivitySkuStockCount(sku,activitySkuEntity.getStockCount());
        //预热活动【查询时预热到缓存】
        activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //预热活动次数【查询时预热到缓存】
        activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        return false;
    }

    //缓存库存的方法
    private void cacheActivitySkuStockCount(Long sku, Integer stockCount) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        activityRepository.cacheActivitySkuStockCount(cacheKey,stockCount);

    }
    //扣减库存的方法
    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        return activityRepository.subtractionActivitySkuStock(sku,cacheKey,endDateTime);
    }
}
