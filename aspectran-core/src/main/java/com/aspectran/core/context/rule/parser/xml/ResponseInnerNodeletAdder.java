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

import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.context.rule.DispatchResponseRule;
import com.aspectran.core.context.rule.ForwardResponseRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.RedirectResponseRule;
import com.aspectran.core.context.rule.TemplateRule;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.context.rule.ability.ResponseRuleApplicable;
import com.aspectran.core.context.rule.assistant.ContextRuleAssistant;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.xml.NodeletAdder;
import com.aspectran.core.util.xml.NodeletParser;

/**
 * The Class ResponseRuleNodeletAdder.
 * 
 * <p>Created: 2008. 06. 14 AM 6:56:29</p>
 */
class ResponseInnerNodeletAdder implements NodeletAdder {

    protected final ContextRuleAssistant assistant;

    /**
     * Instantiates a new ResponseInnerNodeletAdder.
     *
     * @param assistant the assistant for Context Builder
     */
    ResponseInnerNodeletAdder(ContextRuleAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public void process(String xpath, NodeletParser parser) {
        parser.addNodelet(xpath, "/transform", (node, attributes, text) -> {
            String type = attributes.get("type");
            String contentType = attributes.get("contentType");
            String characterEncoding = attributes.get("characterEncoding");
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));
            Boolean pretty = BooleanUtils.toNullableBooleanObject(attributes.get("pretty"));

            TransformRule tr = TransformRule.newInstance(type, contentType, characterEncoding, defaultResponse, pretty);
            assistant.pushObject(tr);

            ActionList actionList = new ActionList();
            assistant.pushObject(actionList);
        });
        parser.addNodelet(xpath, "/transform", new ActionNodeletAdder(assistant));
        parser.addNodelet(xpath, "/transform/template", (node, attributes, text) -> {
            String engine = attributes.get("engine");
            String name = attributes.get("name");
            String file = attributes.get("file");
            String resource = attributes.get("resource");
            String url = attributes.get("url");
            String style = attributes.get("style");
            String encoding = attributes.get("encoding");
            Boolean noCache = BooleanUtils.toNullableBooleanObject(attributes.get("noCache"));

            TemplateRule templateRule = TemplateRule.newInstanceForBuiltin(engine, name, file, resource, url, text, style, encoding, noCache);

            TransformRule transformRule = assistant.peekObject(1);
            transformRule.setTemplateRule(templateRule);

            assistant.resolveBeanClass(templateRule.getTemplateTokens());
        });
        parser.addNodelet(xpath, "/transform/call", (node, attributes, text) -> {
            String template = StringUtils.emptyToNull(attributes.get("template"));

            if (template == null) {
                throw new IllegalArgumentException("The <call> element inside <transform> must have the attribute 'template'");
            }

            TransformRule transformRule = assistant.peekObject(1);
            transformRule.setTemplateId(template);
        });
        parser.addNodelet(xpath, "/transform/end()", (node, attributes, text) -> {
            ActionList actionList = assistant.popObject();
            TransformRule tr = assistant.popObject();

            if (!actionList.isEmpty()) {
                tr.setActionList(actionList);
            }

            ResponseRuleApplicable applicable = assistant.peekObject();
            applicable.applyResponseRule(tr);
        });
        parser.addNodelet(xpath, "/dispatch", (node, attributes, text) -> {
            String name = attributes.get("name");
            String dispatcher = attributes.get("dispatcher");
            String contentType = attributes.get("contentType");
            String characterEncoding = attributes.get("characterEncoding");
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));

            DispatchResponseRule drr = DispatchResponseRule.newInstance(name, dispatcher, contentType, characterEncoding, defaultResponse);
            assistant.pushObject(drr);

