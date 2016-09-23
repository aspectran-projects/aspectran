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
package com.aspectran.core.context.aspect;

import java.util.List;
import java.util.Set;

import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.aspect.pointcut.Pointcut;
import com.aspectran.core.context.bean.BeanRuleRegistry;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.PointcutPatternRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.type.JoinpointType;
import com.aspectran.core.context.translet.TransletRuleRegistry;
import com.aspectran.core.util.BeanDescriptor;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class AspectAdviceRulePreRegister.
 */
public class AspectAdviceRulePreRegister {
	
	private final Log log = LogFactory.getLog(AspectAdviceRulePreRegister.class);
	
	private AspectRuleRegistry aspectRuleRegistry;
	
	public AspectAdviceRulePreRegister(AspectRuleRegistry aspectRuleRegistry) {
		this.aspectRuleRegistry = aspectRuleRegistry;
		
		for (AspectRule aspectRule : aspectRuleRegistry.getAspectRules()) {
			JoinpointType joinpointType = aspectRule.getJoinpointType();

			if (joinpointType == JoinpointType.BEAN) {
				aspectRule.setBeanRelevanted(true);
			} else if (joinpointType == JoinpointType.SESSION) {
				aspectRule.setBeanRelevanted(false);
			} else {
				Pointcut pointcut = aspectRule.getPointcut();

				if (pointcut == null) {
					aspectRule.setBeanRelevanted(false);
				} else {
					List<PointcutPatternRule> pointcutPatternRuleList = pointcut.getPointcutPatternRuleList();
					boolean beanRelevanted = false;
					
					for (PointcutPatternRule ppr : pointcutPatternRuleList) {
						if (ppr.getBeanIdPattern() != null || ppr.getClassNamePattern() != null || ppr.getMethodNamePattern() != null) {
							beanRelevanted = true;
							break;
						}
					}
					
					aspectRule.setBeanRelevanted(beanRelevanted);
				}
			}
			
			if (log.isTraceEnabled()) {
				log.trace("preregistered aspectRule " + aspectRule);
			}
		}
	}
	
	public void register(BeanRuleRegistry beanRuleRegistry) {
		for (BeanRule beanRule : beanRuleRegistry.getConfigBeanRuleMap().values()) {
			if (beanRule.isProxiable()) {
				determineProxyBean(beanRule);
			}
		}
		for (BeanRule beanRule : beanRuleRegistry.getIdBasedBeanRuleMap().values()) {
			if (beanRule.isProxiable()) {
				determineProxyBean(beanRule);
			}
		}
		for (Set<BeanRule> set : beanRuleRegistry.getTypeBasedBeanRuleMap().values()) {
			for (BeanRule beanRule : set) {
				if (beanRule.isProxiable()) {
					determineProxyBean(beanRule);
				}
			}
		}
	}
	
	private void determineProxyBean(BeanRule beanRule) {
		for (AspectRule aspectRule : aspectRuleRegistry.getAspectRules()) {
			if (aspectRule.isBeanRelevanted()) {
				Pointcut pointcut = aspectRule.getPointcut();
				
				if (pointcut != null && pointcut.isExistsBeanMethodNamePattern()) {
					if (existsMatchedBean(pointcut, beanRule)) {
						beanRule.setProxied(true);
	
						if (log.isTraceEnabled()) {
							log.trace("applied aspectRule " + aspectRule + " to beanRule " + beanRule);
						}
	
						break;
					}
				} else {
					if (pointcut == null || existsMatchedBean(pointcut, beanRule.getId(), beanRule.getTargetBeanClassName())) {
						beanRule.setProxied(true);
	
						if (log.isTraceEnabled()) {
							log.trace("applied aspectRule " + aspectRule + " to beanRule " + beanRule);
						}
	
						break;
					}
				}
			}
		}
	}
	
	public void register(TransletRuleRegistry transletRuleRegistry) {
		for (TransletRule transletRule : transletRuleRegistry.getTransletRules()) {
			if (transletRule.getNameTokens() == null) {
				register(transletRule);
			}
		}
	}
	
