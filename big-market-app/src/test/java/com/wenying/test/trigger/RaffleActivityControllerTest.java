package com.wenying.test.trigger;

import com.alibaba.fastjson.JSON;
import com.wenying.trigger.api.IRaffleActivityService;
import com.wenying.trigger.api.dto.ActivityDrawRequestDTO;
import com.wenying.trigger.api.dto.ActivityDrawResponseDTO;
import com.wenying.trigger.api.dto.UserActivityAccountRequestDTO;
import com.wenying.trigger.api.dto.UserActivityAccountResponseDTO;
import com.wenying.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @description 抽奖活动服务测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityControllerTest {

    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void test_armory() {
        Response<Boolean> response = raffleActivityService.armory(100301L);
        log.info("测试结果：{}", JSON.toJSONString(response));
    }


    @Test
    public void test_draw() {
        for (int i = 0; i < 20; i++) {
            ActivityDrawRequestDTO request = new ActivityDrawRequestDTO();
            request.setActivityId(100301L);
            request.setUserId("xiaofuge");
            Response<ActivityDrawResponseDTO> response = raffleActivityService.draw(request);

            log.info("请求参数：{}", JSON.toJSONString(request));
            log.info("测试结果：{}", JSON.toJSONString(response));
        }
    }

    @Test
    public void test_calendarSignRebate(){//签到
        Response<Boolean> response = raffleActivityService.calendarSignRebate("xiyi11");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }
    @Test
    public void test_isCalendarSignRebate() {//判断是否完成日历签到返利接口
        Response<Boolean> response = raffleActivityService.isCalendarSignRebate("xiyiji1");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserActivityAccount() {//查账户
        UserActivityAccountRequestDTO request = new UserActivityAccountRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("xiyi9");

        // 查询数据
        Response<UserActivityAccountResponseDTO> response = raffleActivityService.queryUserActivityAccount(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }



}

