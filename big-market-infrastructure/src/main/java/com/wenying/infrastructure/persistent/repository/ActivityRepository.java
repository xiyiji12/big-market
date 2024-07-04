package com.wenying.infrastructure.persistent.repository;

import com.wenying.domain.activity.model.entity.ActivityCountEntity;
import com.wenying.domain.activity.model.entity.ActivityEntity;
import com.wenying.domain.activity.model.entity.ActivitySkuEntity;
import com.wenying.domain.activity.model.valobj.ActivityStateVO;
import com.wenying.domain.activity.repository.IActivityRepository;
import com.wenying.infrastructure.persistent.dao.IRaffleActivityCountDao;
import com.wenying.infrastructure.persistent.dao.IRaffleActivityDao;
import com.wenying.infrastructure.persistent.dao.IRaffleActivitySkuDao;
import com.wenying.infrastructure.persistent.po.RaffleActivity;
import com.wenying.infrastructure.persistent.po.RaffleActivityCount;
import com.wenying.infrastructure.persistent.po.RaffleActivitySku;
import com.wenying.infrastructure.persistent.redis.IRedisService;
import com.wenying.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 活动仓储服务
 */
@Slf4j
@Repository
public class ActivityRepository implements IActivityRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private IRaffleActivitySkuDao raffleActivitySkuDao;
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    @Resource
    private IRaffleActivityCountDao raffleActivityCountDao;

    /**
     * 查sku
     *
     * @param sku
     * @return
     */
    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    /**
     * 查活动
     *
     * @param activityId
     * @return
     */
    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {

        //优先从缓存取
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if (null != activityEntity) return activityEntity;
        //从库中获取数据
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();
        //写入缓存
        redisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }

    /**
     * 查次数
     *
     * @param activityCountId
     * @return
     */
    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        //优先从缓存取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if (null != activityCountEntity) return activityCountEntity;
        //从库中获取数据
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCountByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        //写入缓存
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }
}
