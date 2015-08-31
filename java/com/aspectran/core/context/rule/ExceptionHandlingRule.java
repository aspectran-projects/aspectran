/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.rule;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.aspectran.core.activity.process.action.BeanAction;
import com.aspectran.core.activity.process.action.EchoAction;
import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.context.rule.ability.ActionRuleApplicable;
import com.aspectran.core.context.rule.type.ActionType;

/**
 * <p>
 * Created: 2009. 03. 09 오후 23:48:09
 * </p>
 */
public class ExceptionHandlingRule implements ActionRuleApplicable, Iterable<ResponseByContentTypeRule> {

	private Executable action;
	
	private ResponseByContentTypeRule defaultResponseByContentTypeRule;
	
	private Map<String, ResponseByContentTypeRule> responseByContentTypeRuleMap = new LinkedHashMap<String, ResponseByContentTypeRule>();

	/* (non-Javadoc)
	 * @see com.aspectran.core.context.rule.ability.ActionRuleApplicable#applyActionRule(com.aspectran.core.context.rule.EchoActionRule)
	 */
	public void applyActionRule(EchoActionRule echoActionRule) {
		action = new EchoAction(echoActionRule, null);
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.context.rule.ability.ActionRuleApplicable#applyActionRule(com.aspectran.core.context.rule.BeanActionRule)
	 */
	public void applyActionRule(BeanActionRule beanActionRule) {
		action = new BeanAction(beanActionRule, null);
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.context.rule.ability.ActionRuleApplicable#applyActionRule(com.aspectran.core.context.rule.IncludeActionRule)
	 */
	public void applyActionRule(IncludeActionRule includeActionRule) {
		throw new UnsupportedOperationException("There is nothing that can be apply to IncludeActionRule. The aspecet-advice is not support include-action.");
	}
	
	public Executable getExecutableAction() {
		return action;
	}
	
	public ActionType getActionType() {
		if(action == null)
			return null;
		
		return action.getActionType();
	}

	public ResponseByContentTypeRule getResponseByContentTypeRule() {
		return defaultResponseByContentTypeRule;
	}

	public ResponseByContentTypeRule putResponseByContentTypeRule(ResponseByContentTypeRule responseByContentTypeRule) {
		String exceptionType = responseByContentTypeRule.getExceptionType();
		
		if(exceptionType != null) {
			responseByContentTypeRuleMap.put(exceptionType, responseByContentTypeRule);
		} else { 
			this.defaultResponseByContentTypeRule = responseByContentTypeRule;
		}
		
		return responseByContentTypeRule;
	}
	
	public ResponseByContentTypeRule getResponseByContentTypeRule(Exception ex) {
		ResponseByContentTypeRule responseByContentTypeRule = null;
		int deepest = Integer.MAX_VALUE;
		
		for(Iterator<ResponseByContentTypeRule> iter = iterator(); iter.hasNext();) {
			ResponseByContentTypeRule rbctr = iter.next();
			
			int depth = getMatchedDepth(rbctr.getExceptionType(), ex);
			
			if(depth >= 0 && depth < deepest) {
				deepest = depth;
				responseByContentTypeRule = rbctr;
			}
		}
		
		if(responseByContentTypeRule == null)
			return this.defaultResponseByContentTypeRule;
		
		return responseByContentTypeRule;
	}
	
	private int getMatchedDepth(String exceptionType, Exception ex) {
		return getMatchedDepth(exceptionType, ex.getClass(), 0);
	}

	private int getMatchedDepth(String exceptionType, Class<?> exceptionClass, int depth) {
		if(exceptionClass.getName().indexOf(exceptionType) != -1)
			return depth;

		if(exceptionClass.equals(Throwable.class))
			return -1;
		
		return getMatchedDepth(exceptionType, exceptionClass.getSuperclass(), depth + 1);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<ResponseByContentTypeRule> iterator() {
		return responseByContentTypeRuleMap.values().iterator();
	}

}
