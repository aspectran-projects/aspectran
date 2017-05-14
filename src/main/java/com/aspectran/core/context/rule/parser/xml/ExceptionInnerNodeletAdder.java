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

import com.aspectran.core.context.rule.ExceptionRule;
import com.aspectran.core.context.rule.ExceptionThrownRule;
import com.aspectran.core.context.rule.assistant.ContextRuleAssistant;
import com.aspectran.core.context.rule.type.ContentStyleType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.xml.NodeletAdder;
import com.aspectran.core.util.xml.NodeletParser;

/**
 * The Class ExceptionInnerNodeletAdder.
 *
 * @since 2013. 8. 11.
 */
class ExceptionInnerNodeletAdder implements NodeletAdder {

    protected final ContextRuleAssistant assistant;

    /**
     * Instantiates a new ExceptionInnerNodeletAdder.
     *
     * @param assistant the assistant for Context Builder
     */
    ExceptionInnerNodeletAdder(ContextRuleAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public void process(String xpath, NodeletParser parser) {
        parser.addNodelet(xpath, "/description", (node, attributes, text) -> {
            if (text != null) {
                String style = attributes.get("style");
                text = ContentStyleType.apply(text, style);

                ExceptionRule exceptionRule = assistant.peekObject();
                exceptionRule.setDescription(text);
            }
        });
        parser.addNodelet(xpath, new ActionNodeletAdder(assistant));
        parser.addNodelet(xpath, "/thrown", (node, attributes, text) -> {
            String exceptionType = attributes.get("type");

            ExceptionThrownRule etr = new ExceptionThrownRule();
            if (exceptionType != null) {
                String[] exceptionTypes = StringUtils.splitCommaDelimitedString(exceptionType);
                etr.setExceptionTypes(exceptionTypes);
            }

            assistant.pushObject(etr);
        });
        parser.addNodelet(xpath, "/thrown", new ActionNodeletAdder(assistant));
        parser.addNodelet(xpath, "/thrown", new ResponseInnerNodeletAdder(assistant));
        parser.addNodelet(xpath, "/thrown/end()", (node, attributes, text) -> {
            ExceptionThrownRule etr = assistant.popObject();
            ExceptionRule exceptionRule = assistant.peekObject();
            exceptionRule.putExceptionThrownRule(etr);
        });
    }

}
