package com.wenying.infrastructure.persistent.dao;

import com.wenying.infrastructure.persistent.po.RaffleActivityAccount;
import com.wenying.infrastructure.persistent.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description 抽奖活动次数配置表Dao
 */
@Mapper
public interface IRaffleActivityCountDao {
    RaffleActivityCount queryRaffleActivityCountByActivityCountId(Long activityCount);

}
