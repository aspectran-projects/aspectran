/*
 * Copyright 2008-2017 Juho Jeong
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
package com.aspectran.core.context.rule.parser.xml;

import com.aspectran.core.context.rule.ScheduleJobRule;
import com.aspectran.core.context.rule.ScheduleRule;
import com.aspectran.core.context.rule.assistant.ContextRuleAssistant;
import com.aspectran.core.context.rule.type.ContentStyleType;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.xml.NodeletAdder;
import com.aspectran.core.util.xml.NodeletParser;

/**
 * The Class ScheduleNodeletAdder.
 * 
 * <p>Created: 2016. 08. 29.</p>
 */
class ScheduleNodeletAdder implements NodeletAdder {

    protected final ContextRuleAssistant assistant;

    /**
     * Instantiates a new ScheduleNodeletAdder.
     *
     * @param assistant the assistant
     */
    ScheduleNodeletAdder(ContextRuleAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public void process(String xpath, NodeletParser parser) {
        parser.addNodelet(xpath, "/schedule", (node, attributes, text) -> {
            String id = StringUtils.emptyToNull(attributes.get("id"));

            ScheduleRule scheduleRule = ScheduleRule.newInstance(id);

            assistant.pushObject(scheduleRule);
        });
        parser.addNodelet(xpath, "/schedule/description", (node, attributes, text) -> {
            if (text != null) {
                String style = attributes.get("style");
                text = ContentStyleType.apply(text, style);

                ScheduleRule scheduleRule = assistant.peekObject();
                scheduleRule.setDescription(text);
            }
        });
        parser.addNodelet(xpath, "/schedule/scheduler", (node, attributes, text) -> {
            String beanIdOrClass = StringUtils.emptyToNull(attributes.get("bean"));

            if (beanIdOrClass != null) {
                ScheduleRule scheduleRule = assistant.peekObject();
                scheduleRule.setSchedulerBeanId(beanIdOrClass);
            }
        });
        parser.addNodelet(xpath, "/schedule/scheduler/trigger", (node, attributes, text) -> {
            String type = StringUtils.emptyToNull(attributes.get("type"));

            ScheduleRule scheduleRule = assistant.peekObject();
            ScheduleRule.updateTrigger(scheduleRule, type, text);
        });
        parser.addNodelet(xpath, "/schedule/job", (node, attributes, text) -> {
            String transletName = StringUtils.emptyToNull(attributes.get("translet"));
            String method = StringUtils.emptyToNull(attributes.get("method"));
            Boolean disabled = BooleanUtils.toNullableBooleanObject(attributes.get("disabled"));

            if (transletName == null) {
                throw new IllegalArgumentException("The 'job' element requires a 'translet' attribute");
            }

            transletName = assistant.applyTransletNamePattern(transletName);

            ScheduleRule scheduleRule = assistant.peekObject();

            ScheduleJobRule scheduleJobRule = ScheduleJobRule.newInstance(scheduleRule, transletName, method, disabled);
            scheduleRule.addScheduleJobRule(scheduleJobRule);
        });
        parser.addNodelet(xpath, "/schedule/end()", (node, attributes, text) -> {
            ScheduleRule scheduleRule = assistant.popObject();

            assistant.addScheduleRule(scheduleRule);
        });
    }

}