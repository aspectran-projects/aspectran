/*
 *  Copyright (c) 2008 Jeong Ju Ho, All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aspectran.core.context.builder.apon;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.process.ContentList;
import com.aspectran.core.activity.variable.token.Token;
import com.aspectran.core.context.builder.ContextBuilderAssistant;
import com.aspectran.core.context.builder.ImportHandler;
import com.aspectran.core.context.builder.Importable;
import com.aspectran.core.context.builder.apon.params.ActionParameters;
import com.aspectran.core.context.builder.apon.params.AdviceActionParameters;
import com.aspectran.core.context.builder.apon.params.AdviceParameters;
import com.aspectran.core.context.builder.apon.params.AspectParameters;
import com.aspectran.core.context.builder.apon.params.AspectranParameters;
import com.aspectran.core.context.builder.apon.params.BeanParameters;
import com.aspectran.core.context.builder.apon.params.ContentParameters;
import com.aspectran.core.context.builder.apon.params.ContentsParameters;
import com.aspectran.core.context.builder.apon.params.DispatchParameters;
import com.aspectran.core.context.builder.apon.params.ExceptionParameters;
import com.aspectran.core.context.builder.apon.params.ExceptionRaizedParameters;
import com.aspectran.core.context.builder.apon.params.ForwardParameters;
import com.aspectran.core.context.builder.apon.params.ImportParameters;
import com.aspectran.core.context.builder.apon.params.JobParameters;
import com.aspectran.core.context.builder.apon.params.JoinpointParameters;
import com.aspectran.core.context.builder.apon.params.RedirectParameters;
import com.aspectran.core.context.builder.apon.params.RequestParameters;
import com.aspectran.core.context.builder.apon.params.ResponseByContentTypeParameters;
import com.aspectran.core.context.builder.apon.params.ResponseParameters;
import com.aspectran.core.context.builder.apon.params.RootParameters;
import com.aspectran.core.context.builder.apon.params.TemplateParameters;
import com.aspectran.core.context.builder.apon.params.TransformParameters;
import com.aspectran.core.context.builder.apon.params.TransletParameters;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.AspectJobAdviceRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.BeanActionRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.DispatchResponseRule;
import com.aspectran.core.context.rule.EchoActionRule;
import com.aspectran.core.context.rule.ForwardResponseRule;
import com.aspectran.core.context.rule.IncludeActionRule;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.PointcutRule;
import com.aspectran.core.context.rule.RedirectResponseRule;
import com.aspectran.core.context.rule.RequestRule;
import com.aspectran.core.context.rule.ResponseByContentTypeRule;
import com.aspectran.core.context.rule.ResponseRule;
import com.aspectran.core.context.rule.SettingsAdviceRule;
import com.aspectran.core.context.rule.TemplateRule;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.ability.ActionRuleApplicable;
import com.aspectran.core.context.rule.ability.ResponseRuleApplicable;
import com.aspectran.core.context.rule.type.AspectAdviceType;
import com.aspectran.core.context.rule.type.DefaultSettingType;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.apon.Parameters;

/**
 * AspectranAponDisassembler.
 * 
 * <p>Created: 2015. 01. 27 오후 10:36:29</p>
 */
public class RootAponDisassembler {
	
	private final ContextBuilderAssistant assistant;
	
	public RootAponDisassembler(ContextBuilderAssistant assistant) {
		this.assistant = assistant;
	}
	
	public void disassembleRoot(Parameters rootParameters) throws Exception {
		Parameters aspectranParameters = rootParameters.getParameters(RootParameters.aspectran);
		disassembleAspectran(aspectranParameters);
	}
	
