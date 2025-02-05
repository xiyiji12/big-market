package com.wenying.domain.activity.model.entity;

import lombok.Data;

/**
 * 活动商品充值实体对象
 */
@Data
public class SkuRechargeEntity {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 商品sku- activity + activity count
     */
    private Long sku;
    /**
     * 幂等业务单号，外部谁充值谁透传，保证幂等（多次调用也能确保结果唯一，不会多次充值）
     */
    private String outBusinessNo;
}
