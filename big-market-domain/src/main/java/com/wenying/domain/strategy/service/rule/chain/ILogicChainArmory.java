package com.wenying.domain.strategy.service.rule.chain;

/**
 * 责任链接口
 */
public interface ILogicChainArmory {

    /**
     * 构建结点信息，填充链
     * @param next
     * @return
     */
    ILogicChain appendNext(ILogicChain next);

    /**
     * 下一个节点是什么
     * @return
     */
    ILogicChain next();
}
