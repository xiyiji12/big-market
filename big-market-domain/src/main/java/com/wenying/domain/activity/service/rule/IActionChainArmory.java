package com.wenying.domain.activity.service.rule;

/**
 * 抽奖动作责任链装配
 */
public interface IActionChainArmory {

    /**
     * 构建结点信息，填充链
     * @param next
     * @return
     */
    IActionChain appendNext(IActionChain next);

    /**
     * 下一个节点是什么
     * @return
     */
    IActionChain next();
}
