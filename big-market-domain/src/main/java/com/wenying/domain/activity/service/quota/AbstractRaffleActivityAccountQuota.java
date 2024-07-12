package com.wenying.domain.activity.service.quota;

import com.alibaba.fastjson.JSON;
import com.wenying.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.wenying.domain.activity.model.entity.*;
import com.wenying.domain.activity.repository.IActivityRepository;
import com.wenying.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.wenying.domain.activity.service.quota.rule.IActionChain;
import com.wenying.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import com.wenying.types.enums.ResponseCode;
import com.wenying.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @description 抽奖活动抽象类，定义下单标准的流程，由子类实现抽象方法
 */
@Slf4j
public abstract class AbstractRaffleActivityAccountQuota extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {


    public AbstractRaffleActivityAccountQuota(DefaultActivityChainFactory defaultActivityChainFactory, IActivityRepository activityRepository) {
        super(defaultActivityChainFactory, activityRepository);
    }

    @Override
    public ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity) {
        // 1. 通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(activityShopCartEntity.getSku());
        // 2. 查询活动信息
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 3. 查询次数信息（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        log.info("查询结果：{} {} {}", JSON.toJSONString(activitySkuEntity), JSON.toJSONString(activityEntity), JSON.toJSONString(activityCountEntity));

        return ActivityOrderEntity.builder().build();
    }

    @Override
    public String createOrder(SkuRechargeEntity skuRechargeEntity) {
        // 1.参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if (null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2.查询基础信息
        // 2.1. 通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        // 2.2. 查询活动信息
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 2.3. 查询次数信息（用户在活动上可参与的次数）
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        // 3.活动动作规则校验（责任链）todo 后续处理规则过滤流程，暂时也不处理责任链结果
        IActionChain actionChain = defaultActivityChainFactory.openActionChain();
        actionChain.action(activitySkuEntity,activityEntity,activityCountEntity);

        // 4.构建订单聚合对象
       CreateQuotaOrderAggregate createOrderAggregate = buildOrderAggregate(skuRechargeEntity,activitySkuEntity,activityEntity,activityCountEntity);
        // 5.保存订单
        doSaveOrder(createOrderAggregate);

        // 6. 返回单号
        return createOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    //构建订单聚合对象抽象方法
    protected abstract void doSaveOrder(CreateQuotaOrderAggregate createOrderAggregate);


    //保存订单抽象方法
    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);
}
