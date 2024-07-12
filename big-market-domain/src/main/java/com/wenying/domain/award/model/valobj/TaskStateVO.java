package com.wenying.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * 任务状态值对象
 */
@Getter
@AllArgsConstructor
public enum TaskStateVO {
    create("create","创建"),
    complete("complete","发奖完成"),
    fail("fail","发奖失败"),
    ;

    private final String code;
    private final String desc;
}
