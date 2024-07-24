package com.wenying.trigger.api;

import com.wenying.trigger.api.dto.RaffleAwardListRequestDTO;
import com.wenying.trigger.api.dto.RaffleAwardListResponseDTO;
import com.wenying.trigger.api.dto.RaffleStrategyRequestDTO;
import com.wenying.trigger.api.dto.RaffleStrategyResponseDTO;
import com.wenying.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口：在触发器层实现接口
 */
public interface IRaffleStrategyService {

    /**
     * 策略装配接口
     * @param strategyId 策略ID
     * @return 装配结果
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询整个奖品列表的接口：大转盘上
     * @param requestDTO 抽奖列表查询请求参数
     * @return 奖品列表数据
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);

    /**
     * 随机抽奖接口
     * @param requestDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO requestDTO);
}
