package com.aspectran.core.context.rule;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Created: 2019-01-06</p>
 */
public class CaseRule {

    private final int caseNo;

    private Map<Integer, CaseWhenRule> caseWhenRuleMap;

    public CaseRule(int caseNo) {
        this.caseNo = caseNo;
    }

    public CaseWhenRule newCaseWhenRule() {
        if (caseWhenRuleMap == null) {
            caseWhenRuleMap = new LinkedHashMap<>();
        }
        int caseWhenNo = (caseNo * 1000) + caseWhenRuleMap.size();
        CaseWhenRule caseWhenRule = new CaseWhenRule(caseWhenNo);
        caseWhenRuleMap.put(caseWhenNo, caseWhenRule);
        return caseWhenRule;
    }

    public CaseWhenRule getCaseWhenRule(int caseWhenNo) {
        return (caseWhenRuleMap != null ? caseWhenRuleMap.get(caseWhenNo) : null);
    }

}
