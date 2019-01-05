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
package com.aspectran.core.context.rule.parser.xml;

import com.aspectran.core.context.rule.BeanMethodActionRule;
import com.aspectran.core.context.rule.EchoActionRule;
import com.aspectran.core.context.rule.HeaderActionRule;
import com.aspectran.core.context.rule.IncludeActionRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.ability.ActionRuleApplicable;
import com.aspectran.core.context.rule.assistant.ContextRuleAssistant;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.nodelet.NodeletAdder;
import com.aspectran.core.util.nodelet.NodeletParser;

/**
 * The Class ActionNodeletAdder.
 *
 * @since 2011. 1. 9.
 */
class ActionNodeletAdder implements NodeletAdder {

    protected final ContextRuleAssistant assistant;

    /**
     * Instantiates a new ActionNodeletAdder.
     *
     * @param assistant the ContextRuleAssistant
     */
    ActionNodeletAdder(ContextRuleAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public void add(String xpath, NodeletParser parser) {
        parser.setXpath(xpath + "/action");
        parser.addNodelet(attrs -> {
            String id = StringUtils.emptyToNull(attrs.get("id"));
            String beanIdOrClass = StringUtils.emptyToNull(attrs.get("bean"));
            String methodName = StringUtils.emptyToNull(attrs.get("method"));
            Boolean hidden = BooleanUtils.toNullableBooleanObject(attrs.get("hidden"));

            BeanMethodActionRule beanActionRule = BeanMethodActionRule.newInstance(id, beanIdOrClass, methodName, hidden);
            assistant.resolveActionBeanClass(beanActionRule);
            parser.pushObject(beanActionRule);
        });
        parser.addNodeEndlet(text -> {
            BeanMethodActionRule beanMethodActionRule = parser.popObject();
            ActionRuleApplicable applicable = parser.peekObject();
            applicable.applyActionRule(beanMethodActionRule);
        });
        parser.setXpath(xpath + "/action/arguments");
        parser.addNodelet(attrs -> {
            ItemRuleMap irm = new ItemRuleMap();
            parser.pushObject(irm);
        });
        parser.addNodelet(new ItemNodeletAdder(assistant));
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            if (!irm.isEmpty()) {
                BeanMethodActionRule beanActionRule = parser.peekObject();
                beanActionRule.setArgumentItemRuleMap(irm);
            }
        });
        parser.setXpath(xpath + "/action/properties");
        parser.addNodelet(attrs -> {
            ItemRuleMap irm = new ItemRuleMap();
            parser.pushObject(irm);
        });
        parser.addNodelet(new ItemNodeletAdder(assistant));
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            if (!irm.isEmpty()) {
                BeanMethodActionRule beanActionRule = parser.peekObject();
                beanActionRule.setPropertyItemRuleMap(irm);
            }
        });
        parser.setXpath(xpath + "/include");
        parser.addNodelet(attrs -> {
            String id = StringUtils.emptyToNull(attrs.get("id"));
            String transletName = StringUtils.emptyToNull(attrs.get("translet"));
            String methodType = StringUtils.emptyToNull(attrs.get("method"));
            Boolean hidden = BooleanUtils.toNullableBooleanObject(attrs.get("hidden"));

            transletName = assistant.applyTransletNamePattern(transletName);

            IncludeActionRule includeActionRule = IncludeActionRule.newInstance(id, transletName, methodType, hidden);
            parser.pushObject(includeActionRule);
        });
        parser.addNodeEndlet(text -> {
            IncludeActionRule includeActionRule = parser.popObject();
            ActionRuleApplicable applicable = parser.peekObject();
            applicable.applyActionRule(includeActionRule);
        });
        parser.setXpath(xpath + "/include/parameters");
        parser.addNodelet(attrs -> {
            ItemRuleMap irm = new ItemRuleMap();
            parser.pushObject(irm);
        });
        parser.addNodelet(new ItemNodeletAdder(assistant));
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            if (!irm.isEmpty()) {
                IncludeActionRule includeActionRule = parser.peekObject();
                includeActionRule.setParameterItemRuleMap(irm);
            }
        });
        parser.setXpath(xpath + "/include/attributes");
        parser.addNodelet(attrs -> {
            ItemRuleMap irm = new ItemRuleMap();
            parser.pushObject(irm);
        });
        parser.addNodelet(new ItemNodeletAdder(assistant));
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            if (!irm.isEmpty()) {
                IncludeActionRule includeActionRule = parser.peekObject();
                includeActionRule.setAttributeItemRuleMap(irm);
            }
        });
        parser.setXpath(xpath + "/echo");
        parser.addNodelet(attrs -> {
            String id = StringUtils.emptyToNull(attrs.get("id"));
            Boolean hidden = BooleanUtils.toNullableBooleanObject(attrs.get("hidden"));

            EchoActionRule echoActionRule = EchoActionRule.newInstance(id, hidden);
            parser.pushObject(echoActionRule);

            ItemRuleMap irm = new ItemRuleMap();
            parser.pushObject(irm);
        });
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            EchoActionRule echoActionRule = parser.popObject();

            if (!irm.isEmpty()) {
                echoActionRule.setAttributeItemRuleMap(irm);
            }

            ActionRuleApplicable applicable = parser.peekObject();
            applicable.applyActionRule(echoActionRule);
        });
        parser.addNodelet(new ItemNodeletAdder(assistant));
        parser.setXpath(xpath + "/echo/attributes");
        parser.addNodelet(new ItemNodeletAdder(assistant));
        parser.setXpath(xpath + "/headers");
        parser.addNodelet(attrs -> {
            String id = StringUtils.emptyToNull(attrs.get("id"));
            Boolean hidden = BooleanUtils.toNullableBooleanObject(attrs.get("hidden"));

            HeaderActionRule headerActionRule = HeaderActionRule.newInstance(id, hidden);
            parser.pushObject(headerActionRule);

            ItemRuleMap irm = new ItemRuleMap();
            parser.pushObject(irm);
        });
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            HeaderActionRule headerActionRule = parser.popObject();

            if (!irm.isEmpty()) {
                headerActionRule.setHeaderItemRuleMap(irm);
            }

            ActionRuleApplicable applicable = parser.peekObject();
            applicable.applyActionRule(headerActionRule);
        });
        parser.addNodelet(new ItemNodeletAdder(assistant));
    }

}