	public void disassembleAspectran(Parameters aspectranParameters) throws Exception {
		Parameters defaultSettingsParameters = aspectranParameters.getParameters(AspectranParameters.setting);
		if(defaultSettingsParameters != null)
			disassembleDefaultSettings(defaultSettingsParameters);

		Parameters typeAliasParameters = aspectranParameters.getParameters(AspectranParameters.typeAlias);
		if(typeAliasParameters != null)
			disassembleTypeAlias(typeAliasParameters);
		
		List<Parameters> aspectParametersList = aspectranParameters.getParametersList(AspectranParameters.aspects);
		if(aspectParametersList != null) {
			for(Parameters aspectParameters : aspectParametersList) {
				disassembleAspectRule(aspectParameters);
			}
		}

		List<Parameters> beanParametersList = aspectranParameters.getParametersList(AspectranParameters.beans);
		if(beanParametersList != null) {
			for(Parameters beanParameters : beanParametersList) {
				disassembleBeanRule(beanParameters);
			}
		}

		List<Parameters> transletParametersList = aspectranParameters.getParametersList(AspectranParameters.translets);
		if(transletParametersList != null) {
			for(Parameters transletParameters : transletParametersList) {
				disassembleTransletRule(transletParameters);
			}
		}
		
		List<Parameters> importParametersList = aspectranParameters.getParametersList(AspectranParameters.imports);
		if(importParametersList != null) {
			for(Parameters importParameters : importParametersList) {
				disassembleImport(importParameters);
			}
		}
	}
	
	public void disassembleImport(Parameters importParameters) throws Exception {
		String resource = importParameters.getString(ImportParameters.resource);
		String file = importParameters.getString(ImportParameters.file);
		String url = importParameters.getString(ImportParameters.url);
		String fileType = importParameters.getString(ImportParameters.fileType);
		
		Importable importable = Importable.newInstance(assistant, resource, file, url, fileType);

		ImportHandler importHandler = assistant.getImportHandler();
		if(importHandler != null)
			importHandler.pending(importable);
	}
	
	public void disassembleDefaultSettings(Parameters defaultSettingsParameters) throws ClassNotFoundException {
		if(defaultSettingsParameters == null)
			return;
		
		Iterator<String> iter = defaultSettingsParameters.getParameterNameSet().iterator();
		
		while(iter.hasNext()) {
			String name = iter.next();
			
			DefaultSettingType settingType = null;
			
			if(name != null) {
				settingType = DefaultSettingType.valueOf(name);
				
				if(settingType == null)
					throw new IllegalArgumentException("Unknown default setting name '" + name + "'");
			}
			
			assistant.putSetting(settingType, defaultSettingsParameters.getString(name));
		}
		
		assistant.applySettings();
	}
	
	public void disassembleTypeAlias(Parameters parameters) {
		if(parameters == null)
			return;
		
		Iterator<String> iter = parameters.getParameterNameSet().iterator();
		
		while(iter.hasNext()) {
			String alias = iter.next();
			assistant.addTypeAlias(alias, parameters.getString(alias));
		}
	}