            ActionList actionList = new ActionList();
            assistant.pushObject(actionList);
        });
        parser.addNodelet(xpath, "/dispatch", new ActionNodeletAdder(assistant));
        parser.addNodelet(xpath, "/dispatch/end()", (node, attributes, text) -> {
            ActionList actionList = assistant.popObject();
            DispatchResponseRule drr = assistant.popObject();

            if (!actionList.isEmpty()) {
                drr.setActionList(actionList);
            }

            ResponseRuleApplicable applicable = assistant.peekObject();
            applicable.applyResponseRule(drr);
        });
        parser.addNodelet(xpath, "/redirect", (node, attributes, text) -> {
            String contentType = attributes.get("contentType");
            String target = attributes.get("target");
            String characterEncoding = attributes.get("characterEncoding");
            Boolean excludeNullParameter = BooleanUtils.toNullableBooleanObject(attributes.get("excludeNullParameter"));
            Boolean excludeEmptyParameter = BooleanUtils.toNullableBooleanObject(attributes.get("excludeEmptyParameter"));
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));

            RedirectResponseRule rrr = RedirectResponseRule.newInstance(contentType, target, characterEncoding, excludeNullParameter, excludeEmptyParameter, defaultResponse);
            assistant.pushObject(rrr);

            ActionList actionList = new ActionList();
            assistant.pushObject(actionList);
        });
        parser.addNodelet(xpath, "/redirect", new ActionNodeletAdder(assistant));
        parser.addNodelet(xpath, "/redirect/parameters", (node, attributes, text) -> {
            ItemRuleMap irm = new ItemRuleMap();
            assistant.pushObject(irm);
        });
        parser.addNodelet(xpath, "/redirect/parameters", new ItemNodeletAdder(assistant));
        parser.addNodelet(xpath, "/redirect/parameters/end()", (node, attributes, text) -> {
            ItemRuleMap irm = assistant.popObject();

            if (!irm.isEmpty()) {
                RedirectResponseRule rrr = assistant.peekObject(1);
                rrr.setParameterItemRuleMap(irm);
            }
        });
        parser.addNodelet(xpath, "/redirect/end()", (node, attributes, text) -> {
            ActionList actionList = assistant.popObject();
            RedirectResponseRule rrr = assistant.popObject();

            if (rrr.getTarget() == null) {
                throw new IllegalArgumentException("The <redirect> element requires a 'target' attribute");
            }

            if (!actionList.isEmpty()) {
                rrr.setActionList(actionList);
            }

            ResponseRuleApplicable applicable = assistant.peekObject();
            applicable.applyResponseRule(rrr);

            assistant.resolveBeanClass(rrr.getTargetTokens());
        });
        parser.addNodelet(xpath, "/forward", (node, attributes, text) -> {
            String contentType = attributes.get("contentType");
            String transletName = attributes.get("translet");
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));

            transletName = assistant.applyTransletNamePattern(transletName);

            ForwardResponseRule frr = ForwardResponseRule.newInstance(contentType, transletName, defaultResponse);
            assistant.pushObject(frr);

            ActionList actionList = new ActionList();
            assistant.pushObject(actionList);
        });
        parser.addNodelet(xpath, "/forward", new ActionNodeletAdder(assistant));
        parser.addNodelet(xpath, "/forward/parameters", (node, attributes, text) -> {
            ItemRuleMap irm = new ItemRuleMap();
            assistant.pushObject(irm);
        });
        parser.addNodelet(xpath, "/forward/parameters", new ItemNodeletAdder(assistant));
        parser.addNodelet(xpath, "/forward/parameters/end()", (node, attributes, text) -> {
            ItemRuleMap irm = assistant.popObject();

            if (irm.size() > 0) {
                ForwardResponseRule frr = assistant.peekObject(1);
                frr.setAttributeItemRuleMap(irm);
            }
        });
        parser.addNodelet(xpath, "/forward/end()", (node, attributes, text) -> {
            ActionList actionList = assistant.popObject();
            ForwardResponseRule frr = assistant.popObject();

            if (!actionList.isEmpty()) {
                frr.setActionList(actionList);
            }

            ResponseRuleApplicable applicable = assistant.peekObject();
            applicable.applyResponseRule(frr);
        });
    }

}