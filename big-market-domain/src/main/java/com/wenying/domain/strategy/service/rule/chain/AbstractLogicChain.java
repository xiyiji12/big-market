package com.wenying.domain.strategy.service.rule.chain;

public abstract class AbstractLogicChain implements ILogicChain{
    private ILogicChain next;//添加链

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    @Override
    public ILogicChain next() {
        return next;
    }

    protected abstract String ruleModel();//抽象方法
}
