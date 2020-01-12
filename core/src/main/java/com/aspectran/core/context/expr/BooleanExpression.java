/*
 * Copyright (c) 2008-2020 The Aspectran Project
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
package com.aspectran.core.context.expr;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.ActivityDataMap;
import com.aspectran.core.context.expr.ognl.OgnlSupport;
import com.aspectran.core.context.rule.ChooseWhenRule;
import com.aspectran.core.context.rule.IllegalRuleException;

/**
 * It supports expressions in the CHOOSE-WHEN statement,
 * and evaluates the expression as a boolean result.
 *
 * <p>Created: 2019-01-06</p>
 *
 * @since 6.0.0
 */
public class BooleanExpression {

    protected final Activity activity;

    public BooleanExpression(Activity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("activity must not be null");
        }
        this.activity = activity;
    }

    public boolean evaluate(ChooseWhenRule chooseWhenRule) throws IllegalRuleException {
        if (chooseWhenRule.getExpression() == null) {
            return true;
        }
        ActivityDataMap root = (activity.getTranslet() != null ? activity.getTranslet().getActivityDataMap() : null);
        return OgnlSupport.evaluateAsBoolean(chooseWhenRule.getExpression(), chooseWhenRule.getRepresented(), root);
    }

    public static Object parseExpression(String expression) throws IllegalRuleException {
        return OgnlSupport.parseExpression(expression);
    }

}
