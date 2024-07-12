package com.wenying.domain.award.repository;

import com.wenying.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * 奖品仓储接口
 */
public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}
