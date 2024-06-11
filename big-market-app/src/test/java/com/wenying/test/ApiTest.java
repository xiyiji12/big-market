package com.wenying.test;

import com.wenying.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IRedisService redisService;

    @Test
    public void test() {
        // 获取名为 "strategy_id_100001" 的Redis映射（类似于Java中的Map）
        RMap<Object,Object> map = redisService.getMap("strategy_id_100001");
        // 向映射中插入键值对
        map.put(1,101);
        map.put(2,101);
        map.put(3,101);
        map.put(4,102);
        map.put(5,102);
        map.put(6,102);
        map.put(7,103);
        map.put(8,103);
        map.put(9,104);
        map.put(10,105);
        // 从映射中获取键为5的值，并记录日志
        //定义好一些键值对，将随机结果对应key来查对应的奖品信息，用空间换时间
        log.info("测试结果：{}", redisService.getFromMap("strategy_id_100001",5).toString());
    }

}
