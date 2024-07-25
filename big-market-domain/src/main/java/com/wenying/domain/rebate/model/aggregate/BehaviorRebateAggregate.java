package com.wenying.domain.rebate.model.aggregate;

import com.wenying.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.wenying.domain.rebate.model.entity.TaskEntity;
import javafx.scene.control.Tab;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为返利聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateAggregate {
    private String userId;

    private BehaviorRebateOrderEntity behaviorRebateOrderEntity;

    private TaskEntity taskEntity;

}
