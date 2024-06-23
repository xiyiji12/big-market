package com.wenying.test.domin;

import com.alibaba.fastjson.JSON;
import com.wenying.domain.strategy.model.entity.RaffleAwardEntity;
import com.wenying.domain.strategy.model.entity.RaffleFactorEntity;
import com.wenying.domain.strategy.service.IRaffleStrategy;
import com.wenying.domain.strategy.service.armory.IStrategyArmory;
import com.wenying.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import com.wenying.domain.strategy.service.rule.tree.impl.RuleLockLogicTreeNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @description 抽奖策略测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleStrategyTest {
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IRaffleStrategy raffleStrategy;//抽奖

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;
    @Resource
    private RuleLockLogicTreeNode ruleLockLogicTreeNode;


    //@Before
    @Test
    public void setUp() {
        // 策略装配 100001、100002、100003
       // log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100001L));
       // log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100002L));
       // log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100003L));
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100006L));
        // 通过反射 mock 规则中的值
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 40500L);
        ReflectionTestUtils.setField(ruleLockLogicTreeNode, "userRaffleCount", 10L);

    }

    /**
     * 测试抽奖前规则
     */
    @Test
    public void test_performRaffle() throws InterruptedException {
        for (int i = 0; i < 3; i++) {//测3次
            RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                    .userId("xiaofuge")
                    .strategyId(100006L)
                    .build();

            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

            log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
        }

        // 等待 UpdateAwardStockJob 消费队列，让线程永远阻塞
        new CountDownLatch(1).await();
    }

    /**
     * 测试抽奖前黑名单过滤规则
     */
    @Test
    public void test_performRaffle_blacklist() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user003")  // 黑名单用户 user001,user002,user003
                .strategyId(100001L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }


    /**
     * 测试抽奖中规则
     * 次数错校验，抽奖n次后解锁。100003 策略，你可以通过调整 @Before 的 setUp 方法中个人抽奖次数来验证。比如最开始设置0，之后设置10
     * ReflectionTestUtils.setField(ruleLockLogicFilter, "userRaffleCount", 10L);
     */
    @Test
    public void test_raffle_center_rule_lock(){
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("xiaofuge")
                .strategyId(100003L)
                .build();

        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);

        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }


}

