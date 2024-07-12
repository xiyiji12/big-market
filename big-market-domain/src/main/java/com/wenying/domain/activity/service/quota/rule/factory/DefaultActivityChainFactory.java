package com.wenying.domain.activity.service.quota.rule.factory;

import com.wenying.domain.activity.service.quota.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 责任链工厂
 */
@Service
public class DefaultActivityChainFactory {

    private final IActionChain actionChain;

    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainGroup) {
        actionChain = actionChainGroup.get(ActionModel.activity_base_action.code);//拿到当前链
        actionChain.appendNext(actionChainGroup.get(ActionModel.activity_sku_stock_action.code));//填充下一个链
    }

    public IActionChain openActionChain(){//使用链
        return this.actionChain;
    }

    @Getter
    @AllArgsConstructor
    public enum ActionModel {

        activity_base_action("activity_base_action","活动的时间、状态校验"),
        activity_sku_stock_action("activity_sku_stock_action","活动sku库存")
        ;

        private final String code;
        private final String info;
    }

}
