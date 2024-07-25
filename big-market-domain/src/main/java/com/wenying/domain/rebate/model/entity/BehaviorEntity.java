package com.wenying.domain.rebate.model.entity;

import com.wenying.domain.rebate.model.valobj.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorEntity {

    private String userId;

    private BehaviorTypeVO behaviorTypeVO;

    private String outBusinessNo;//保证幂等，每次结果保持统一
}
