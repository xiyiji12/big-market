package com.wenying.infrastructure.persistent.dao;


import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.wenying.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description 抽奖活动账户表
 */
@Mapper
public interface IRaffleActivityAccountDao {

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    void insert(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(RaffleActivityAccount raffleActivityAccount);
    int updateActivityAccountSubtractionQuota(RaffleActivityAccount raffleActivityAccount);
    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount raffleActivityAccount);
    void updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount raffleActivityAccount);
}

