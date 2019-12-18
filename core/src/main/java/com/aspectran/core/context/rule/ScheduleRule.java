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

import com.aspectran.core.context.rule.ability.BeanReferenceable;
import com.aspectran.core.context.rule.params.TriggerExpressionParameters;
import com.aspectran.core.context.rule.params.TriggerParameters;
import com.aspectran.core.context.rule.type.BeanRefererType;
import com.aspectran.core.context.rule.type.TriggerType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.ToStringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Class ScheduleRule.
 * 
 * <pre>
 * &lt;schedule id="schedule-1"&gt;
 *   &lt;scheduler bean="schedulerFactory"&gt;
 *     &lt;trigger type="simple"&gt;
 *       startDelaySeconds: 10
 *       intervalInSeconds: 10
 *       repeatCount: 10
 *     &lt;/trigger&gt;
 *   &lt;/scheduler&gt;
 *   &lt;job translet="/a/b/c/action1"/&gt;
 *   &lt;job translet="/a/b/c/action2"/&gt;
 *   &lt;job translet="/a/b/c/action3"/&gt;
 * &lt;schedule&gt;
 * </pre>
 */
public class ScheduleRule implements BeanReferenceable {

    private static final BeanRefererType BEAN_REFERER_TYPE = BeanRefererType.SCHEDULE_RULE;

    private String id;

    private TriggerType triggerType;

    private TriggerExpressionParameters triggerExpressionParameters;

    private String schedulerBeanId;

    private Class<?> schedulerBeanClass;

    private List<ScheduledJobRule> scheduledJobRuleList = new ArrayList<>();

    private DescriptionRule descriptionRule;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public TriggerExpressionParameters getTriggerExpressionParameters() {
        return triggerExpressionParameters;
    }

    public void setTriggerExpressionParameters(TriggerExpressionParameters triggerExpressionParameters) {
        this.triggerExpressionParameters = triggerExpressionParameters;
    }

    public String getSchedulerBeanId() {
        return schedulerBeanId;
    }

    public void setSchedulerBeanId(String schedulerBeanId) {
        this.schedulerBeanId = schedulerBeanId;
    }

    public Class<?> getSchedulerBeanClass() {
        return schedulerBeanClass;
    }

    public void setSchedulerBeanClass(Class<?> schedulerBeanClass) {
        this.schedulerBeanClass = schedulerBeanClass;
    }

    public List<ScheduledJobRule> getScheduledJobRuleList() {
        return scheduledJobRuleList;
    }

    public void setScheduledJobRuleList(List<ScheduledJobRule> scheduledJobRuleList) {
        this.scheduledJobRuleList = scheduledJobRuleList;
    }

    public void addScheduledJobRule(ScheduledJobRule scheduledJobRule) {
        scheduledJobRuleList.add(scheduledJobRule);
    }

    public DescriptionRule getDescriptionRule() {
        return descriptionRule;
    }

    public void setDescriptionRule(DescriptionRule descriptionRule) {
        this.descriptionRule = descriptionRule;
    }

    @Override
    public BeanRefererType getBeanRefererType() {
        return BEAN_REFERER_TYPE;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("id", id);
        tsb.append("scheduler", schedulerBeanId);
        tsb.append("trigger", triggerExpressionParameters);
        tsb.append("jobs", scheduledJobRuleList);
        return tsb.toString();
    }

    public static ScheduleRule newInstance(String id) throws IllegalRuleException {
        if (id == null) {
            throw new IllegalRuleException("The 'schedule' element requires an 'id' attribute");
        }

        ScheduleRule scheduleRule = new ScheduleRule();
        scheduleRule.setId(id);
        return scheduleRule;
    }

    public static void updateTrigger(ScheduleRule scheduleRule, TriggerParameters triggerParameters) throws IllegalRuleException {
        updateTriggerType(scheduleRule, triggerParameters.getString(TriggerParameters.type));
        TriggerExpressionParameters expressionParameters = triggerParameters.getParameters(TriggerParameters.expression);
        if (expressionParameters == null) {
            throw new IllegalRuleException("Be sure to specify trigger expression parameters " +
                    Arrays.toString(TriggerExpressionParameters.getParameterKeys()));
        }
        updateTriggerExpression(scheduleRule, expressionParameters);
    }

    public static void updateTrigger(ScheduleRule scheduleRule, String type, String expression) throws IllegalRuleException {
        updateTriggerType(scheduleRule, type);
        updateTriggerExpression(scheduleRule, expression);
    }

    public static void updateTriggerType(ScheduleRule scheduleRule, String type) {
        TriggerType triggerType;
        if (type != null) {
            triggerType = TriggerType.resolve(type);
            if (triggerType == null) {
                throw new IllegalArgumentException("Unknown trigger type '" + type + "'; Trigger type for Scheduler must be 'cron' or 'simple'");
            }
        } else {
            triggerType = TriggerType.CRON;
        }
        scheduleRule.setTriggerType(triggerType);
    }

    public static void updateTriggerExpression(ScheduleRule scheduleRule, String expression) throws IllegalRuleException {
        if (StringUtils.hasText(expression)) {
            TriggerExpressionParameters expressionParameters;
            try {
                expressionParameters = new TriggerExpressionParameters(expression);
            } catch (IOException e) {
                throw new IllegalRuleException("Trigger expression parameters can not be parsed", e);
            }
            updateTriggerExpression(scheduleRule, expressionParameters);
        }
    }

    public static void updateTriggerExpression(ScheduleRule scheduleRule, TriggerExpressionParameters expressionParameters) {
        if (scheduleRule.getTriggerType() == TriggerType.SIMPLE) {
            Long intervalInMilliseconds = expressionParameters.getLong(TriggerExpressionParameters.intervalInMilliseconds);
            Integer intervalInSeconds = expressionParameters.getInt(TriggerExpressionParameters.intervalInSeconds);
            Integer intervalInMinutes = expressionParameters.getInt(TriggerExpressionParameters.intervalInMinutes);
            Integer intervalInHours = expressionParameters.getInt(TriggerExpressionParameters.intervalInHours);
            if (intervalInMilliseconds == null && intervalInSeconds == null && intervalInMinutes == null && intervalInHours == null) {
                throw new IllegalArgumentException("Must specify the interval between execution times for simple trigger. (" +
                        "Specifiable time interval types: intervalInMilliseconds, intervalInSeconds, intervalInMinutes, intervalInHours)");
            }
        } else {
            String expression = expressionParameters.getString(TriggerExpressionParameters.expression);
            String[] fields = StringUtils.tokenize(expression, " ");
            if (fields.length != 6) {
                throw new IllegalArgumentException(String.format("Cron expression must consist of 6 fields (found %d in %s)", fields.length, expression));
            }
            expressionParameters.putValue(TriggerParameters.expression, StringUtils.toDelimitedString(fields, " "));
        }
        scheduleRule.setTriggerExpressionParameters(expressionParameters);
    }

}
