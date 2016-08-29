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
package com.aspectran.core.context.builder.apon;

import java.util.List;

import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.process.ContentList;
import com.aspectran.core.context.builder.apon.params.ActionParameters;
import com.aspectran.core.context.builder.apon.params.AdviceActionParameters;
import com.aspectran.core.context.builder.apon.params.AdviceParameters;
import com.aspectran.core.context.builder.apon.params.AspectParameters;
import com.aspectran.core.context.builder.apon.params.AspectranParameters;
import com.aspectran.core.context.builder.apon.params.BeanParameters;
import com.aspectran.core.context.builder.apon.params.ConstructorParameters;
import com.aspectran.core.context.builder.apon.params.ContentParameters;
import com.aspectran.core.context.builder.apon.params.ContentsParameters;
import com.aspectran.core.context.builder.apon.params.DispatchParameters;
import com.aspectran.core.context.builder.apon.params.EnvironmentParameters;
import com.aspectran.core.context.builder.apon.params.ExceptionParameters;
import com.aspectran.core.context.builder.apon.params.ForwardParameters;
import com.aspectran.core.context.builder.apon.params.ImportParameters;
import com.aspectran.core.context.builder.apon.params.ItemHolderParameters;
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
import com.aspectran.core.context.builder.assistant.ContextBuilderAssistant;
import com.aspectran.core.context.builder.importer.ImportHandler;
import com.aspectran.core.context.builder.importer.Importer;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.JobRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.BeanActionRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.DispatchResponseRule;
import com.aspectran.core.context.rule.EchoActionRule;
import com.aspectran.core.context.rule.EnvironmentRule;
import com.aspectran.core.context.rule.ExceptionRule;
import com.aspectran.core.context.rule.ForwardResponseRule;
import com.aspectran.core.context.rule.IncludeActionRule;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.JoinpointRule;
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
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.apon.Parameters;
import com.google.common.util.concurrent.CycleDetectingLockFactory;

