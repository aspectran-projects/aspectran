/*
 *  Copyright (c) 2010 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.context.rule;

import java.util.ArrayList;
import java.util.List;

import com.aspectran.core.context.builder.apon.params.PointcutParameters;
import com.aspectran.core.context.builder.apon.params.TargetParameters;
import com.aspectran.core.context.rule.type.AspectTargetType;
import com.aspectran.core.context.rule.type.PointcutType;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.apon.Parameters;

public class PointcutRule {
	
	private PointcutType pointcutType;
	
	private List<PointcutPatternRule> pointcutPatternRuleList;
	
	private List<Parameters> targetParametersList;
	
	private Parameters simpleTriggerParameters;
	
	private Parameters cronTriggerParameters;
	
	public PointcutType getPointcutType() {
		return pointcutType;
	}

	public void setPointcutType(PointcutType pointcutType) {
		this.pointcutType = pointcutType;
	}

	public List<PointcutPatternRule> getPointcutPatternRuleList() {
		return pointcutPatternRuleList;
	}

	public void setPointcutPatternRuleList(List<PointcutPatternRule> pointcutPatternRuleList) {
		this.pointcutPatternRuleList = pointcutPatternRuleList;
	}
	
	public synchronized void addPointcutPatternRule(List<PointcutPatternRule> pointcutPatternRuleList) {
		if(this.pointcutPatternRuleList == null) {
			this.pointcutPatternRuleList = pointcutPatternRuleList;
		} else {
			this.pointcutPatternRuleList.addAll(pointcutPatternRuleList);
		}
	}
	
	public void addPointcutPatternRule(PointcutPatternRule pointcutPatternRule) {
		touchPointcutPatternRuleList();

		pointcutPatternRuleList.add(pointcutPatternRule);
	}
	
	public synchronized List<PointcutPatternRule> touchPointcutPatternRuleList() {
		if(pointcutPatternRuleList == null) {
			pointcutPatternRuleList = newPointcutPatternRuleList();
		}
		
		return pointcutPatternRuleList;
	}

	public static List<PointcutPatternRule> newPointcutPatternRuleList() {
		return new ArrayList<PointcutPatternRule>();
	}

	public List<Parameters> touchTargetParametersList() {
		if(targetParametersList == null)
			targetParametersList = new ArrayList<Parameters>();
		
		return targetParametersList;
	}

	public List<Parameters> getTargetParametersList() {
		return targetParametersList;
	}
	
	public void setTargetParametersList(List<Parameters> targetParametersList) {
		this.targetParametersList = targetParametersList;
	}

	public Parameters getSimpleTriggerParameters() {
		return simpleTriggerParameters;
	}

	public void setSimpleTriggerParameters(Parameters simpleTriggerParameters) {
		this.simpleTriggerParameters = simpleTriggerParameters;
	}
	
	public Parameters getCronTriggerParameters() {
		return cronTriggerParameters;
	}

	public void setCronTriggerParameters(Parameters cronTriggerParameters) {
		this.cronTriggerParameters = cronTriggerParameters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{pointcutType=").append(pointcutType);
		sb.append(", targetParametersList=").append(targetParametersList);
		sb.append(", simpleTriggerParameters=").append(simpleTriggerParameters);
		sb.append(", cronTriggerParameters=").append(cronTriggerParameters);
		sb.append("}");
		
		return sb.toString();
	}
	
	public static PointcutRule newInstance(AspectRule aspectRule, String type, String text) {
		if(StringUtils.hasText(text)) {
			Parameters pointcutParameters = new PointcutParameters(text);
			return newInstance(aspectRule, type, pointcutParameters);
		} else {
			return newInstance(aspectRule, type, (Parameters)null);
		}
	}
	
	public static PointcutRule newInstance(AspectRule aspectRule, String type, Parameters pointcutParameters) {
		PointcutRule pointcutRule = new PointcutRule();

		if(aspectRule.getAspectTargetType() == AspectTargetType.SCHEDULER) {
			PointcutType pointcutType = null;
			Parameters simpleTriggerParameters = null;
			Parameters cronTriggerParameters = null;

			if(pointcutParameters != null) {
				simpleTriggerParameters = pointcutParameters.getParameters(PointcutParameters.simpleTrigger);
				cronTriggerParameters = pointcutParameters.getParameters(PointcutParameters.cronTrigger);
	
				if(simpleTriggerParameters != null) {
					pointcutType = PointcutType.SIMPLE_TRIGGER;
					pointcutRule.setSimpleTriggerParameters(simpleTriggerParameters);
				} else if(cronTriggerParameters != null) {
					pointcutType = PointcutType.CRON_TRIGGER;
					pointcutRule.setCronTriggerParameters(cronTriggerParameters);
				}
			}
			
			if(pointcutType != null) {
				pointcutRule.setPointcutType(pointcutType);
			} else {
				pointcutType = PointcutType.valueOf(type);
				
				if(pointcutType != PointcutType.SIMPLE_TRIGGER && pointcutType != PointcutType.CRON_TRIGGER)
					throw new IllegalArgumentException("Unknown pointcut-type '" + type + "'. Scheduler's pointcut-type must be 'simpleTrigger' or 'cronTrigger'.");
				
				pointcutRule.setPointcutType(pointcutType);
			}
			
			if(pointcutType == PointcutType.SIMPLE_TRIGGER && simpleTriggerParameters == null)
				throw new IllegalArgumentException("Not specified 'simpleTrigger'. Scheduler's pointcut-type must be 'simpleTrigger' or 'cronTrigger'.");
			else if(pointcutType == PointcutType.CRON_TRIGGER && cronTriggerParameters == null)
				throw new IllegalArgumentException("Not specified 'cronTrigger'. Scheduler's pointcut-type must be 'simpleTrigger' or 'cronTrigger'.");
		} else {
			if(pointcutParameters != null) {
				PointcutType pointcutType = null;
				
				if(type == null)
					type = pointcutParameters.getString(PointcutParameters.type);
				
				if(type != null) {
					pointcutType = PointcutType.valueOf(type);
					if(pointcutType == null)
						throw new IllegalArgumentException("Unknown pointcut-type '" + type + "'. Translet's pointcut-type must be 'wildcard' or 'regexp'.");
					
					aspectRule.setPointcutType(pointcutType);
				}
				
				List<Parameters> targetParametersList = pointcutParameters.getParametersList(PointcutParameters.targets);
				if(targetParametersList != null) {
					for(Parameters targetParameters : targetParametersList) {
						addPointcutPatternRule(pointcutRule.touchPointcutPatternRuleList(), targetParameters);
						pointcutRule.touchTargetParametersList().add(targetParameters);
					}
				}
			}
		}
/*		
		pointcutRule.setPointcutType(pointcutType);
		PointcutType pointcutType = null;
		
		if(aspectRule.getAspectTargetType() == AspectTargetType.SCHEDULER) {
			pointcutType = PointcutType.valueOf(type);
			
			if(pointcutType != PointcutType.SIMPLE_TRIGGER && pointcutType != PointcutType.CRON_TRIGGER)
				throw new IllegalArgumentException("Unknown pointcut-type '" + type + "'. Scheduler's pointcut-type must be 'simpleTrigger' or 'cronTrigger'.");
			
			if(!StringUtils.hasText(text))
				throw new IllegalArgumentException("Pointcut pattern can not be null");
		} else {
			if(type != null) {
				pointcutType = PointcutType.valueOf(type);

				if(pointcutType != PointcutType.WILDCARD && pointcutType != PointcutType.REGEXP)
					throw new IllegalArgumentException("Unknown pointcut-type '" + type + "'. Translet's pointcut-type must be 'wildcard' or 'regexp'.");
			} else {
				pointcutType = PointcutType.WILDCARD;
			}
		}
		
		PointcutRule pointcutRule = new PointcutRule();
		pointcutRule.setPointcutType(pointcutType);
		pointcutRule.setPatternString(text);
		
		if(pointcutType == PointcutType.SIMPLE_TRIGGER) {
			Parameters simpleTriggerParameters = new SimpleTriggerParameters(text);
			pointcutRule.setSimpleTriggerParameters(simpleTriggerParameters);
		} else if(pointcutType == PointcutType.CRON_TRIGGER) {
			Parameters cronTriggerParameters = new CronTriggerParameters(text);
			pointcutRule.setCronTriggerParameters(cronTriggerParameters);
		}
*/
		return pointcutRule;
	}