	public void disassembleAspectRule(Parameters aspectParameters) {
		String id = aspectParameters.getString(AspectParameters.id);
		String useFor = aspectParameters.getString(AspectParameters.useFor);
		AspectRule aspectRule = AspectRule.newInstance(id, useFor);
	
		Parameters joinpointParameters = aspectParameters.getParameters(AspectParameters.jointpoint);
		String scope = joinpointParameters.getString(JoinpointParameters.scope);
		AspectRule.updateJoinpointScope(aspectRule, scope);
	
		Parameters pointcutParameters = joinpointParameters.getParameters(JoinpointParameters.pointcut);
		PointcutRule pointcutRule = PointcutRule.newInstance(aspectRule, null, pointcutParameters);
		aspectRule.setPointcutRule(pointcutRule);
	
		Parameters settingParameters = aspectParameters.getParameters(AspectParameters.setting);
		if(settingParameters != null) {
			SettingsAdviceRule settingsAdviceRule = SettingsAdviceRule.newInstance(aspectRule, settingParameters);
			aspectRule.setSettingsAdviceRule(settingsAdviceRule);
		}
		
		Parameters adviceParameters = aspectParameters.getParameters(AspectParameters.advice);
		String adviceBeanId = adviceParameters.getString(AdviceParameters.bean);
		if(adviceBeanId != null) {
			aspectRule.setAdviceBeanId(adviceBeanId);
			assistant.putBeanReference(adviceBeanId, aspectRule);
		}
		
		Parameters beforeAdviceParameters = adviceParameters.getParameters(AdviceParameters.beforeAdvice);
		if(beforeAdviceParameters != null) {
			Parameters actionParameters = beforeAdviceParameters.getParameters(AdviceActionParameters.action);
			AspectAdviceRule aspectAdviceRule = AspectAdviceRule.newInstance(aspectRule, AspectAdviceType.BEFORE);
			disassembleActionRule(actionParameters, aspectAdviceRule);
			aspectRule.addAspectAdviceRule(aspectAdviceRule);
		}
		
		Parameters afterAdviceParameters = adviceParameters.getParameters(AdviceParameters.afterAdvice);
		if(afterAdviceParameters != null) {
			Parameters actionParameters = afterAdviceParameters.getParameters(AdviceActionParameters.action);
			AspectAdviceRule aspectAdviceRule = AspectAdviceRule.newInstance(aspectRule, AspectAdviceType.AFTER);
			disassembleActionRule(actionParameters, aspectAdviceRule);
			aspectRule.addAspectAdviceRule(aspectAdviceRule);
		}
	
		Parameters aroundAdviceParameters = adviceParameters.getParameters(AdviceParameters.aroundAdvice);
		if(aroundAdviceParameters != null) {
			Parameters actionParameters = aroundAdviceParameters.getParameters(AdviceActionParameters.action);
			AspectAdviceRule aspectAdviceRule = AspectAdviceRule.newInstance(aspectRule, AspectAdviceType.AROUND);
			disassembleActionRule(actionParameters, aspectAdviceRule);
			aspectRule.addAspectAdviceRule(aspectAdviceRule);
		}
	
		Parameters finallyAdviceParameters = adviceParameters.getParameters(AdviceParameters.finallyAdvice);
		if(finallyAdviceParameters != null) {
			Parameters actionParameters = finallyAdviceParameters.getParameters(AdviceActionParameters.action);
			AspectAdviceRule aspectAdviceRule = AspectAdviceRule.newInstance(aspectRule, AspectAdviceType.AROUND);
			disassembleActionRule(actionParameters, aspectAdviceRule);
			aspectRule.addAspectAdviceRule(aspectAdviceRule);
		}
	
		Parameters exceptionRaizedParameters = adviceParameters.getParameters(AdviceParameters.exceptionRaized);
		if(exceptionRaizedParameters != null) {
			AspectAdviceRule aspectAdviceRule = AspectAdviceRule.newInstance(aspectRule, AspectAdviceType.EXCPETION_RAIZED);
	
			Parameters actionParameters = exceptionRaizedParameters.getParameters(ExceptionRaizedParameters.action);
			if(actionParameters != null) {
				disassembleActionRule(actionParameters, aspectAdviceRule);
			}
	
			List<Parameters> rrtrParametersList = exceptionRaizedParameters.getParametersList(ExceptionRaizedParameters.responseByContentTypes);
			if(rrtrParametersList != null && !rrtrParametersList.isEmpty()) {
				for(Parameters rrtrParameters : rrtrParametersList) {
					ResponseByContentTypeRule rrtr = disassembleResponseByContentTypeRule(rrtrParameters);
					aspectAdviceRule.addResponseByContentTypeRule(rrtr);
				}
			}
			
			aspectRule.addAspectAdviceRule(aspectAdviceRule);
		}
		
		List<Parameters> jobParametersList = adviceParameters.getParametersList(AdviceParameters.jobs);
		if(jobParametersList != null && !jobParametersList.isEmpty()) {
			for(Parameters jobParameters : jobParametersList) {
				String translet = jobParameters.getString(JobParameters.translet);
				Boolean disabled = jobParameters.getBoolean(JobParameters.disabled);
				
				translet = assistant.applyTransletNamePattern(translet);
				
				AspectJobAdviceRule ajar = AspectJobAdviceRule.newInstance(aspectRule, translet, disabled);
				aspectRule.addAspectJobAdviceRule(ajar);
			}
		}
		
		assistant.addAspectRule(aspectRule);
	}

