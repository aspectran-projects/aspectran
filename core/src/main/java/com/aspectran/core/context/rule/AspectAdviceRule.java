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
package com.aspectran.core.context.rule;

import com.aspectran.core.activity.process.action.BeanMethodAction;
import com.aspectran.core.activity.process.action.ConfigBeanMethodAction;
import com.aspectran.core.activity.process.action.EchoAction;
import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.activity.process.action.HeaderAction;
import com.aspectran.core.context.rule.ability.ActionRuleApplicable;
import com.aspectran.core.context.rule.type.ActionType;
import com.aspectran.core.context.rule.type.AspectAdviceType;
import com.aspectran.core.util.ToStringBuilder;

/**
 * Advices are actions taken for a particular join point.
 * In terms of programming, they are methods that gets executed
 * when a certain join point with matching pointcut is reached
 * in the application.
 * 
 * <p>Created: 2008. 04. 01 PM 11:19:28</p>
 */
public class AspectAdviceRule implements ActionRuleApplicable {

    private final AspectRule aspectRule;

    private final String aspectId;

    private final String adviceBeanId;

    private final Class<?> adviceBeanClass;

    private final AspectAdviceType aspectAdviceType;

    private Executable action;

    private ExceptionRule exceptionRule;

    private ExceptionThrownRule exceptionThrownRule;

    public AspectAdviceRule(AspectRule aspectRule, AspectAdviceType aspectAdviceType) {
        this.aspectRule = aspectRule;
        this.aspectId = aspectRule.getId();
        this.adviceBeanId = aspectRule.getAdviceBeanId();
        this.adviceBeanClass = aspectRule.getAdviceBeanClass();
        this.aspectAdviceType = aspectAdviceType;
    }

    public String getAspectId() {
        return aspectId;
    }

    public AspectRule getAspectRule() {
        return aspectRule;
    }

    public String getAdviceBeanId() {
        return adviceBeanId;
    }

    public Class<?> getAdviceBeanClass() {
        return adviceBeanClass;
    }

    public AspectAdviceType getAspectAdviceType() {
        return aspectAdviceType;
    }

    @Override
    public Executable applyActionRule(BeanMethodActionRule beanMethodActionRule) {
        BeanMethodAction action = new BeanMethodAction(beanMethodActionRule, null);
        if (beanMethodActionRule.getBeanId() == null) {
            action.setAspectAdviceRule(this);
        }
        this.action = action;
        return action;
    }

    @Override
    public Executable applyActionRule(ConfigBeanMethodActionRule configBeanMethodActionRule) {
        throw new UnsupportedOperationException(
                "Cannot apply the config bean method action Rule to the Aspect Advice Rule");
    }

    @Override
    public Executable applyActionRule(IncludeActionRule includeActionRule) {
        throw new UnsupportedOperationException(
                "Cannot apply the include action rule to the Aspect Advice Rule; " +
                "AspectAdvice is not support IncludeAction");
    }

    @Override
    public Executable applyActionRule(EchoActionRule echoActionRule) {
        Executable action = new EchoAction(echoActionRule, null);
        this.action = action;
        return action;
    }

    @Override
    public Executable applyActionRule(HeaderActionRule headerActionRule) {
        Executable action = new HeaderAction(headerActionRule, null);
        this.action = action;
        return action;
    }

    @Override
    public void applyActionRule(Executable action) {
        this.action = action;
    }

    public Executable getExecutableAction() {
        return action;
    }

    public void setExecutableAction(ConfigBeanMethodAction action) {
        this.action = action;
    }

    public ActionType getActionType() {
        return (action != null ? action.getActionType() : null);
    }

    public ExceptionRule getExceptionRule() {
        return exceptionRule;
    }

    public ExceptionThrownRule getExceptionThrownRule() {
        return exceptionThrownRule;
    }

    public void setExceptionThrownRule(ExceptionThrownRule exceptionThrownRule) {
        ExceptionRule exceptionRule = new ExceptionRule();
        exceptionRule.putExceptionThrownRule(exceptionThrownRule);

        this.exceptionRule = exceptionRule;
        this.exceptionThrownRule = exceptionThrownRule;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean preventRecursive) {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("aspectId", aspectId);
        tsb.append("adviceBeanId", adviceBeanId);
        tsb.append("adviceBeanClass", adviceBeanClass);
        tsb.append("aspectAdviceType", aspectAdviceType);
        if (!preventRecursive) {
            tsb.append("action", action);
        }
        return tsb.toString();
    }

    public static AspectAdviceRule newInstance(AspectRule aspectRule, AspectAdviceType aspectAdviceType) {
        return new AspectAdviceRule(aspectRule, aspectAdviceType);
    }

}
