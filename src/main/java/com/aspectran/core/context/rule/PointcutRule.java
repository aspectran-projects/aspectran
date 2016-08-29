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

import java.util.ArrayList;
import java.util.List;

import com.aspectran.core.context.rule.type.PointcutType;
import com.aspectran.core.util.ToStringBuilder;
import com.aspectran.core.util.apon.Parameters;

/**
 * The Class PointcutRule.
 */
public class PointcutRule {
	
	private final PointcutType pointcutType;
	
	private List<PointcutPatternRule> pointcutPatternRuleList;
	
	private List<Parameters> plusPointcutParametersList;
	
	private List<Parameters> minusPointcutParametersList;
	
	private List<Parameters> includePointcutParametersList;
	
	private List<Parameters> excludePointcutParametersList;
	
	public PointcutRule(PointcutType pointcutType) {
		this.pointcutType = pointcutType;
	}
	
	public PointcutType getPointcutType() {
		return pointcutType;
	}

	public List<PointcutPatternRule> getPointcutPatternRuleList() {
		return pointcutPatternRuleList;
	}

//	public void setPointcutPatternRuleList(List<PointcutPatternRule> pointcutPatternRuleList) {
//		for(PointcutPatternRule ppr : pointcutPatternRuleList) {
//			ppr.setPointcutType(pointcutType);
//		}
//		this.pointcutPatternRuleList = pointcutPatternRuleList;
//	}
//	
//	public void addPointcutPatternRule(List<PointcutPatternRule> pointcutPatternRuleList) {
//		for(PointcutPatternRule ppr : pointcutPatternRuleList) {
//			ppr.setPointcutType(pointcutType);
//		}
//		if(this.pointcutPatternRuleList == null) {
//			this.pointcutPatternRuleList = pointcutPatternRuleList;
//		} else {
//			this.pointcutPatternRuleList.addAll(pointcutPatternRuleList);
//		}
//	}
	
	public void addPointcutPatternRule(PointcutPatternRule pointcutPatternRule, List<PointcutPatternRule> excludePointcutPatternRuleList) {
		pointcutPatternRule.setPointcutType(pointcutType);
		if(excludePointcutPatternRuleList != null) {
			pointcutPatternRule.setExcludePointcutPatternRuleList(excludePointcutPatternRuleList);
		}
		touchPointcutPatternRuleList().add(pointcutPatternRule);
	}
	
	public List<PointcutPatternRule> touchPointcutPatternRuleList() {
		if(pointcutPatternRuleList == null) {
			pointcutPatternRuleList = newPointcutPatternRuleList();
		}
		return pointcutPatternRuleList;
	}

	public List<PointcutPatternRule> newPointcutPatternRuleList() {
		return new ArrayList<PointcutPatternRule>();
	}
	
	public List<PointcutPatternRule> newPointcutPatternRuleList(int initialCapacity) {
		return new ArrayList<PointcutPatternRule>(initialCapacity);
	}

	public List<Parameters> getPlusPointcutParametersList() {
		return plusPointcutParametersList;
	}

	public void setPlusPointcutParametersList(List<Parameters> plusPointcutParametersList) {
		this.plusPointcutParametersList = plusPointcutParametersList;
	}

	public List<Parameters> getMinusPointcutParametersList() {
		return minusPointcutParametersList;
	}

	public void setMinusPointcutParametersList(List<Parameters> minusPointcutParametersList) {
		this.minusPointcutParametersList = minusPointcutParametersList;
	}

	public List<Parameters> getIncludePointcutParametersList() {
		return includePointcutParametersList;
	}

	public void setIncludePointcutParametersList(List<Parameters> includePointcutParametersList) {
		this.includePointcutParametersList = includePointcutParametersList;
	}

	public List<Parameters> getExcludePointcutParametersList() {
		return excludePointcutParametersList;
	}

	public void setExcludePointcutParametersList(List<Parameters> excludePointcutParametersList) {
		this.excludePointcutParametersList = excludePointcutParametersList;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder();
		tsb.append("pointcutType", pointcutType);
		tsb.append("pointcutPatternRule", pointcutPatternRuleList);
		return tsb.toString();
	}
	