	public void disassembleBeanRule(Parameters beanParameters) throws ClassNotFoundException, IOException, CloneNotSupportedException {
		String id = beanParameters.getString(BeanParameters.id);
		String className = assistant.resolveAliasType(beanParameters.getString(BeanParameters.className));
		String scope = beanParameters.getString(BeanParameters.scope);
		Boolean singleton = beanParameters.getBoolean(BeanParameters.singleton);
		String factoryMethod = beanParameters.getString(BeanParameters.factoryMethod);
		String initMethod = beanParameters.getString(BeanParameters.initMethod);
		String destroyMethod = beanParameters.getString(BeanParameters.destroyMethod);
		Boolean lazyInit = beanParameters.getBoolean(BeanParameters.lazyInit);
		Boolean important = beanParameters.getBoolean(BeanParameters.important);
		List<Parameters> constructorArgumentParametersList = beanParameters.getParametersList(BeanParameters.constructor);
		List<Parameters> propertyParametersList = beanParameters.getParametersList(BeanParameters.properties);
		
		BeanRule beanRule = BeanRule.newInstance(id, className, scope, singleton, factoryMethod, initMethod, destroyMethod, lazyInit, important);

		if(constructorArgumentParametersList != null && !constructorArgumentParametersList.isEmpty()) {
			ItemRuleMap constructorArgumentItemRuleMap = disassembleItemRuleMap(constructorArgumentParametersList);
			beanRule.setConstructorArgumentItemRuleMap(constructorArgumentItemRuleMap);
		}
		
		if(propertyParametersList != null && !propertyParametersList.isEmpty()) {
			ItemRuleMap propertyItemRuleMap = disassembleItemRuleMap(propertyParametersList);
			beanRule.setPropertyItemRuleMap(propertyItemRuleMap);
		}
		
		assistant.addBeanRule(beanRule);
	}

