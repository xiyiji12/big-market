package com.wenying.domain.activity.service;

import com.wenying.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

/**
 * @description 抽奖活动服务
 */
@Service
public class RaffleActivityService extends AbstractRaffleActivity {

    public RaffleActivityService(IActivityRepository activityRepository) {
        super(activityRepository);
    }

}
