/*
 * Copyright (c) 2008-2019 The Aspectran Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.activity.process.action;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpression;
import com.aspectran.core.context.rule.IncludeActionRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.type.ActionType;
import com.aspectran.core.util.ToStringBuilder;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.util.Map;

/**
 * The Class IncludeAction.
 * 
 * <p>Created: 2008. 06. 05 PM 9:22:05</p>
 */
public class IncludeAction extends AbstractAction {

    private static final Log log = LogFactory.getLog(IncludeAction.class);

    private final IncludeActionRule includeActionRule;

    /**
     * Instantiates a new IncludeAction.
     *
     * @param includeActionRule the process call action rule
     */
    public IncludeAction(IncludeActionRule includeActionRule) {
        this.includeActionRule = includeActionRule;
    }

    @Override
    public Object execute(Activity activity) throws Exception {
        Activity innerActivity = null;
        try {
            innerActivity = activity.newActivity();
            innerActivity.prepare(includeActionRule.getTransletName(), includeActionRule.getMethodType());

            ItemRuleMap parameterItemRuleMap = includeActionRule.getParameterItemRuleMap();
            ItemRuleMap attributeItemRuleMap = includeActionRule.getAttributeItemRuleMap();
            if ((parameterItemRuleMap != null && !parameterItemRuleMap.isEmpty()) ||
                    (attributeItemRuleMap != null && !attributeItemRuleMap.isEmpty())) {
                ItemEvaluator evaluator = new ItemExpression(activity);
                if (parameterItemRuleMap != null) {
                    Map<String, Object> valueMap = evaluator.evaluate(parameterItemRuleMap);
                    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                        innerActivity.getRequestAdapter().setParameter(entry.getKey(), entry.getValue().toString());
                    }
                }
                if (attributeItemRuleMap != null) {
                    Map<String, Object> valueMap = evaluator.evaluate(attributeItemRuleMap);
                    innerActivity.getRequestAdapter().putAllAttributes(valueMap);
                }
            }

            innerActivity.perform();
            return innerActivity.getProcessResult();
        } catch (Exception e) {
            throw new ActionExecutionException("Failed to execute include action " + this, e);
        } finally {
            if (innerActivity != null) {
                innerActivity.finish();
            }
        }
    }

    /**
     * Returns the include action rule.
     *
     * @return the include action rule
     */
    public IncludeActionRule getIncludeActionRule() {
        return includeActionRule;
    }

    @Override
    public String getActionId() {
        return includeActionRule.getActionId();
    }

    @Override
    public boolean isHidden() {
        return includeActionRule.isHidden();
    }

    @Override
    public ActionType getActionType() {
        return ActionType.INCLUDE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getActionRule() {
        return (T)getIncludeActionRule();
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("actionType", getActionType());
        tsb.append("includeActionRule", includeActionRule);
        return tsb.toString();
    }

}
