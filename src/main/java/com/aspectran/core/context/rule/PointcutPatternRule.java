/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.aspectran.core.context.rule;

import java.util.ArrayList;
import java.util.List;

import com.aspectran.core.context.rule.type.JoinpointScopeType;
import com.aspectran.core.context.rule.type.PointcutType;

public class PointcutPatternRule {
	
	private static final char POINTCUT_BEAN_ID_DELIMITER = '@';
	
	private static final char POINTCUT_METHOD_NAME_DELIMITER = '^';

	private static final char JOINPOINT_SCOPE_DELIMITER = '$';
	
	private PointcutType pointcutType;
	
	private String patternString;
	
	private String transletNamePattern;
	
	private String beanIdPattern;

	private String beanMethodNamePattern;
	
	private int matchedTransletCount;
	
	private int matchedBeanCount;
	
	private int matchedBeanMethodCount;
	
	private List<PointcutPatternRule> excludePointcutPatternRuleList;
	
	public PointcutPatternRule() {
	}
	
	public PointcutType getPointcutType() {
		return pointcutType;
	}

	protected void setPointcutType(PointcutType pointcutType) {
		this.pointcutType = pointcutType;
	}

	public String getPatternString() {
		return patternString;
	}

	public void setPatternString(String patternString) {
		this.patternString = patternString;
	}

	public String getTransletNamePattern() {
		return transletNamePattern;
	}

	public void setTransletNamePattern(String transletNamePattern) {
		this.transletNamePattern = transletNamePattern;
	}

	public String getBeanIdPattern() {
		return beanIdPattern;
	}

	public void setBeanIdPattern(String beanIdPattern) {
		this.beanIdPattern = beanIdPattern;
	}

	public String getBeanMethodNamePattern() {
		return beanMethodNamePattern;
	}

	public void setBeanMethodNamePattern(String beanMethodNamePattern) {
		this.beanMethodNamePattern = beanMethodNamePattern;
	}
	
	public List<PointcutPatternRule> getExcludePointcutPatternRuleList() {
		return excludePointcutPatternRuleList;
	}

	public void setExcludePointcutPatternRuleList(List<PointcutPatternRule> excludePointcutPatternRuleList) {
		this.excludePointcutPatternRuleList = excludePointcutPatternRuleList;
	}
	
	public void addExcludePointcutPatternRule(PointcutPatternRule excludePointcutPatternRule) {
		if(excludePointcutPatternRuleList == null)
			excludePointcutPatternRuleList = new ArrayList<PointcutPatternRule>();
		
		excludePointcutPatternRuleList.add(excludePointcutPatternRule);
	}

	public int getMatchedTransletCount() {
		return matchedTransletCount;
	}

	public void increaseMatchedTransletCount() {
		matchedTransletCount++;
	}

	public int getMatchedBeanCount() {
		return matchedBeanCount;
	}

	public void increaseMatchedBeanCount() {
		matchedBeanCount++;
	}

	public int getMatchedBeanMethodCount() {
		return matchedBeanMethodCount;
	}

	public void increaseMatchedBeanMethodCount() {
		matchedBeanMethodCount++;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{translet=").append(transletNamePattern);
		sb.append(", bean=").append(beanIdPattern);
		sb.append(", method=").append(beanMethodNamePattern);
		sb.append("}");
		
		return sb.toString();
	}
	
	public static String combinePatternString(String transletName, String beanId, String beanMethodName) {
		StringBuilder sb = new StringBuilder();
		
		if(transletName != null)
			sb.append(transletName);
		
		if(beanId != null) {
			sb.append(POINTCUT_BEAN_ID_DELIMITER);
			sb.append(beanId);
		}
		
		if(beanMethodName != null) {
			sb.append(POINTCUT_METHOD_NAME_DELIMITER);
			sb.append(beanMethodName);
		}
		
		return sb.toString();
	}
	
	public static String combinePatternString(JoinpointScopeType joinpointScope, String transletName, String beanId, String methodName) {
		StringBuilder sb = new StringBuilder();
		
		if(joinpointScope != null) {
			sb.append(joinpointScope);
			sb.append(JOINPOINT_SCOPE_DELIMITER);
		}
		
		if(transletName != null)
			sb.append(transletName);
		
		if(beanId != null) {
			sb.append(POINTCUT_BEAN_ID_DELIMITER);
			sb.append(beanId);
		}
		
		if(methodName != null) {
			sb.append(POINTCUT_METHOD_NAME_DELIMITER);
			sb.append(methodName);
		}
		
		return sb.toString();
	}
	
	public static PointcutPatternRule parsePatternString(String patternString) {
		PointcutPatternRule pointcutPatternRule = new PointcutPatternRule();
		pointcutPatternRule.setPatternString(patternString);
		
		String transletNamePattern = null;
		String beanIdPattern = null;
		String beanMethodNamePattern = null;

		int actionDelimiterIndex = patternString.indexOf(POINTCUT_BEAN_ID_DELIMITER);
		
		if(actionDelimiterIndex == -1)
			transletNamePattern = patternString;
		else if(actionDelimiterIndex == 0)
			beanIdPattern = patternString.substring(1);
		else {
			transletNamePattern = patternString.substring(0, actionDelimiterIndex);
			beanIdPattern = patternString.substring(actionDelimiterIndex + 1);
		}

		if(beanIdPattern != null) {
			int beanMethodDelimiterIndex = beanIdPattern.indexOf(POINTCUT_METHOD_NAME_DELIMITER);
			
			if(beanMethodDelimiterIndex == 0) {
				beanMethodNamePattern = beanIdPattern.substring(1);
				beanIdPattern = null;
			} else if(beanMethodDelimiterIndex > 0) {
				beanMethodNamePattern = beanIdPattern.substring(beanMethodDelimiterIndex + 1);
				beanIdPattern = beanIdPattern.substring(0, beanMethodDelimiterIndex);
			}
		}
		
		if(transletNamePattern != null)
			pointcutPatternRule.setTransletNamePattern(transletNamePattern);
		
		if(beanIdPattern != null)
			pointcutPatternRule.setBeanIdPattern(beanIdPattern);

		if(beanMethodNamePattern != null)
			pointcutPatternRule.setBeanMethodNamePattern(beanMethodNamePattern);
		
		return pointcutPatternRule;
	}
	
	public static PointcutPatternRule newInstance(String translet, String bean, String method) {
		PointcutPatternRule pointcutPatternRule = new PointcutPatternRule();

		if(translet != null && translet.length() > 0)						
			pointcutPatternRule.setTransletNamePattern(translet);
		if(bean != null && bean.length() > 0)
			pointcutPatternRule.setBeanIdPattern(bean);
		if(method != null && method.length() > 0)
			pointcutPatternRule.setBeanMethodNamePattern(method);
		
		return pointcutPatternRule;
	}
	
}
