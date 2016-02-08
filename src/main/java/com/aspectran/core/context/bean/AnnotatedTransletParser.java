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
package com.aspectran.core.context.bean;

import java.lang.reflect.Method;

import com.aspectran.core.context.bean.annotation.Dispatch;
import com.aspectran.core.context.bean.annotation.Forward;
import com.aspectran.core.context.bean.annotation.Redirect;
import com.aspectran.core.context.bean.annotation.Transform;
import com.aspectran.core.context.bean.annotation.Request;
import com.aspectran.core.context.bean.annotation.Configuration;
import com.aspectran.core.context.rule.BeanActionRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.DispatchResponseRule;
import com.aspectran.core.context.rule.ForwardResponseRule;
import com.aspectran.core.context.rule.RedirectResponseRule;
import com.aspectran.core.context.rule.ResponseRule;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.TransletRuleMap;
import com.aspectran.core.context.rule.type.RequestMethodType;
import com.aspectran.core.util.StringUtils;

/**
 * The Class AnnotatedTransletParser.
 */
public abstract class AnnotatedTransletParser {

	public static void parse(BeanRule beanRule, TransletRuleMap transletRuleMap) {
		Class<?> targetClass = beanRule.getBeanClass();
		if(!targetClass.isAnnotationPresent(Configuration.class))
			return;

		Configuration configAnno = targetClass.getAnnotation(Configuration.class);
		String namespace = StringUtils.emptyToNull(configAnno.namespace());
		
		for(Method method : targetClass.getMethods()) {
			if(method.isAnnotationPresent(Request.class)) {
				Request requestAnno = method.getAnnotation(Request.class);
				String transletName = StringUtils.emptyToNull(requestAnno.translet());
				RequestMethodType[] requestMethods = requestAnno.method();
				
				if(namespace != null && transletName != null)
					transletName = namespace + transletName;
				
				TransletRule transletRule = TransletRule.newInstance(transletName, requestMethods);
				
				if(method.isAnnotationPresent(Dispatch.class)) {
					Dispatch dispatchAnno = method.getAnnotation(Dispatch.class);
					String dispatchName = StringUtils.emptyToNull(dispatchAnno.name());
					String characterEncoding = StringUtils.emptyToNull(dispatchAnno.characterEncoding());
					DispatchResponseRule drr = DispatchResponseRule.newInstance(dispatchName, characterEncoding);
					transletRule.setResponseRule(ResponseRule.newInstance(drr));
				} else if(method.isAnnotationPresent(Transform.class)) {
					Transform transformAnno = method.getAnnotation(Transform.class);
					String transformType = StringUtils.emptyToNull(transformAnno.transformType());
					String contentType = StringUtils.emptyToNull(transformAnno.contentType());
					String templateId = StringUtils.emptyToNull(transformAnno.templateId());
					String characterEncoding = StringUtils.emptyToNull(transformAnno.characterEncoding());
					boolean pretty = transformAnno.pretty();
					TransformRule tr = TransformRule.newInstance(transformType, contentType, templateId, characterEncoding, null, pretty);
					transletRule.setResponseRule(ResponseRule.newInstance(tr));
				} else if(method.isAnnotationPresent(Forward.class)) {
					Forward forwardAnno = method.getAnnotation(Forward.class);
					String translet = StringUtils.emptyToNull(forwardAnno.translet());
					ForwardResponseRule frr = ForwardResponseRule.newInstance(translet);
					transletRule.setResponseRule(ResponseRule.newInstance(frr));
				} else if(method.isAnnotationPresent(Redirect.class)) {
					Redirect redirectAnno = method.getAnnotation(Redirect.class);
					String target = StringUtils.emptyToNull(redirectAnno.target());
					RedirectResponseRule rrr = RedirectResponseRule.newInstance(target);
					transletRule.setResponseRule(ResponseRule.newInstance(rrr));
				}
				
				BeanActionRule beanActionRule = new BeanActionRule();
				beanActionRule.setBeanId(beanRule.getId());
				beanActionRule.setMethodName(method.getName());
				transletRule.applyActionRule(beanActionRule);
				
				transletRuleMap.putTransletRule(transletRule);
			}
		}
	}
	
}