/**
 * The Class RootAponDisassembler.
 * 
 * <p>Created: 2015. 01. 27 PM 10:36:29</p>
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
		String description = aspectranParameters.getString(AspectranParameters.description);
		if(description != null)
			assistant.getAssistantLocal().setDescription(description);
		
		Parameters defaultSettingsParameters = aspectranParameters.getParameters(AspectranParameters.settings);
		if(defaultSettingsParameters != null)
			disassembleDefaultSettings(defaultSettingsParameters);

		List<Parameters> environmentParametersList = aspectranParameters.getParametersList(AspectranParameters.environments);
		if(environmentParametersList != null) {
			for(Parameters environmentParameters : environmentParametersList) {
				disassembleEnvironmentRule(environmentParameters);
			}
		}
		
		Parameters typeAliasParameters = aspectranParameters.getParameters(AspectranParameters.typeAlias);
		if(typeAliasParameters != null) {
			disassembleTypeAlias(typeAliasParameters);
		}
		
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

		List<Parameters> scheduleParametersList = aspectranParameters.getParametersList(ScheduleParameters.schedules);
		if(aspectParametersList != null) {
			for(Parameters aspectParameters : aspectParametersList) {
				disassembleAspectRule(aspectParameters);
			}
		}

		List<Parameters> transletParametersList = aspectranParameters.getParametersList(AspectranParameters.translets);
		if(transletParametersList != null) {
			for(Parameters transletParameters : transletParametersList) {
				disassembleTransletRule(transletParameters);
			}
		}
		
		List<Parameters> templateParametersList = aspectranParameters.getParametersList(AspectranParameters.templates);
		if(templateParametersList != null) {
			for(Parameters templateParameters : templateParametersList) {
				disassembleTemplateRule(templateParameters);
			}
		}

		List<Parameters> importParametersList = aspectranParameters.getParametersList(AspectranParameters.imports);
		if(importParametersList != null) {
			for(Parameters importParameters : importParametersList) {
				disassembleImport(importParameters);
			}
		}
	}
	
	private void disassembleImport(Parameters importParameters) throws Exception {
		String file = importParameters.getString(ImportParameters.file);
		String resource = importParameters.getString(ImportParameters.resource);
		String url = importParameters.getString(ImportParameters.url);
		String fileType = importParameters.getString(ImportParameters.fileType);
		String profile = importParameters.getString(ImportParameters.profile);

		ImportHandler importHandler = assistant.getImportHandler();
		if(importHandler != null) {
			Importer importer = assistant.newImporter(file, resource, url, fileType, profile);
			if(importer == null) {
				throw new IllegalArgumentException("The 'import' element requires either a 'file' or a 'resource' or a 'url' attribute.");
			}
			importHandler.pending(importer);
		}
	}
	
	private void disassembleDefaultSettings(Parameters defaultSettingsParameters) throws ClassNotFoundException {
		if(defaultSettingsParameters == null)
			return;

		for(String name : defaultSettingsParameters.getParameterNameSet()) {
			DefaultSettingType settingType = null;
			if(name != null) {
				settingType = DefaultSettingType.resolve(name);
				if(settingType == null)
					throw new IllegalArgumentException("Unknown default setting name '" + name + "'.");
			}

			assistant.putSetting(settingType, defaultSettingsParameters.getString(name));
		}
		
		assistant.applySettings();
	}
	
	private void disassembleEnvironmentRule(Parameters environmentParameters) {
		if(environmentParameters != null) {
			String profile = StringUtils.emptyToNull(environmentParameters.getString(EnvironmentParameters.profile));
			ItemHolderParameters propertyItemHolderParameters = environmentParameters.getParameters(EnvironmentParameters.properties);
			ItemRuleMap propertyItemRuleMap = null;
			if(propertyItemHolderParameters != null)
				propertyItemRuleMap = disassembleItemRuleMap(propertyItemHolderParameters);

			EnvironmentRule environmentRule = EnvironmentRule.newInstance(profile, propertyItemRuleMap);
			assistant.addEnvironmentRule(environmentRule);
		}
	}
	
	private void disassembleTypeAlias(Parameters parameters) {
		if(parameters != null) {
			for(String alias : parameters.getParameterNameSet()) {
				assistant.addTypeAlias(alias, parameters.getString(alias));
			}
		}
	}

	private void disassembleAspectRule(Parameters aspectParameters) {
		String description = aspectParameters.getString(AspectParameters.description);
		String id = StringUtils.emptyToNull(aspectParameters.getString(AspectParameters.id));
		String order = aspectParameters.getString(AspectParameters.order);
		Boolean isolated = aspectParameters.getBoolean(AspectParameters.isolated);

		if(id == null) {
			throw new IllegalArgumentException("The 'aspect' element requires an 'id' attribute.");
		}

		AspectRule aspectRule = AspectRule.newInstance(id, order, isolated);
		if(description != null) {
			aspectRule.setDescription(description);
		}
	
		Parameters joinpointParameters = aspectParameters.getParameters(AspectParameters.jointpoint);
		if(joinpointParameters != null) {
			JoinpointRule joinpointRule = JoinpointRule.newInstance(joinpointParameters);
			aspectRule.setJoinpointRule(joinpointRule);
		}

		Parameters settingsParameters = aspectParameters.getParameters(AspectParameters.settings);
		if(settingsParameters != null) {
			SettingsAdviceRule settingsAdviceRule = SettingsAdviceRule.newInstance(aspectRule, settingsParameters);
			aspectRule.setSettingsAdviceRule(settingsAdviceRule);
		}
		
		Parameters adviceParameters = aspectParameters.getParameters(AspectParameters.advice);
		if(adviceParameters != null) {
			String adviceBeanId = adviceParameters.getString(AdviceParameters.bean);
			if(!StringUtils.isEmpty(adviceBeanId)) {
				aspectRule.setAdviceBeanId(adviceBeanId);
				assistant.resolveBeanClass(adviceBeanId, aspectRule);
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
		
			List<Parameters> jobParametersList = adviceParameters.getParametersList(AdviceParameters.jobs);
			if(jobParametersList != null) {
				for(Parameters jobParameters : jobParametersList) {
					String translet = StringUtils.emptyToNull(jobParameters.getString(JobParameters.translet));
					Boolean disabled = jobParameters.getBoolean(JobParameters.disabled);

					if(translet == null)
						throw new IllegalArgumentException("The 'job' element requires a 'translet' attribute.");

					translet = assistant.applyTransletNamePattern(translet);

					JobRule ajar = JobRule.newInstance(aspectRule, translet, disabled);
					aspectRule.addAspectJobAdviceRule(ajar);
				}
			}
		}
		
		Parameters exceptionParameters = aspectParameters.getParameters(AspectParameters.exception);
		if(exceptionParameters != null) {
			ExceptionRule exceptionRule = ExceptionRule.newInstance(aspectRule);
			exceptionRule.setDescription(exceptionParameters.getString(ExceptionParameters.description));

			Parameters actionParameters = exceptionParameters.getParameters(ExceptionParameters.action);
			if(actionParameters != null) {
				disassembleActionRule(actionParameters, exceptionRule);
			}
	
			List<Parameters> rbctParametersList = exceptionParameters.getParametersList(ExceptionParameters.responseByContentTypes);
			if(rbctParametersList != null) {
				for(Parameters rrtrParameters : rbctParametersList) {
					ResponseByContentTypeRule rbctr = disassembleResponseByContentTypeRule(rrtrParameters);
					exceptionRule.putResponseByContentTypeRule(rbctr);
				}
			}
			
			aspectRule.setExceptionRule(exceptionRule);
		}

		assistant.addAspectRule(aspectRule);
	}

	private void disassembleBeanRule(Parameters beanParameters) throws ClassNotFoundException {
		String description = beanParameters.getString(BeanParameters.description);
		String id = StringUtils.emptyToNull(beanParameters.getString(BeanParameters.id));
		String className = StringUtils.emptyToNull(assistant.resolveAliasType(beanParameters.getString(BeanParameters.className)));
		String scan = beanParameters.getString(BeanParameters.scan);
		String mask = beanParameters.getString(BeanParameters.mask);
		String scope = beanParameters.getString(BeanParameters.scope);
		Boolean singleton = beanParameters.getBoolean(BeanParameters.singleton);
		String offerBean = StringUtils.emptyToNull(beanParameters.getString(BeanParameters.offerBean));
		String offerMethod = StringUtils.emptyToNull(beanParameters.getString(BeanParameters.offerMethod));
		String initMethod = StringUtils.emptyToNull(beanParameters.getString(BeanParameters.initMethod));
		String factoryMethod = StringUtils.emptyToNull(beanParameters.getString(BeanParameters.factoryMethod));
		String destroyMethod = StringUtils.emptyToNull(beanParameters.getString(BeanParameters.destroyMethod));
		Boolean lazyInit = beanParameters.getBoolean(BeanParameters.lazyInit);
		Boolean important = beanParameters.getBoolean(BeanParameters.important);
		ConstructorParameters constructorParameters = beanParameters.getParameters(BeanParameters.constructor);
		ItemHolderParameters propertyItemHolderParameters = beanParameters.getParameters(BeanParameters.properties);
		Parameters filterParameters = beanParameters.getParameters(BeanParameters.filter);
		
		BeanRule beanRule;

		if(className == null && scan == null && offerBean != null) {
			if(offerMethod == null)
				throw new IllegalArgumentException("The 'bean' element requires an 'offerMethod' attribute.");

			beanRule = BeanRule.newOfferedBeanInstance(id, offerBean, offerMethod, initMethod, destroyMethod, factoryMethod, scope, singleton, lazyInit, important);

			assistant.resolveBeanClass(offerBean, beanRule);
		} else {
			if(className == null && scan == null)
				throw new IllegalArgumentException("The 'bean' element requires a 'class' attribute.");

			beanRule = BeanRule.newInstance(id, className, scan, mask, initMethod, destroyMethod, factoryMethod, scope, singleton, lazyInit, important);
		}

		if(description != null)
			beanRule.setDescription(description);
		
		if(filterParameters != null)
			beanRule.setFilterParameters(filterParameters);
		
		if(constructorParameters != null) {
			Parameters constructorArgumentItemHolderParameters = constructorParameters.getParameters(ConstructorParameters.arguments);
			if(constructorArgumentItemHolderParameters != null) {
				ItemRuleMap constructorArgumentItemRuleMap = disassembleItemRuleMap(constructorArgumentItemHolderParameters);
				beanRule.setConstructorArgumentItemRuleMap(constructorArgumentItemRuleMap);
			}
		}
		
		if(propertyItemHolderParameters != null) {
			ItemRuleMap propertyItemRuleMap = disassembleItemRuleMap(propertyItemHolderParameters);
			beanRule.setPropertyItemRuleMap(propertyItemRuleMap);
		}
		
		assistant.addBeanRule(beanRule);
	}

	private void disassembleTransletRule(Parameters transletParameters) {
		String description = transletParameters.getString(TransletParameters.description);
		String name = StringUtils.emptyToNull(transletParameters.getString(TransletParameters.name));
		String scan = transletParameters.getString(TransletParameters.scan);
		String mask = transletParameters.getString(TransletParameters.mask);
		String method = transletParameters.getString(TransletParameters.method);
		
		if(name == null && scan == null)
			throw new IllegalArgumentException("The 'translet' element requires a 'name' attribute.");

		TransletRule transletRule = TransletRule.newInstance(name, mask, scan, method);
		
		if(description != null)
			transletRule.setDescription(description);
		
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
				ActionList actionList = disassembleActionList(contentParamters);
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
			ExceptionRule exceptionRule = new ExceptionRule();
			exceptionRule.setDescription(exceptionParameters.getString(ExceptionParameters.description));

			Parameters actionParameters = exceptionParameters.getParameters(ExceptionParameters.action);
			if(actionParameters != null) {
				disassembleActionRule(actionParameters, exceptionRule);
			}

			List<Parameters> rbctParametersList = exceptionParameters.getParametersList(ExceptionParameters.responseByContentTypes);
			if(rbctParametersList != null) {
				for(Parameters rrtrParameters : rbctParametersList) {
					ResponseByContentTypeRule rbctr = disassembleResponseByContentTypeRule(rrtrParameters);
					exceptionRule.putResponseByContentTypeRule(rbctr);
				}
			}

			transletRule.setExceptionRule(exceptionRule);
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
	
	private RequestRule disassembleRequestRule(Parameters requestParameters) {
		String allowedMethod = requestParameters.getString(RequestParameters.allowedMethod);
		String characterEncoding = requestParameters.getString(RequestParameters.characterEncoding);
		ItemHolderParameters parameterItemHolderParameters = requestParameters.getParameters(RequestParameters.parameters);
		ItemHolderParameters attributeItemHolderParameters = requestParameters.getParameters(RequestParameters.attributes);

		RequestRule requestRule = RequestRule.newInstance(allowedMethod, characterEncoding);
	
		if(parameterItemHolderParameters != null) {
			ItemRuleMap parameterItemRuleMap = disassembleItemRuleMap(parameterItemHolderParameters);
			requestRule.setParameterItemRuleMap(parameterItemRuleMap);
		}

		if(attributeItemHolderParameters != null) {
			ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(attributeItemHolderParameters);
			requestRule.setAttributeItemRuleMap(attributeItemRuleMap);
		}
	
		return requestRule;
	}

	private ResponseRule disassembleResponseRule(Parameters responseParameters) {
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
	
	private ContentList disassembleContentList(Parameters contentsParameters) {
		String name = contentsParameters.getString(ContentsParameters.name);
		Boolean omittable = contentsParameters.getBoolean(ContentsParameters.omittable);
		List<Parameters> contentParametersList = contentsParameters.getParametersList(ContentsParameters.contents);
		
		ContentList contentList = ContentList.newInstance(name, omittable);

		if(contentParametersList != null) {
			for(Parameters contentParamters : contentParametersList) {
				ActionList actionList = disassembleActionList(contentParamters);
				contentList.addActionList(actionList);
			}
		}
		
		return contentList;
	}
	
	private ActionList disassembleActionList(Parameters contentParameters) {
		String name = contentParameters.getString(ContentParameters.name);
		Boolean omittable = contentParameters.getBoolean(ContentParameters.omittable);
		Boolean hidden = contentParameters.getBoolean(ContentParameters.hidden);
		List<Parameters> actionParametersList = contentParameters.getParametersList(ContentParameters.actions);
		
		ActionList actionList = ActionList.newInstance(name, omittable, hidden);

		if(actionParametersList != null) {
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
		}

		return actionList;
	}
	
	private void disassembleActionRule(Parameters actionParameters, ActionRuleApplicable actionRuleApplicable) {
		String id = StringUtils.emptyToNull(actionParameters.getString(ActionParameters.id));
		String beanIdOrClass = StringUtils.emptyToNull(actionParameters.getString(ActionParameters.bean));
		String methodName = StringUtils.emptyToNull(actionParameters.getString(ActionParameters.methodName));
		ItemHolderParameters argumentItemHolderParameters = actionParameters.getParameters(ActionParameters.arguments);
		ItemHolderParameters propertyItemHolderParameters = actionParameters.getParameters(ActionParameters.properties);
		String include = actionParameters.getString(ActionParameters.include);
		ItemHolderParameters attributeItemHolderParameters = actionParameters.getParameters(ActionParameters.attributes);
		ItemHolderParameters echoItemHolderParameters = actionParameters.getParameters(ActionParameters.echo);
		Boolean hidden = actionParameters.getBoolean(ActionParameters.hidden);
		
		if(!assistant.isNullableActionId() && id == null)
			throw new IllegalArgumentException("The 'action' element requires an 'id' attribute. The 'nullableActionId' setting is true.");

		if(methodName != null) {
			BeanActionRule beanActionRule = BeanActionRule.newInstance(id, beanIdOrClass, methodName, hidden);
			if(argumentItemHolderParameters != null) {
				ItemRuleMap argumentItemRuleMap = disassembleItemRuleMap(argumentItemHolderParameters);
				beanActionRule.setArgumentItemRuleMap(argumentItemRuleMap);
			}
			if(propertyItemHolderParameters != null) {
				ItemRuleMap propertyItemRuleMap = disassembleItemRuleMap(propertyItemHolderParameters);
				beanActionRule.setPropertyItemRuleMap(propertyItemRuleMap);
			}
			if(beanIdOrClass != null) {
				assistant.resolveBeanClass(beanIdOrClass, beanActionRule);
			}
			actionRuleApplicable.applyActionRule(beanActionRule);
		} else if(include != null) {
			include = assistant.applyTransletNamePattern(include);
			IncludeActionRule includeActionRule = IncludeActionRule.newInstance(id, include, hidden);
			if(attributeItemHolderParameters != null) {
				ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(attributeItemHolderParameters);
				includeActionRule.setAttributeItemRuleMap(attributeItemRuleMap);
			}
			actionRuleApplicable.applyActionRule(includeActionRule);
		} else if(echoItemHolderParameters != null) {
			EchoActionRule echoActionRule = EchoActionRule.newInstance(id, hidden);
			ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(echoItemHolderParameters);
			echoActionRule.setAttributeItemRuleMap(attributeItemRuleMap);
			actionRuleApplicable.applyActionRule(echoActionRule);
		}
	}

	private ResponseByContentTypeRule disassembleResponseByContentTypeRule(Parameters responseByContentTypeParameters) {
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
	
	private void disassembleTransformRule(List<Parameters> transformParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters transformParameters : transformParametersList) {
			TransformRule tr = disassembleTransformRule(transformParameters);
			responseRuleApplicable.applyResponseRule(tr);
		}
	}
	
	private TransformRule disassembleTransformRule(Parameters transformParameters) {
		String transformType = transformParameters.getString(TransformParameters.type);
		String contentType = transformParameters.getString(TransformParameters.contentType);
		String templateId = transformParameters.getString(TransformParameters.template);
		String characterEncoding = transformParameters.getString(TransformParameters.characterEncoding);
		List<Parameters> actionParametersList = transformParameters.getParametersList(TransformParameters.actions);
		Boolean defaultResponse = transformParameters.getBoolean(TransformParameters.defaultResponse);
		Boolean pretty = transformParameters.getBoolean(TransformParameters.pretty);
		Parameters templateParameters = transformParameters.getParameters(TransformParameters.builtin);

		TransformRule tr = TransformRule.newInstance(transformType, contentType, templateId, characterEncoding, defaultResponse, pretty);
		
		if(actionParametersList != null && !actionParametersList.isEmpty()) {
			ActionList actionList = new ActionList();
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
			tr.setActionList(actionList);
		}
		
		if(templateParameters != null) {
			String engine = templateParameters.getString(TemplateParameters.engine);
			String name = templateParameters.getString(TemplateParameters.name);
			String file = templateParameters.getString(TemplateParameters.file);
			String resource = templateParameters.getString(TemplateParameters.resource);
			String url = templateParameters.getString(TemplateParameters.url);
			String content = templateParameters.getString(TemplateParameters.content);
			String encoding = templateParameters.getString(TemplateParameters.encoding);
			Boolean noCache = templateParameters.getBoolean(TemplateParameters.noCache);
			TemplateRule templateRule = TemplateRule.newInstanceForBuiltin(engine, name, file, resource, url, content, encoding, noCache);
			tr.setTemplateRule(templateRule);
		}
		
		return tr;
	}

	private void disassembleDispatchResponseRule(List<Parameters> dispatchParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters dispatchParameters : dispatchParametersList) {
			DispatchResponseRule drr = disassembleDispatchResponseRule(dispatchParameters);
			responseRuleApplicable.applyResponseRule(drr);
		}
	}
	
	private DispatchResponseRule disassembleDispatchResponseRule(Parameters dispatchParameters) {
		String name = dispatchParameters.getString(DispatchParameters.name);
		String dispatcher = dispatchParameters.getString(DispatchParameters.dispatcher);
		String contentType = dispatchParameters.getString(DispatchParameters.contentType);
		String characterEncoding = dispatchParameters.getString(DispatchParameters.characterEncoding);
		List<Parameters> actionParametersList = dispatchParameters.getParametersList(DispatchParameters.actions);
		Boolean defaultResponse = dispatchParameters.getBoolean(DispatchParameters.defaultResponse);
		
		DispatchResponseRule drr = DispatchResponseRule.newInstance(name, dispatcher, contentType, characterEncoding, defaultResponse);
		
		if(actionParametersList != null && !actionParametersList.isEmpty()) {
			ActionList actionList = new ActionList();
			for(Parameters actionParameters : actionParametersList) {
				disassembleActionRule(actionParameters, actionList);
			}
			drr.setActionList(actionList);
		}
		
		return drr;
	}

	private void disassembleRedirectResponseRule(List<Parameters> redirectParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters redirectParameters : redirectParametersList) {
			RedirectResponseRule rrr = disassembleRedirectResponseRule(redirectParameters);
			responseRuleApplicable.applyResponseRule(rrr);
		}
	}
	
	private RedirectResponseRule disassembleRedirectResponseRule(Parameters redirectParameters) {
		String contentType = redirectParameters.getString(RedirectParameters.contentType);
		String target = redirectParameters.getString(RedirectParameters.target);
		ItemHolderParameters parameterItemHolderParametersList = redirectParameters.getParameters(RedirectParameters.parameters);
		Boolean excludeNullParameter = redirectParameters.getBoolean(RedirectParameters.excludeNullParameter);
		List<Parameters> actionParametersList = redirectParameters.getParametersList(RedirectParameters.actions);
		Boolean defaultResponse = redirectParameters.getBoolean(RedirectParameters.defaultResponse);
		
		RedirectResponseRule rrr = RedirectResponseRule.newInstance(contentType, target, excludeNullParameter, defaultResponse);
		
		if(parameterItemHolderParametersList != null) {
			ItemRuleMap parameterItemRuleMap = disassembleItemRuleMap(parameterItemHolderParametersList);
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

	private void disassembleForwardResponseRule(List<Parameters> forwardParametersList, ResponseRuleApplicable responseRuleApplicable) {
		for(Parameters forwardParameters : forwardParametersList) {
			ForwardResponseRule frr = disassembleForwardResponseRule(forwardParameters);
			responseRuleApplicable.applyResponseRule(frr);
		}
	}

	private ForwardResponseRule disassembleForwardResponseRule(Parameters forwardParameters) {
		String contentType = forwardParameters.getString(ForwardParameters.contentType);
		String translet = StringUtils.emptyToNull(forwardParameters.getString(ForwardParameters.translet));
		ItemHolderParameters attributeItemHolderParametersList = forwardParameters.getParameters(ForwardParameters.attributes);
		List<Parameters> actionParametersList = forwardParameters.getParametersList(ForwardParameters.actions);
		Boolean defaultResponse = forwardParameters.getBoolean(ForwardParameters.defaultResponse);

		if(translet == null)
			throw new IllegalArgumentException("The 'forward' element requires a 'translet' attribute.");

		translet = assistant.applyTransletNamePattern(translet);

		ForwardResponseRule rrr = ForwardResponseRule.newInstance(contentType, translet, defaultResponse);
		
		if(attributeItemHolderParametersList != null) {
			ItemRuleMap attributeItemRuleMap = disassembleItemRuleMap(attributeItemHolderParametersList);
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
	
	private ItemRuleMap disassembleItemRuleMap(Parameters itemHolderParameters) {
		List<Parameters> itemParametersList = itemHolderParameters.getParametersList(ItemHolderParameters.item);
		ItemRuleMap itemRuleMap = ItemRule.toItemRuleMap(itemParametersList);
			
		if(itemRuleMap != null) {
			for(ItemRule itemRule : itemRuleMap.values()) {
				assistant.resolveBeanClass(itemRule);
			}
		}
		
		return itemRuleMap;
	}

	private void disassembleTemplateRule(Parameters templateParameters) {
		String id = StringUtils.emptyToNull(templateParameters.getString(TemplateParameters.id));
		String engine = StringUtils.emptyToNull(templateParameters.getString(TemplateParameters.engine));
		String name = StringUtils.emptyToNull(templateParameters.getString(TemplateParameters.name));
		String file = StringUtils.emptyToNull(templateParameters.getString(TemplateParameters.file));
		String resource = StringUtils.emptyToNull(templateParameters.getString(TemplateParameters.resource));
		String url = StringUtils.emptyToNull(templateParameters.getString(TemplateParameters.url));
		String content = StringUtils.emptyToNull(templateParameters.getString(TemplateParameters.content));
		String encoding = templateParameters.getString(TemplateParameters.encoding);
		Boolean noCache = templateParameters.getBoolean(TemplateParameters.noCache);

		if(id == null)
			throw new IllegalArgumentException("The 'template' element requires an 'id' attribute.");

		TemplateRule templateRule = TemplateRule.newInstance(id, engine, name, file, resource, url, content, encoding, noCache);

		if(engine != null) {
			assistant.putBeanReference(engine, templateRule);
		}

		assistant.addTemplateRule(templateRule);
	}
	
}
