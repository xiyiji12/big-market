package com.wenying.domain.rebate.repository;

import com.wenying.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.wenying.domain.rebate.model.valobj.BehaviorTypeVO;
import com.wenying.domain.rebate.model.valobj.DailyBehaviorRebateVO;

import java.util.List;

/**
 * 行为返利服务仓储接口
 */
public interface IBehaviorRebateRepository {

    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);//查询
    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);//插入

}
