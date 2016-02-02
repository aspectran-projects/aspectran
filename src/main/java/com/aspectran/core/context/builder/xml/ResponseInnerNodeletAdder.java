/**
 * Copyright 2008-2016 Juho Jeong
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
package com.aspectran.core.context.builder.xml;

import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.context.builder.ContextBuilderAssistant;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.rule.DispatchResponseRule;
import com.aspectran.core.context.rule.ForwardResponseRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.RedirectResponseRule;
import com.aspectran.core.context.rule.TemplateRule;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.context.rule.ability.ResponseRuleApplicable;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.xml.Nodelet;
import com.aspectran.core.util.xml.NodeletAdder;
import com.aspectran.core.util.xml.NodeletParser;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * The Class ResponseRuleNodeletAdder.
 * 
 * <p>Created: 2008. 06. 14 AM 6:56:29</p>
 */
public class ResponseInnerNodeletAdder implements NodeletAdder {
	
	protected ContextBuilderAssistant assistant;
	
	/**
	 * Instantiates a new ResponseInnerNodeletAdder.
	 *
	 * @param assistant the assistant for Context Builder
	 */
	public ResponseInnerNodeletAdder(ContextBuilderAssistant assistant) {
		this.assistant = assistant;
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.util.xml.NodeletAdder#process(java.lang.String, com.aspectran.core.util.xml.NodeletParser)
	 */
	public void process(String xpath, NodeletParser parser) {
		parser.addNodelet(xpath, "/transform", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				String type = attributes.get("type");
				String contentType = attributes.get("contentType");
				String templateId = attributes.get("template");
				String characterEncoding = attributes.get("characterEncoding");
				Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));
				Boolean pretty = BooleanUtils.toNullableBooleanObject(attributes.get("pretty"));

				TransformRule tr = TransformRule.newInstance(type, contentType, templateId, characterEncoding, defaultResponse, pretty);
				assistant.pushObject(tr);
				
				ActionList actionList = new ActionList();
				assistant.pushObject(actionList);
			}
		});
		parser.addNodelet(xpath, "/transform", new ActionRuleNodeletAdder(assistant));
		parser.addNodelet(xpath, "/transform/template", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				String engine = attributes.get("engine");
				String name = attributes.get("name");
				String file = attributes.get("file");
				String resource = attributes.get("resource");
				String url = attributes.get("url");
				String encoding = attributes.get("encoding");
				Boolean noCache = BooleanUtils.toNullableBooleanObject(attributes.get("noCache"));
				
				TemplateRule templateRule = TemplateRule.newInstanceForBuiltin(engine, name, file, resource, url, text, encoding, noCache);

				TransformRule transformRule = assistant.peekObject(1);
				transformRule.setTemplateRule(templateRule);
				
				if(templateRule.getContentTokens() != null) {
					for(Token token : templateRule.getContentTokens()) {
						if(token.getType() == TokenType.REFERENCE_BEAN) {
							assistant.putBeanReference(token.getName(), transformRule);
						}
					}
				}
			}
		});
		parser.addNodelet(xpath, "/transform/end()", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ActionList actionList = assistant.popObject();
				TransformRule tr = assistant.popObject();
				
				if(!actionList.isEmpty())
					tr.setActionList(actionList);
				
				ResponseRuleApplicable applicable = assistant.peekObject();
				applicable.applyResponseRule(tr);
			}
		});
		parser.addNodelet(xpath, "/dispatch", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				String name = attributes.get("name");
				String contentType = attributes.get("contentType");
				String characterEncoding = attributes.get("characterEncoding");
				Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));

				DispatchResponseRule drr = DispatchResponseRule.newInstance(name, contentType, characterEncoding, defaultResponse);
				assistant.pushObject(drr);

				ActionList actionList = new ActionList();
				assistant.pushObject(actionList);
			}
		});
		parser.addNodelet(xpath, "/dispatch", new ActionRuleNodeletAdder(assistant));
		parser.addNodelet(xpath, "/dispatch/end()", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ActionList actionList = assistant.popObject();
				DispatchResponseRule drr = assistant.popObject();
				
				if(!actionList.isEmpty())
					drr.setActionList(actionList);
				
				ResponseRuleApplicable applicable = assistant.peekObject();
				applicable.applyResponseRule(drr);
			}
		});
		parser.addNodelet(xpath, "/redirect", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				String contentType = attributes.get("contentType");
				String target = attributes.get("target");
				Boolean excludeNullParameters = BooleanUtils.toNullableBooleanObject(attributes.get("excludeNullParameters"));
				Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));
				
				RedirectResponseRule rrr = RedirectResponseRule.newInstance(contentType, target, excludeNullParameters, defaultResponse);
				assistant.pushObject(rrr);
				
				ActionList actionList = new ActionList();
				assistant.pushObject(actionList);
			}
		});
		parser.addNodelet(xpath, "/redirect", new ActionRuleNodeletAdder(assistant));
		parser.addNodelet(xpath, "/redirect/parameter", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ItemRuleMap irm = new ItemRuleMap();
				assistant.pushObject(irm);
			}
		});
		parser.addNodelet(xpath, "/redirect/parameter", new ItemNodeletAdder(assistant));
		parser.addNodelet(xpath, "/redirect/parameter/end()", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ItemRuleMap irm = assistant.popObject();
				
				if(!irm.isEmpty()) {
					RedirectResponseRule rrr = assistant.peekObject(1);
					rrr.setParameterItemRuleMap(irm);
				}
			}
		});
		parser.addNodelet(xpath, "/redirect/end()", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ActionList actionList = assistant.popObject();
				RedirectResponseRule rrr = assistant.popObject();
				
				if(rrr.getTarget() == null)
					throw new IllegalArgumentException("The <redirect> element requires a target attribute.");

				if(!actionList.isEmpty())
					rrr.setActionList(actionList);
				
				ResponseRuleApplicable applicable = assistant.peekObject();
				applicable.applyResponseRule(rrr);
				
				if(rrr.getTargetTokens() != null) {
					for(Token token : rrr.getTargetTokens()) {
						if(token.getType() == TokenType.REFERENCE_BEAN) {
							assistant.putBeanReference(token.getName(), rrr);
						}
					}
				}
			}
		});
		parser.addNodelet(xpath, "/forward", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				String contentType = attributes.get("contentType");
				String transletName = attributes.get("translet");
				Boolean defaultResponse = BooleanUtils.toNullableBooleanObject(attributes.get("defaultResponse"));
				
				transletName = assistant.applyTransletNamePattern(transletName);
				
				ForwardResponseRule frr = ForwardResponseRule.newInstance(contentType, transletName, defaultResponse);
				assistant.pushObject(frr);

				ActionList actionList = new ActionList();
				assistant.pushObject(actionList);
			}
		});
		parser.addNodelet(xpath, "/forward", new ActionRuleNodeletAdder(assistant));
		parser.addNodelet(xpath, "/forward/parameter", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ItemRuleMap irm = new ItemRuleMap();
				assistant.pushObject(irm);
			}
		});
		parser.addNodelet(xpath, "/forward/parameter", new ItemNodeletAdder(assistant));
		parser.addNodelet(xpath, "/forward/parameter/end()", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ItemRuleMap irm = assistant.popObject();
				
				if(irm.size() > 0) {
					ForwardResponseRule frr = assistant.peekObject(1);
					frr.setAttributeItemRuleMap(irm);
				}
			}
		});
		parser.addNodelet(xpath, "/forward/end()", new Nodelet() {
			public void process(Node node, Map<String, String> attributes, String text) throws Exception {
				ActionList actionList = assistant.popObject();
				ForwardResponseRule frr = assistant.popObject();
				
				if(!actionList.isEmpty())
					frr.setActionList(actionList);
				
				ResponseRuleApplicable applicable = assistant.peekObject();
				applicable.applyResponseRule(frr);
			}
		});
	}

}