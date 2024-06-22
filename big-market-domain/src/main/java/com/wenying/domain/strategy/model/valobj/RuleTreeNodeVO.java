package com.wenying.domain.strategy.model.valobj;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description 规则树节点对象
 * 决策树中的一个节点。节点包含规则的键、描述、值以及节点连接信息。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeVO {

    /** 规则树ID */
    private String treeId;
    /** 规则Key */
    private String ruleKey;
    /** 规则描述 */
    private String ruleDesc;
    /** 规则比值 */
    private String ruleValue;

    /** 规则连线 */
    private List<RuleTreeNodeLineVO> treeNodeLineVOList;

}
