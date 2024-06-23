package com.wenying.trigger.job;

import com.wenying.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.wenying.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 更新奖品库存任务，为了不让更新库存的压力打到数据库中，这里采用redis更新缓存库存，
 * 异步队列更新数据库，数据库表最终一致即可
 */
@Slf4j
@Component()
public class UpdateAwardStockJob {

    @Resource
    private IRaffleStock raffleStock;

    @Scheduled(cron = "0/5 * * * * ?")//每5秒执行一次
    public void exec() {
        try {
            log.info("定时任务，更新奖品消耗库存【延迟队列获取，降低对数据库的更新频次，不要产生竞争】");
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = raffleStock.takeQueueValue();//拿到队列里的值
            if (null == strategyAwardStockKeyVO) return;//如果队列没值了直接退出方法5秒后进行下次判断
            //如果值不为空就更新数据库的操作
            log.info("定时任务，更新奖品消耗库存 strategyId:{} awardId:{}", strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
            raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(), strategyAwardStockKeyVO.getAwardId());
        } catch (Exception e) {
            log.error("定时任务，更新奖品消耗库存失败", e);
        }
    }



}
