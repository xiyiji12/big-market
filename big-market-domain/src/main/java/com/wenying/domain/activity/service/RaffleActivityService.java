package com.wenying.domain.activity.service;

import com.wenying.domain.activity.model.aggregate.CreateOrderAggregate;
import com.wenying.domain.activity.model.entity.*;
import com.wenying.domain.activity.model.valobj.OrderStateVO;
import com.wenying.domain.activity.repository.IActivityRepository;
import com.wenying.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @description 抽奖活动服务 是抽象类定义的抽象接口由此类实现。
 */
@Service
public class RaffleActivityService extends AbstractRaffleActivity {


    public RaffleActivityService(DefaultActivityChainFactory defaultActivityChainFactory, IActivityRepository activityRepository) {
        super(defaultActivityChainFactory, activityRepository);
    }

    //实现保存订单抽象方法
    @Override
    protected void doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        activityRepository.doSaveOrder(createOrderAggregate);
    }

    //实现构建订单聚合对象抽象方法
    @Override
    protected CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {

        //订单实体对象
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(skuRechargeEntity.getUserId());
        activityOrderEntity.setSku(skuRechargeEntity.getSku());
        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setStrategyId(activityEntity.getStrategyId());
        //公司一般会有专门的雪花算法UUID服务，直接随机生成个12位
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        activityOrderEntity.setOrderTime(new Date());
        activityOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        activityOrderEntity.setDayCount(activityCountEntity.getDayCount());
        activityOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        activityOrderEntity.setState(OrderStateVO.completed);
        activityOrderEntity.setOutBusinessNo(skuRechargeEntity.getOutBusinessNo());


        //构建聚合对象
       return CreateOrderAggregate.builder()
                .userId(activityOrderEntity.getUserId())
                .activityId(activityOrderEntity.getActivityId())
                .totalCount(activityOrderEntity.getTotalCount())
                .dayCount(activityOrderEntity.getDayCount())
                .monthCount(activityOrderEntity.getMonthCount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }
}
