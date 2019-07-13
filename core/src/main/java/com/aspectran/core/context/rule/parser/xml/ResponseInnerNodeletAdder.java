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

import com.aspectran.core.context.rule.ChooseRule;
import com.aspectran.core.context.rule.ChooseRuleMap;
import com.aspectran.core.context.rule.DispatchRule;
import com.aspectran.core.context.rule.ForwardRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.RedirectRule;
import com.aspectran.core.context.rule.TemplateRule;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.ability.ResponseRuleApplicable;
import com.aspectran.core.context.rule.assistant.ContextRuleAssistant;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.nodelet.NodeletAdder;
import com.aspectran.core.util.nodelet.NodeletParser;

/**
 * The Class ResponseInnerNodeletAdder.
 * 
 * <p>Created: 2008. 06. 14 AM 6:56:29</p>
 */
class ResponseInnerNodeletAdder implements NodeletAdder {

    @Override
    public void add(String xpath, NodeletParser parser) {
        AspectranNodeParser nodeParser = parser.getNodeParser();
        ActionNodeletAdder actionNodeletAdder = nodeParser.getActionNodeletAdder();
        ChooseWhenNodeletAdder chooseWhenNodeletAdder = nodeParser.getChooseWhenNodeletAdder();
        ItemNodeletAdder itemNodeletAdder = nodeParser.getItemNodeletAdder();
        ContextRuleAssistant assistant = nodeParser.getAssistant();

        parser.setXpath(xpath + "/transform");
        parser.addNodelet(attrs -> {
            String type = attrs.get("type");
            String contentType = attrs.get("contentType");
            String encoding = attrs.get("encoding");
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attrs.get("default"));
            Boolean pretty = BooleanUtils.toNullableBooleanObject(attrs.get("pretty"));

            TransformRule transformRule = TransformRule.newInstance(type, contentType, encoding, defaultResponse, pretty);
            parser.pushObject(transformRule);
        });
        parser.addNodeEndlet(text -> {
            TransformRule transformRule = parser.popObject();
            ResponseRuleApplicable applicable = parser.peekObject();
            applicable.applyResponseRule(transformRule);
        });
        parser.addNodelet(actionNodeletAdder);
        parser.setXpath(xpath + "/transform/choose");
        parser.addNodelet(attrs -> {
            TransletRule transletRule = parser.peekObject(TransletRule.class);

            ChooseRuleMap chooseRuleMap = transletRule.touchChooseRuleMap();
            ChooseRule chooseRule = chooseRuleMap.newChooseRule();

            parser.pushObject(chooseRule);
        });
        parser.addNodelet(chooseWhenNodeletAdder);
        parser.addNodeEndlet(text -> {
            ChooseRule chooseRule = parser.popObject();
            TransformRule transformRule = parser.peekObject();
            chooseRule.join(transformRule);
        });
        parser.setXpath(xpath + "/transform/template");
        parser.addNodelet(attrs -> {
            String engine = attrs.get("engine");
            String name = attrs.get("name");
            String file = attrs.get("file");
            String resource = attrs.get("resource");
            String url = attrs.get("url");
            String style = attrs.get("style");
            String encoding = attrs.get("encoding");
            Boolean noCache = BooleanUtils.toNullableBooleanObject(attrs.get("noCache"));

            TemplateRule templateRule = TemplateRule.newInstanceForBuiltin(engine, name, file, resource, url,
                    null, style, encoding, noCache);
            parser.pushObject(templateRule);
        });
        parser.addNodeEndlet(text -> {
            TemplateRule templateRule = parser.popObject();
            TransformRule transformRule = parser.peekObject();

            TemplateRule.updateTemplateSource(templateRule, text);
            transformRule.setTemplateRule(templateRule);

            assistant.resolveBeanClass(templateRule);
        });
        parser.setXpath(xpath + "/transform/call");
        parser.addNodelet(attrs -> {
            String template = StringUtils.emptyToNull(attrs.get("template"));

            TransformRule transformRule = parser.peekObject();
            TransformRule.updateTemplateId(transformRule, template);
        });
        parser.setXpath(xpath + "/dispatch");
        parser.addNodelet(attrs -> {
            String name = attrs.get("name");
            String dispatcher = attrs.get("dispatcher");
            String contentType = attrs.get("contentType");
            String encoding = attrs.get("encoding");
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attrs.get("default"));

            DispatchRule dispatchRule = DispatchRule.newInstance(name, dispatcher, contentType, encoding, defaultResponse);
            parser.pushObject(dispatchRule);
        });
        parser.addNodeEndlet(text -> {
            DispatchRule dispatchRule = parser.popObject();
            ResponseRuleApplicable applicable = parser.peekObject();
            applicable.applyResponseRule(dispatchRule);
        });
        parser.addNodelet(actionNodeletAdder);
        parser.setXpath(xpath + "/dispatch/choose");
        parser.addNodelet(attrs -> {
            TransletRule transletRule = parser.peekObject(TransletRule.class);

            ChooseRuleMap chooseRuleMap = transletRule.touchChooseRuleMap();
            ChooseRule chooseRule = chooseRuleMap.newChooseRule();

            parser.pushObject(chooseRule);
        });
        parser.addNodelet(chooseWhenNodeletAdder);
        parser.addNodeEndlet(text -> {
            ChooseRule chooseRule = parser.popObject();
            DispatchRule dispatchRule = parser.peekObject();
            chooseRule.join(dispatchRule);
        });
        parser.setXpath(xpath + "/forward");
        parser.addNodelet(attrs -> {
            String contentType = attrs.get("contentType");
            String transletName = attrs.get("translet");
            String method = attrs.get("method");
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attrs.get("default"));

            transletName = assistant.applyTransletNamePattern(transletName);

            ForwardRule forwardRule = ForwardRule.newInstance(contentType, transletName, method, defaultResponse);
            parser.pushObject(forwardRule);
        });
        parser.addNodeEndlet(text -> {
            ForwardRule forwardRule = parser.popObject();
            ResponseRuleApplicable applicable = parser.peekObject();
            applicable.applyResponseRule(forwardRule);
        });
        parser.addNodelet(actionNodeletAdder);
        parser.setXpath(xpath + "/forward/choose");
        parser.addNodelet(attrs -> {
            TransletRule transletRule = parser.peekObject(TransletRule.class);

            ChooseRuleMap chooseRuleMap = transletRule.touchChooseRuleMap();
            ChooseRule chooseRule = chooseRuleMap.newChooseRule();

            parser.pushObject(chooseRule);
        });
        parser.addNodelet(chooseWhenNodeletAdder);
        parser.addNodeEndlet(text -> {
            ChooseRule chooseRule = parser.popObject();
            ForwardRule forwardRule = parser.peekObject();
            chooseRule.join(forwardRule);
        });
        parser.setXpath(xpath + "/forward/attributes");
        parser.addNodelet(attrs -> {
            ItemRuleMap irm = new ItemRuleMap();
            irm.setProfile(StringUtils.emptyToNull(attrs.get("profile")));
            parser.pushObject(irm);
        });
        parser.addNodelet(itemNodeletAdder);
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            ForwardRule forwardRule = parser.peekObject();
            irm = assistant.profiling(irm, forwardRule.getAttributeItemRuleMap());
            forwardRule.setAttributeItemRuleMap(irm);
        });
        parser.setXpath(xpath + "/redirect");
        parser.addNodelet(attrs -> {
            String contentType = attrs.get("contentType");
            String path = attrs.get("path");
            String encoding = attrs.get("encoding");
            Boolean excludeNullParameters = BooleanUtils.toNullableBooleanObject(attrs.get("excludeNullParameters"));
            Boolean excludeEmptyParameters = BooleanUtils.toNullableBooleanObject(attrs.get("excludeEmptyParameters"));
            Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attrs.get("default"));

            RedirectRule redirectRule = RedirectRule.newInstance(contentType, path, encoding, excludeNullParameters,
                    excludeEmptyParameters, defaultResponse);
            parser.pushObject(redirectRule);
        });
        parser.addNodeEndlet(text -> {
            RedirectRule redirectRule = parser.popObject();

            if (redirectRule.getPath() == null) {
                throw new IllegalArgumentException("The <redirect> element requires a 'path' attribute");
            }

            ResponseRuleApplicable applicable = parser.peekObject();
            applicable.applyResponseRule(redirectRule);

            assistant.resolveBeanClass(redirectRule.getPathTokens());
        });
        parser.addNodelet(actionNodeletAdder);
        parser.setXpath(xpath + "/redirect/choose");
        parser.addNodelet(attrs -> {
            TransletRule transletRule = parser.peekObject(TransletRule.class);

            ChooseRuleMap chooseRuleMap = transletRule.touchChooseRuleMap();
            ChooseRule chooseRule = chooseRuleMap.newChooseRule();

            parser.pushObject(chooseRule);
        });
        parser.addNodelet(chooseWhenNodeletAdder);
        parser.addNodeEndlet(text -> {
            ChooseRule chooseRule = parser.popObject();
            RedirectRule redirectRule = parser.peekObject();
            chooseRule.join(redirectRule);
        });
        parser.setXpath(xpath + "/redirect/parameters");
        parser.addNodelet(attrs -> {
            ItemRuleMap irm = new ItemRuleMap();
            irm.setProfile(StringUtils.emptyToNull(attrs.get("profile")));
            parser.pushObject(irm);
        });
        parser.addNodelet(itemNodeletAdder);
        parser.addNodeEndlet(text -> {
            ItemRuleMap irm = parser.popObject();
            RedirectRule redirectRule = parser.peekObject();
            irm = assistant.profiling(irm, redirectRule.getParameterItemRuleMap());
            redirectRule.setParameterItemRuleMap(irm);
        });
    }

}