	public void disassembleTransletRule(Parameters transletParameters) throws CloneNotSupportedException {
		String name = transletParameters.getString(TransletParameters.name);
		TransletRule transletRule = TransletRule.newInstance(name);
		
		Parameters requestParamters = transletParameters.getParameters(TransletParameters.request);
		if(requestParamters != null) {
			RequestRule requestRule = disassembleRequestRule(requestParamters);
			transletRule.setRequestRule(requestRule);
		}
		
		Parameters contentsParameters = transletParameters.getParameters(TransletParameters.contents1);
		if(contentsParameters != null) {
			ContentList contentList = disassembleContentList(contentsParameters);
			transletRule.setContentList(contentList);
		}
		
		List<Parameters> contentParametersList = transletParameters.getParametersList(TransletParameters.contents2);
		if(contentParametersList != null && !contentParametersList.isEmpty()) {
			ContentList contentList = transletRule.touchContentList();
			for(Parameters contentParamters : contentParametersList) {
				ActionList actionList = disassembleActionList(contentParamters, contentList);
				contentList.addActionList(actionList);
			}
		}
		
		List<Parameters> responseParametersList = transletParameters.getParametersList(TransletParameters.responses);
		if(responseParametersList != null) {
			for(Parameters responseParamters : responseParametersList) {
				ResponseRule responseRule = disassembleResponseRule(responseParamters);
				transletRule.addResponseRule(responseRule);
			}
		}
		
		Parameters exceptionParameters = transletParameters.getParameters(TransletParameters.exception);
		if(exceptionParameters != null) {
			List<Parameters> rrtrParametersList = exceptionParameters.getParametersList(ExceptionParameters.responseByContentTypes);
			if(rrtrParametersList != null && !rrtrParametersList.isEmpty()) {
				for(Parameters rrtrParameters : rrtrParametersList) {
					ResponseByContentTypeRule rrtr = disassembleResponseByContentTypeRule(rrtrParameters);
					transletRule.addExceptionHandlingRule(rrtr);
				}
			}
		}
		
		List<Parameters> actionParametersList = transletParameters.getParametersList(TransletParameters.actions);
		if(actionParametersList != null) {
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, transletRule);
			}
		}
		
		Parameters transformParameters = transletParameters.getParameters(TransletParameters.transform);
		if(transformParameters != null) {
			TransformRule tr = disassembleTransformRule(transformParameters);
			transletRule.applyResponseRule(tr);
		}
		
		Parameters dispatchParameters = transletParameters.getParameters(TransletParameters.dispatch);
		if(dispatchParameters != null) {
			DispatchResponseRule drr = disassembleDispatchResponseRule(dispatchParameters);
			transletRule.applyResponseRule(drr);
		}

		Parameters redirectParameters = transletParameters.getParameters(TransletParameters.redirect);
		if(redirectParameters != null) {
			RedirectResponseRule rrr = disassembleRedirectResponseRule(redirectParameters);
			transletRule.applyResponseRule(rrr);
		}
		
		Parameters forwardParameters = transletParameters.getParameters(TransletParameters.forward);
		if(forwardParameters != null) {
			ForwardResponseRule frr = disassembleForwardResponseRule(forwardParameters);
			transletRule.applyResponseRule(frr);
		}

		assistant.addTransletRule(transletRule);
	}
	
	public RequestRule disassembleRequestRule(Parameters requestParameters) {
		String method = requestParameters.getString(RequestParameters.method);
		String characterEncoding = requestParameters.getString(RequestParameters.characterEncoding);
		List<Parameters> attributeParametersList = requestParameters.getParametersList(RequestParameters.attributes);
		
		RequestRule requestRule = RequestRule.newInstance(method, characterEncoding);
	
		ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(attributeParametersList);
		if(attributeItemRuleMap != null) {
			requestRule.setAttributeItemRuleMap(attributeItemRuleMap);
		}
	
		return requestRule;
	}

	public ResponseRule disassembleResponseRule(Parameters responseParameters) {
		String name = responseParameters.getString(ResponseParameters.name);
		String characterEncoding = responseParameters.getString(ResponseParameters.characterEncoding);

		ResponseRule responseRule = ResponseRule.newInstance(name, characterEncoding);
		
		Parameters transformParameters = responseParameters.getParameters(ResponseParameters.transform);
		if(transformParameters != null) {
			responseRule.applyResponseRule(disassembleTransformRule(transformParameters));
		}
		
		Parameters dispatchParameters = responseParameters.getParameters(ResponseParameters.dispatch);
		if(dispatchParameters != null) {
			responseRule.applyResponseRule(disassembleDispatchResponseRule(dispatchParameters));
		}

		Parameters redirectParameters = responseParameters.getParameters(ResponseParameters.redirect);
		if(redirectParameters != null) {
			responseRule.applyResponseRule(disassembleRedirectResponseRule(redirectParameters));
		}
		
		Parameters forwardParameters = responseParameters.getParameters(ResponseParameters.forward);
		if(forwardParameters != null) {
			responseRule.applyResponseRule(disassembleForwardResponseRule(forwardParameters));
		}
		
		return responseRule;
	}
	
	public ContentList disassembleContentList(Parameters contentsParameters) {
		String name = contentsParameters.getString(ContentsParameters.name);
		Boolean omittable = contentsParameters.getBoolean(ContentsParameters.omittable);
		List<Parameters> contentParametersList = contentsParameters.getParametersList(ContentsParameters.contents);
		
		ContentList contentList = ContentList.newInstance(name, omittable);
		
		if(contentParametersList != null) {
			for(Parameters contentParamters : contentParametersList) {
				ActionList actionList = disassembleActionList(contentParamters, contentList);
				contentList.addActionList(actionList);
			}
		}
		
		return contentList;
	}
	
	public ActionList disassembleActionList(Parameters contentParameters, ContentList contentList) {
		String id = contentParameters.getString(ContentParameters.id);
		String name = contentParameters.getString(ContentParameters.name);
		Boolean omittable = contentParameters.getBoolean(ContentParameters.omittable);
		Boolean hidden = contentParameters.getBoolean(ContentParameters.hidden);
		List<Parameters> actionParametersList = contentParameters.getParametersList(ContentParameters.actions);
		
		if(!assistant.isNullableContentId() && StringUtils.isEmpty(id))
			throw new IllegalArgumentException("The <content> element requires a id attribute.");
		
		ActionList actionList = ActionList.newInstance(id, name, omittable, hidden, contentList);

		if(actionParametersList != null && !actionParametersList.isEmpty()) {
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
		}
		
		return actionList;
	}
	
	public void disassembleActionRule(Parameters actionParameters, ActionRuleApplicable actionRuleApplicable) {
		String id = actionParameters.getString(ActionParameters.id);
		String beanId = actionParameters.getString(ActionParameters.beanId);
		String methodName = actionParameters.getString(ActionParameters.methodName);
		List<Parameters> argumentParametersList = actionParameters.getParametersList(ActionParameters.arguments);
		List<Parameters> propertyParametersList = actionParameters.getParametersList(ActionParameters.properties);
		String include = actionParameters.getString(ActionParameters.include);
		List<Parameters> echoParametersList = actionParameters.getParametersList(ActionParameters.echo);
		Boolean hidden = actionParameters.getBoolean(ActionParameters.hidden);
		
		if(!assistant.isNullableActionId() && StringUtils.isEmpty(id))
			throw new IllegalArgumentException("The <echo>, <action>, <include> element requires a id attribute.");
		
		if(beanId != null && methodName != null) {
			BeanActionRule beanActionRule = BeanActionRule.newInstance(id, beanId, methodName, hidden);
			ItemRuleMap argumentItemRuleMap = disassembleItemRuleMap(argumentParametersList);
			if(argumentItemRuleMap != null) {
				beanActionRule.setArgumentItemRuleMap(argumentItemRuleMap);
			}
			ItemRuleMap propertyItemRuleMap = disassembleItemRuleMap(propertyParametersList);
			if(propertyItemRuleMap != null) {
				beanActionRule.setPropertyItemRuleMap(propertyItemRuleMap);
			}
			actionRuleApplicable.applyActionRule(beanActionRule);
			assistant.putBeanReference(beanId, beanActionRule);
		} else if(echoParametersList != null) {
			EchoActionRule echoActionRule = EchoActionRule.newInstance(id, hidden);
			ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(echoParametersList);
			echoActionRule.setAttributeItemRuleMap(attributeItemRuleMap);
			actionRuleApplicable.applyActionRule(echoActionRule);
		} else if(include != null) {
			IncludeActionRule includeActionRule = IncludeActionRule.newInstance(id, include, hidden);
			ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(echoParametersList);
			includeActionRule.setAttributeItemRuleMap(attributeItemRuleMap);
			actionRuleApplicable.applyActionRule(includeActionRule);
		}
	}

	public ResponseByContentTypeRule disassembleResponseByContentTypeRule(Parameters responseByContentTypeParameters) {
		ResponseByContentTypeRule rbctr = new ResponseByContentTypeRule();
		
		String exceptionType = responseByContentTypeParameters.getString(ResponseByContentTypeParameters.exceptionType);
		rbctr.setExceptionType(exceptionType);
		
		List<Parameters> transformParametersList = responseByContentTypeParameters.getParametersList(ResponseByContentTypeParameters.transforms);
		if(transformParametersList != null && !transformParametersList.isEmpty()) {
			disassembleTransformRule(transformParametersList, rbctr);
		}
		
		List<Parameters> dispatchParametersList = responseByContentTypeParameters.getParametersList(ResponseByContentTypeParameters.dispatchs);
		if(dispatchParametersList != null && !dispatchParametersList.isEmpty()) {
			disassembleDispatchResponseRule(dispatchParametersList, rbctr);
		}

		List<Parameters> redirectParametersList = responseByContentTypeParameters.getParametersList(ResponseByContentTypeParameters.redirects);
		if(redirectParametersList != null && !redirectParametersList.isEmpty()) {
			disassembleRedirectResponseRule(redirectParametersList, rbctr);
		}
		
		List<Parameters> forwardParametersList = responseByContentTypeParameters.getParametersList(ResponseByContentTypeParameters.forwards);
		if(forwardParametersList != null && !forwardParametersList.isEmpty()) {
			disassembleForwardResponseRule(forwardParametersList, rbctr);
		}
		
		return rbctr;
	}
	
	public void disassembleTransformRule(List<Parameters> transformParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters transformParameters : transformParametersList) {
			TransformRule tr = disassembleTransformRule(transformParameters);
			responseRuleApplicable.applyResponseRule(tr);
		}
	}
	
	public TransformRule disassembleTransformRule(Parameters transformParameters) {
		String transformType = transformParameters.getString(TransformParameters.type);
		String contentType = transformParameters.getString(TransformParameters.contentType);
		String characterEncoding = transformParameters.getString(TransformParameters.characterEncoding);
		Parameters templateParameters = transformParameters.getParameters(TransformParameters.template);
		List<Parameters> actionParametersList = transformParameters.getParametersList(TransformParameters.actions);
		Boolean defaultResponse = transformParameters.getBoolean(TransformParameters.defaultResponse);
		Boolean pretty = transformParameters.getBoolean(TransformParameters.pretty);
		
		TransformRule tr = TransformRule.newInstance(transformType, contentType, characterEncoding, defaultResponse, pretty);
		
		if(actionParametersList != null && !actionParametersList.isEmpty()) {
			ActionList actionList = new ActionList();
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
			tr.setActionList(actionList);
		}
		
		if(templateParameters != null) {
			String file = templateParameters.getString(TemplateParameters.file);
			String resource = templateParameters.getString(TemplateParameters.resource);
			String url = templateParameters.getString(TemplateParameters.url);
			String content = templateParameters.getString(TemplateParameters.content);
			String encoding = templateParameters.getString(TemplateParameters.encoding);
			Boolean noCache = templateParameters.getBoolean(TemplateParameters.noCache);
			System.out.println(file);
			System.out.println(resource);
			System.out.println(content);
			System.out.println(encoding);
			System.out.println(noCache);
			System.out.println(templateParameters);
			System.out.println(templateParameters);
			System.out.println(templateParameters);
			TemplateRule templateRule = TemplateRule.newInstance(file, resource, url, content, encoding, noCache);
			tr.setTemplateRule(templateRule);
		}
		
		return tr;
	}

	public void disassembleDispatchResponseRule(List<Parameters> dispatchParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters dispatchParameters : dispatchParametersList) {
			DispatchResponseRule drr = disassembleDispatchResponseRule(dispatchParameters);
			responseRuleApplicable.applyResponseRule(drr);
		}
	}
	
	public DispatchResponseRule disassembleDispatchResponseRule(Parameters dispatchParameters) {
		String contentType = dispatchParameters.getString(DispatchParameters.contentType);
		String characterEncoding = dispatchParameters.getString(DispatchParameters.characterEncoding);
		Parameters templateParameters = dispatchParameters.getParameters(DispatchParameters.template);
		List<Parameters> actionParametersList = dispatchParameters.getParametersList(DispatchParameters.actions);
		Boolean defaultResponse = dispatchParameters.getBoolean(DispatchParameters.defaultResponse);
		
		DispatchResponseRule drr = DispatchResponseRule.newInstance(contentType, characterEncoding, defaultResponse);
		
		if(actionParametersList != null && !actionParametersList.isEmpty()) {
			ActionList actionList = new ActionList();
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
			drr.setActionList(actionList);
		}
	
		if(templateParameters != null) {
			String file = templateParameters.getString(TemplateParameters.file);
			String encoding = templateParameters.getString(TemplateParameters.encoding);
			Boolean noCache = templateParameters.getBoolean(TemplateParameters.noCache);
			TemplateRule templateRule = TemplateRule.newInstance(file, null, null, null, encoding, noCache);
			drr.setTemplateRule(templateRule);
		}
		
		return drr;
	}

	public void disassembleRedirectResponseRule(List<Parameters> redirectParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters redirectParameters : redirectParametersList) {
			RedirectResponseRule rrr = disassembleRedirectResponseRule(redirectParameters);
			responseRuleApplicable.applyResponseRule(rrr);
		}
	}
	
	public RedirectResponseRule disassembleRedirectResponseRule(Parameters redirectParameters) {
		String contentType = redirectParameters.getString(RedirectParameters.contentType);
		String translet = redirectParameters.getString(RedirectParameters.translet);
		String url = redirectParameters.getString(RedirectParameters.url);
		List<Parameters> parameterParametersList = redirectParameters.getParametersList(RedirectParameters.parameters);
		Boolean excludeNullParameter = redirectParameters.getBoolean(RedirectParameters.excludeNullParameter);
		List<Parameters> actionParametersList = redirectParameters.getParametersList(RedirectParameters.actions);
		Boolean defaultResponse = redirectParameters.getBoolean(RedirectParameters.defaultResponse);
		
		RedirectResponseRule rrr = RedirectResponseRule.newInstance(contentType, translet, url, excludeNullParameter, defaultResponse);
		
		ItemRuleMap parameterItemRuleMap = disassembleItemRuleMap(parameterParametersList);
		if(parameterItemRuleMap != null) {
			rrr.setParameterItemRuleMap(parameterItemRuleMap);
		}
		
		if(actionParametersList != null && !actionParametersList.isEmpty()) {
			ActionList actionList = new ActionList();
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
			rrr.setActionList(actionList);
		}
		
		return rrr;
	}

	public void disassembleForwardResponseRule(List<Parameters> forwardParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters forwardParameters : forwardParametersList) {
			ForwardResponseRule frr = disassembleForwardResponseRule(forwardParameters);
			responseRuleApplicable.applyResponseRule(frr);
		}
	}

	public ForwardResponseRule disassembleForwardResponseRule(Parameters forwardParameters) {
		String contentType = forwardParameters.getString(ForwardParameters.contentType);
		String translet = forwardParameters.getString(ForwardParameters.translet);
		List<Parameters> attributeParametersList = forwardParameters.getParametersList(ForwardParameters.attributes);
		List<Parameters> actionParametersList = forwardParameters.getParametersList(ForwardParameters.actions);
		Boolean defaultResponse = forwardParameters.getBoolean(ForwardParameters.defaultResponse);
		
		translet = assistant.applyTransletNamePattern(translet);
		
		ForwardResponseRule rrr = ForwardResponseRule.newInstance(contentType, translet, defaultResponse);
		
		ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(attributeParametersList);
		if(attributeItemRuleMap != null) {
			rrr.setAttributeItemRuleMap(attributeItemRuleMap);
		}
		
		if(actionParametersList != null && !actionParametersList.isEmpty()) {
			ActionList actionList = new ActionList();
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
			rrr.setActionList(actionList);
		}
		
		return rrr;
	}
	
	public ItemRuleMap disassembleItemRuleMap(List<Parameters> itemParametersList) {
		ItemRuleMap itemRuleMap = ItemRule.toItemRuleMap(itemParametersList);
		
		if(itemRuleMap != null) {
			for(ItemRule itemRule : itemRuleMap) {
				Iterator<Token[]> iter = ItemRule.tokenIterator(itemRule);
				
				if(iter != null) {
					while(iter.hasNext()) {
						for(Token token : iter.next()) {
							if(token.getType() == TokenType.REFERENCE_BEAN) {
								assistant.putBeanReference(token.getName(), itemRule);
							}
						}
					}
				}
			}
		}
		
		return itemRuleMap;
	}
	
}
