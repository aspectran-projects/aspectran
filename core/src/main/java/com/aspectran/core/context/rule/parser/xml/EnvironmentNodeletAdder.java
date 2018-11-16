/*
 * Copyright (c) 2008-2018 The Aspectran Project
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

import com.aspectran.core.context.rule.EnvironmentRule;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.assistant.ContextRuleAssistant;
import com.aspectran.core.context.rule.type.ContentStyleType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.nodelet.NodeletAdder;
import com.aspectran.core.util.nodelet.NodeletParser;

/**
 * The Class EnvironmentNodeletAdder.
 * 
 * <p>Created: 2016. 01. 09</p>
 */
class EnvironmentNodeletAdder implements NodeletAdder {

    protected final ContextRuleAssistant assistant;

    /**
     * Instantiates a new EnvironmentNodeletAdder.
     *
     * @param assistant the assistant
     */
    EnvironmentNodeletAdder(ContextRuleAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public void process(String xpath, NodeletParser parser) {
        parser.setXpath(xpath + "/environment");
        parser.addNodelet(attrs -> {
            String profile = attrs.get("profile");

            EnvironmentRule environmentRule = EnvironmentRule.newInstance(profile, null);
            parser.pushObject(environmentRule);
        });
        parser.addNodeEndlet(text -> {
            EnvironmentRule environmentRule = parser.popObject();
            assistant.addEnvironmentRule(environmentRule);
        });
        parser.setXpath(xpath + "/environment/description");
        parser.addNodelet(attrs -> {
            String style = attrs.get("style");
            parser.pushObject(style);
        });
        parser.addNodeEndlet(text -> {
            String style = parser.popObject();
            if (style != null) {
                text = ContentStyleType.styling(text, style);
            }
            if (StringUtils.hasText(text)) {
                EnvironmentRule environmentRule = parser.peekObject();
                environmentRule.setDescription(text);
            }
        });
        parser.setXpath(xpath + "/environment/properties");
        parser.addNodelet(attrs -> {
            ItemRuleMap irm = new ItemRuleMap();
            parser.pushObject(irm);
        });
        parser.addNodelet(new ItemNodeletAdder(assistant));
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            EnvironmentRule environmentRule = parser.peekObject();
            if (!irm.isEmpty()) {
                environmentRule.setPropertyItemRuleMap(irm);
            } else if (StringUtils.hasLength(text)) {
                ItemRuleMap propertyItemRuleMap = ItemRule.toItemRuleMap(text);
                environmentRule.setPropertyItemRuleMap(propertyItemRuleMap);
            }
        });
    }

}