	public static PointcutRule newInstance(String type) {
		PointcutType pointcutType = null;
		
		if(type != null) {
			pointcutType = PointcutType.resolve(type);
			if(pointcutType == null) {
				throw new IllegalArgumentException("Unknown pointcut type '" + type + "'. Pointcut type for Translet must be 'wildcard' or 'regexp'.");
			}
		}
		
		return new PointcutRule(pointcutType);
	}

	
	
	
	
	
	
	
	
	
	
	
	
//	
//	
//	
//	
//	
//	
//	public static PointcutRule newInstance(AspectRule aspectRule, String type, Parameters pointcutParameters) {
//		PointcutRule pointcutRule = null;
//
//		if(aspectRule.getSimpleTriggerParameters() != null || aspectRule.getCronTriggerParameters() != null) {
//			aspectRule.setAspectTargetType(AspectTargetType.SCHEDULER);
//		} else {
//			aspectRule.setAspectTargetType(AspectTargetType.TRANSLET);
//		}
//		
//		if(aspectRule.getAspectTargetType() == AspectTargetType.SCHEDULER) {
//			PointcutType pointcutType = null;
//			Parameters simpleTriggerParameters = null;
//			Parameters cronTriggerParameters = null;
//
//			if(pointcutParameters != null) {
//				simpleTriggerParameters = pointcutParameters.getParameters(PointcutTargetParameters.simpleTrigger);
//				cronTriggerParameters = pointcutParameters.getParameters(PointcutTargetParameters.cronTrigger);
//	
//				if(simpleTriggerParameters != null) {
//					pointcutType = PointcutType.SIMPLE_TRIGGER;
//				} else if(cronTriggerParameters != null) {
//					pointcutType = PointcutType.CRON_TRIGGER;
//				}
//			}
//			
//			if(pointcutType == null) {
//				pointcutType = PointcutType.resolve(type);
//				
//				if(pointcutType != PointcutType.SIMPLE_TRIGGER && pointcutType != PointcutType.CRON_TRIGGER)
//					throw new IllegalArgumentException("Unknown pointcut type '" + type + "'. Pointcut type for Scheduler must be 'simpleTrigger' or 'cronTrigger'.");
//			}
//			
//			if(pointcutType == PointcutType.SIMPLE_TRIGGER && simpleTriggerParameters == null)
//				throw new IllegalArgumentException("Not specified 'simpleTrigger'.");
//			else if(pointcutType == PointcutType.CRON_TRIGGER && cronTriggerParameters == null)
//				throw new IllegalArgumentException("Not specified 'cronTrigger'.");
//			
//			pointcutRule = new PointcutRule(pointcutType);
//			
//			if(simpleTriggerParameters != null) {
//				pointcutRule.setSimpleTriggerParameters(simpleTriggerParameters);
//			} else if(cronTriggerParameters != null) {
//				pointcutRule.setCronTriggerParameters(cronTriggerParameters);
//			}
//
//		} else {
//			if(pointcutParameters != null) {
//				PointcutType pointcutType = null;
//				
//				if(type == null)
//					type = pointcutParameters.getString(PointcutTargetParameters.type);
//				
//				if(type != null) {
//					pointcutType = PointcutType.resolve(type);
//					if(pointcutType == null)
//						throw new IllegalArgumentException("Unknown pointcut type '" + type + "'. Pointcut type for Translet must be 'wildcard' or 'regexp'.");
//				}
//				
//				pointcutRule = new PointcutRule(pointcutType);
//				
//				List<Parameters> targetParametersList = pointcutParameters.getParametersList(PointcutTargetParameters.targets);
//				if(targetParametersList != null) {
//					for(Parameters targetParameters : targetParametersList) {
//						addPointcutPatternRule(pointcutRule.touchPointcutPatternRuleList(), targetParameters);
//						pointcutRule.touchTargetParametersList().add(targetParameters);
//					}
//				}
//			}
//		}
//
//		return pointcutRule;
//	}

}