/*
	public static void addPointcutPatternRule(List<PointcutPatternRule> pointcutPatternRuleList, String translet, String bean, String method, String text) {
		addPointcutPatternRule(pointcutPatternRuleList, translet, bean, method);
		addPointcutPatternRule(pointcutPatternRuleList, text);
	}

	public static PointcutPatternRule addPointcutPatternRule(List<PointcutPatternRule> pointcutPatternRuleList, String translet, String bean, String method) {
		PointcutPatternRule ppr = null;
		
		if(StringUtils.hasLength(translet) || StringUtils.hasLength(bean) || StringUtils.hasLength(method)) {
			ppr = PointcutPatternRule.newInstance(translet, bean, method);
			pointcutPatternRuleList.add(ppr);
		}
		
		return ppr;
	}

	public static void addPointcutPatternRule(List<PointcutPatternRule> pointcutPatternRuleList, String text) {
		if(StringUtils.hasText(text)) {
			Parameters targetParameters = new TargetParameters(text);
			addPointcutPatternRule(pointcutPatternRuleList, targetParameters);
		}
	}
*/

	private static List<PointcutPatternRule> addPointcutPatternRule(List<PointcutPatternRule> pointcutPatternRuleList, Parameters targetParameters) {
		String translet = targetParameters.getString(TargetParameters.translet);
		String bean = targetParameters.getString(TargetParameters.bean);
		String method = targetParameters.getString(TargetParameters.method);
		List<Parameters> excludeTargetParametersList = targetParameters.getParametersList(TargetParameters.excludeTargets);
		
		if(StringUtils.hasLength(translet) || StringUtils.hasLength(bean) || StringUtils.hasLength(method) || (excludeTargetParametersList != null && !excludeTargetParametersList.isEmpty())) {
			PointcutPatternRule pointcutPatternRule = PointcutPatternRule.newInstance(translet, bean, method);
			
			if(excludeTargetParametersList != null && !excludeTargetParametersList.isEmpty()) {
				for(Parameters excludeTargetParameters : excludeTargetParametersList) {
					addExcludePointcutPatternRule(pointcutPatternRule, excludeTargetParameters);
				}
			}
			
			pointcutPatternRuleList.add(pointcutPatternRule);
		}
		
		List<String> plusPatternStringList = targetParameters.getStringList(TargetParameters.pluses);
		List<String> minusPatternStringList = targetParameters.getStringList(TargetParameters.minuses);
		
		List<PointcutPatternRule> minusPointcutPatternRuleList = null;
		
		if(minusPatternStringList != null && !minusPatternStringList.isEmpty()) {
			minusPointcutPatternRuleList = new ArrayList<PointcutPatternRule>(minusPatternStringList.size());
			
			for(String patternString : minusPatternStringList) {
				PointcutPatternRule pointcutPatternRule = PointcutPatternRule.parsePatternString(patternString);
				minusPointcutPatternRuleList.add(pointcutPatternRule);
			}
		}
		
		if(plusPatternStringList != null && !plusPatternStringList.isEmpty()) {
			for(String patternString : plusPatternStringList) {
				PointcutPatternRule pointcutPatternRule = PointcutPatternRule.parsePatternString(patternString);
				
				if(minusPointcutPatternRuleList != null)
					pointcutPatternRule.setExcludePointcutPatternRuleList(minusPointcutPatternRuleList);
				
				pointcutPatternRuleList.add(pointcutPatternRule);
			}
		}
		
		return pointcutPatternRuleList;
	}

	private static void addExcludePointcutPatternRule(PointcutPatternRule pointcutPatternRule, Parameters excludeTargetParameters) {
		String translet = excludeTargetParameters.getString(TargetParameters.translet);
		String bean = excludeTargetParameters.getString(TargetParameters.bean);
		String method = excludeTargetParameters.getString(TargetParameters.method);

		if(StringUtils.hasLength(translet) || StringUtils.hasLength(bean) || StringUtils.hasLength(method)) {
			PointcutPatternRule ppr = PointcutPatternRule.newInstance(translet, bean, method);
			pointcutPatternRule.addExcludePointcutPatternRule(ppr);
		}
	}
/*
	private static void addExcludePointcutPatternRule(List<PointcutPatternRule> pointcutPatternRuleList, String translet, String bean, String method) {
		for(PointcutPatternRule pointcutPatternRule : pointcutPatternRuleList) {
			addExcludePointcutPatternRule(pointcutPatternRule, translet, bean, method);
		}
	}
	private static void addExcludePointcutPatternRule(PointcutPatternRule pointcutPatternRule, String translet, String bean, String method) {
		if(StringUtils.hasLength(translet) || StringUtils.hasLength(bean) || StringUtils.hasLength(method)) {
			PointcutPatternRule ppr = PointcutPatternRule.newInstance(translet, bean, method);
			pointcutPatternRule.addExcludePointcutPatternRule(ppr);
		}
	}
 */

}