	private void register(TransletRule transletRule) {
		for (AspectRule aspectRule : aspectRuleRegistry.getAspectRules()) {
			JoinpointType joinpointType = aspectRule.getJoinpointType();
			Pointcut pointcut = aspectRule.getPointcut();

			if (!aspectRule.isBeanRelevanted() && joinpointType != JoinpointType.SESSION) {
				if (pointcut == null || pointcut.matches(transletRule.getName())) {
					// register to the translet scope
					transletRule.touchAspectAdviceRuleRegistry().register(aspectRule);
					
					if (log.isTraceEnabled()) {
						log.trace("applied aspectRule " + aspectRule + " to transletRule " + transletRule);
					}
				}
			}

//			if (pointcut != null) {
//				countMatchedTranslet(pointcut, transletRule.getName());
//			}
		}
	}

//	private void countMatchedTranslet(Pointcut pointcut, String transletName) {
//		List<PointcutPatternRule> pointcutPatternRuleList = pointcut.getPointcutPatternRuleList();
//
//		if (pointcutPatternRuleList != null) {
//			for (PointcutPatternRule ppr : pointcutPatternRuleList) {
//				if (existsMatchedTranslet(pointcut, ppr, transletName)) {
//					ppr.increaseMatchedTransletCount();
//				}
//			}
//		}
//	}
//
//	private boolean existsMatchedTranslet(Pointcut pointcut, PointcutPatternRule pointcutPatternRule, String transletName) {
//		boolean matched = true;
//
//		if (pointcutPatternRule.getTransletNamePattern() != null) {
//			matched = pointcut.patternMatches(pointcutPatternRule.getTransletNamePattern(), transletName, ActivityContext.TRANSLET_NAME_SEPARATOR_CHAR);
//		}
//
//		return matched;
//	}
	
	private boolean existsMatchedBean(Pointcut pointcut, String beanId, String className) {
		List<PointcutPatternRule> pointcutPatternRuleList = pointcut.getPointcutPatternRuleList();
		
		if (pointcutPatternRuleList != null) {
			for (PointcutPatternRule ppr : pointcutPatternRuleList) {
				if (existsBean(pointcut, ppr, beanId, className, null)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean existsMatchedBean(Pointcut pointcut, BeanRule beanRule) {
		List<PointcutPatternRule> pointcutPatternRuleList = pointcut.getPointcutPatternRuleList();
		
		if (pointcutPatternRuleList != null) {
			BeanDescriptor cd = BeanDescriptor.getInstance(beanRule.getTargetBeanClass());
			
			String beanId = beanRule.getId();
			String className = beanRule.getTargetBeanClassName();
			String[] methodNames = cd.getDistinctMethodNames();

			for (PointcutPatternRule ppr : pointcutPatternRuleList) {
				if (existsBean(pointcut, ppr, beanId, className, methodNames)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean existsBean(Pointcut pointcut, PointcutPatternRule pointcutPatternRule, String beanId, String className, String[] methodNames) {
		boolean matched = true;
		
		if (beanId != null && pointcutPatternRule.getBeanIdPattern() != null) {
			matched = pointcut.patternMatches(pointcutPatternRule.getBeanIdPattern(), beanId, ActivityContext.ID_SEPARATOR_CHAR);
			if (matched) {
				pointcutPatternRule.increaseMatchedBeanCount();
			}
		}
		
		if (matched && className != null && pointcutPatternRule.getClassNamePattern() != null) {
			matched = pointcut.patternMatches(pointcutPatternRule.getClassNamePattern(), className, ActivityContext.ID_SEPARATOR_CHAR);
			if (matched) {
				pointcutPatternRule.increaseMatchedClassCount();
			}
		}

		if (matched && methodNames != null && pointcutPatternRule.getMethodNamePattern() != null) {
			matched = false;
			for (String methodName : methodNames) {
				boolean matched2 = pointcut.patternMatches(pointcutPatternRule.getMethodNamePattern(), methodName);
				if (matched2) {
					matched = true;
					pointcutPatternRule.increaseMatchedMethodCount();
				}
			}
		}
		
		return matched;
	}
	
}
