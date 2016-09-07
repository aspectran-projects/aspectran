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
package com.aspectran.core.context.rule;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.aspectran.core.activity.process.action.BeanAction;
import com.aspectran.core.activity.process.action.EchoAction;
import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.activity.process.action.HeadingAction;
import com.aspectran.core.context.rule.ability.ActionRuleApplicable;
import com.aspectran.core.context.rule.type.ActionType;

/**
 * The Class ExceptionRule.
 * 
 * <p>Created: 2009. 03. 09 PM 23:48:09</p>
 */
public class ExceptionRule implements ActionRuleApplicable, Iterable<ExceptionCatchRule> {

	private final AspectRule aspectRule;

	private Executable action;
	
	private ExceptionCatchRule defaultExceptionCatchRule;
	
	private Map<String, ExceptionCatchRule> exceptionCatchRuleMap = new LinkedHashMap<>();
	
	private String description;

	public ExceptionRule() {
		this.aspectRule = null;
	}

	public ExceptionRule(AspectRule aspectRule) {
		this.aspectRule = aspectRule;
	}

	public String getAspectId() {
		if(aspectRule == null)
			throw new UnsupportedOperationException();

		return aspectRule.getId();
	}

	public AspectRule getAspectRule() {
		return aspectRule;
	}

	@Override
	public void applyActionRule(BeanActionRule beanActionRule) {
		action = new BeanAction(beanActionRule, null);
	}

	@Override
	public void applyActionRule(MethodActionRule methodActionRule) {
		throw new UnsupportedOperationException(
				"Cannot apply the Method Action Rule to the Exception Rule.");
	}

	@Override
	public void applyActionRule(IncludeActionRule includeActionRule) {
		throw new UnsupportedOperationException(
				"Cannot apply the Include Action Rule to the Exception Rule.");
	}

	@Override
	public void applyActionRule(EchoActionRule echoActionRule) {
		action = new EchoAction(echoActionRule, null);
	}

	@Override
	public void applyActionRule(HeadingActionRule headingActionRule) {
		action = new HeadingAction(headingActionRule, null);
	}
	
	/**
	 * Gets the executable action.
	 *
	 * @return the executable action
	 */
	public Executable getExecutableAction() {
		return action;
	}
	
	/**
	 * Gets the action type.
	 *
	 * @return the action type
	 */
	public ActionType getActionType() {
		return (action == null) ? null : action.getActionType();
	}

	/**
	 * Gets the response by content type rule.
	 *
	 * @return the response by content type rule
	 */
	public ExceptionCatchRule getExceptionCatchRule() {
		return defaultExceptionCatchRule;
	}

	/**
	 * Puts the exception catch rule.
	 *
	 * @param exceptionCatchRule the exception catch rule
	 * @return the exception catch rule
	 */
	public ExceptionCatchRule putExceptionCatchRule(ExceptionCatchRule exceptionCatchRule) {
		String exceptionType = exceptionCatchRule.getExceptionType();
		
		if(exceptionType != null) {
			exceptionCatchRuleMap.put(exceptionType, exceptionCatchRule);
		} else { 
			this.defaultExceptionCatchRule = exceptionCatchRule;
		}
		
		return exceptionCatchRule;
	}
	
	/**
	 * Gets the exception catch rule as specified exception.
	 *
	 * @param ex the exception
	 * @return the rule for the response by content type
	 */
	public ExceptionCatchRule getExceptionCatchRule(Throwable ex) {
		ExceptionCatchRule exceptionCatchRule = null;
		int deepest = Integer.MAX_VALUE;

		for(ExceptionCatchRule rbctr : exceptionCatchRuleMap.values()) {
			int depth = getMatchedDepth(rbctr.getExceptionType(), ex);

			if(depth >= 0 && depth < deepest) {
				deepest = depth;
				exceptionCatchRule = rbctr;
			}
		}

		if(exceptionCatchRule == null) {
			return this.defaultExceptionCatchRule;
		}

		return exceptionCatchRule;
	}
	
	/**
	 * Gets the matched depth.
	 *
	 * @param exceptionType the exception type
	 * @param ex the throwable exception
	 * @return the matched depth
	 */
	private int getMatchedDepth(String exceptionType, Throwable ex) {
		Throwable t = ex.getCause();
		if(t != null) {
			return getMatchedDepth(exceptionType, t);
		}

		return getMatchedDepth(exceptionType, ex.getClass(), 0);
	}

	/**
	 * Gets the matched depth.
	 *
	 * @param exceptionType the exception type
	 * @param exceptionClass the exception class
	 * @param depth the depth
	 * @return the matched depth
	 */
	private int getMatchedDepth(String exceptionType, Class<?> exceptionClass, int depth) {
		if(exceptionClass.getName().contains(exceptionType))
			return depth;

		if(exceptionClass.equals(Throwable.class))
			return -1;
		
		return getMatchedDepth(exceptionType, exceptionClass.getSuperclass(), depth + 1);
	}

	@Override
	public Iterator<ExceptionCatchRule> iterator() {
		return exceptionCatchRuleMap.values().iterator();
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public static ExceptionRule newInstance(AspectRule aspectRule) {
		return new ExceptionRule(aspectRule);
	}

}
