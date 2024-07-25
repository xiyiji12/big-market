package com.wenying.domain.activity.service.partake;

import com.wenying.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.wenying.domain.activity.model.entity.*;
import com.wenying.domain.activity.model.valobj.UserRaffleOrderStateVO;
import com.wenying.domain.activity.repository.IActivityRepository;
import com.wenying.types.enums.ResponseCode;
import com.wenying.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 子类实现抽象类里的抽象方法
 */
@Service
public class  RaffleActivityPartakeService extends AbstractRaffleActivityPartake {

    private final SimpleDateFormat dateFormatMoth = new SimpleDateFormat("yyyy-MM");
    private final SimpleDateFormat dateFormatDay = new SimpleDateFormat("yyyy-MM-dd");

    public RaffleActivityPartakeService(IActivityRepository activityRepository) {
        super(activityRepository);
    }


    /**
     * 账户过滤方法
     * @param userId
     * @param activityId
     * @param currentDate
     * @return
     */
    @Override
    protected CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate) {
        //查询总账户额度
        ActivityAccountEntity activityAccountEntity = activityRepository.queryActivityAccountByUserId(userId,activityId);

        //额度判断（只判断总额度）
        if(null == activityAccountEntity ||activityAccountEntity.getTotalCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }

        String month = dateFormatMoth.format(currentDate);//日期格式转换
        String day = dateFormatDay.format(currentDate);//日期格式转换

        //查询月账户额度
        ActivityAccountMonthEntity activityAccountMonthEntity = activityRepository.queryActivityAccountMonthByUserId(userId,activityId,month);
        //月额度判断
        //不为空且额度小于0，抛异常显示月额度不足
        if(null != activityAccountMonthEntity && activityAccountMonthEntity.getMonthCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
        }
        //如果为空就创建月额度账户: true = 存在月账户、false = 不存在月账户
        boolean isExitAccountMonth = null != activityAccountMonthEntity;
        if(null == activityAccountMonthEntity){
             activityAccountMonthEntity = new ActivityAccountMonthEntity();
            activityAccountMonthEntity.setUserId(userId);
            activityAccountMonthEntity.setActivityId(activityId);
            activityAccountMonthEntity.setMonth(month);
            activityAccountMonthEntity.setMonthCount(activityAccountEntity.getMonthCount());
            activityAccountMonthEntity.setMonthCountSurplus(activityAccountEntity.getMonthCount());

        }

        //查询日账户额度
        ActivityAccountDayEntity activityAccountDayEntity = activityRepository.queryActivityAccountDayByUserId(userId,activityId,day);
        //日额度判断
        //不为空且额度小于0，抛异常显示日额度不足
        if(null != activityAccountDayEntity && activityAccountDayEntity.getDayCountSurplus()<=0){
            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(),ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
        }
        //如果为空就创建月额度账户: true = 存在月账户、false = 不存在月账户
        boolean isExitAccountDay = null != activityAccountDayEntity;
        if(null == activityAccountDayEntity){
            activityAccountDayEntity = new ActivityAccountDayEntity();
            activityAccountDayEntity.setUserId(userId);
            activityAccountDayEntity.setActivityId(activityId);
            activityAccountDayEntity.setDay(day);
            activityAccountDayEntity.setDayCount(activityAccountEntity.getDayCount());
            activityAccountDayEntity.setDayCountSurplus(activityAccountEntity.getDayCount());
        }

        //构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = new CreatePartakeOrderAggregate();
        createPartakeOrderAggregate.setUserId(userId);
        createPartakeOrderAggregate.setActivityId(activityId);
        createPartakeOrderAggregate.setActivityAccountEntity(activityAccountEntity);
        createPartakeOrderAggregate.setExistAccountMonth(isExitAccountMonth);
        createPartakeOrderAggregate.setActivityAccountMonthEntity(activityAccountMonthEntity);
        createPartakeOrderAggregate.setExistAccountDay(isExitAccountDay);
        createPartakeOrderAggregate.setActivityAccountDayEntity(activityAccountDayEntity);


        return createPartakeOrderAggregate;
    }

    /**
     * 构建订单
     * @param userId
     * @param activityId
     * @param currentDate
     * @return
     */
    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate) {

        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        //构建订单
        UserRaffleOrderEntity userRaffleOrder = new UserRaffleOrderEntity();
        userRaffleOrder.setUserId(userId);
        userRaffleOrder.setActivityId(activityId);
        userRaffleOrder.setActivityName(activityEntity.getActivityName());
        userRaffleOrder.setStrategyId(activityEntity.getStrategyId());
        userRaffleOrder.setOrderId(RandomStringUtils.randomNumeric(12));
        userRaffleOrder.setOrderTime(currentDate);
        userRaffleOrder.setOrderState(UserRaffleOrderStateVO.create);
        userRaffleOrder.setEndDateTime(activityEntity.getEndDateTime());

        return userRaffleOrder;
    }
}
