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

import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.ExceptionHandlingRule;
import com.aspectran.core.context.rule.SettingsAdviceRule;
import com.aspectran.core.context.rule.type.AspectAdviceType;

/**
 * The Class AspectAdviceRuleRegister.
 */
public class AspectAdviceRuleRegister {
	
	public static void register(AspectAdviceRuleRegistry aspectAdviceRuleRegistry, AspectRule aspectRule) {
		register(aspectAdviceRuleRegistry, aspectRule, null);
	}
	
	public static void register(AspectAdviceRuleRegistry aspectAdviceRuleRegistry, AspectRule aspectRule, AspectAdviceType excludeAspectAdviceType) {
		SettingsAdviceRule settingsAdviceRule = aspectRule.getSettingsAdviceRule();
		List<AspectAdviceRule> aspectAdviceRuleList = aspectRule.getAspectAdviceRuleList();
		ExceptionHandlingRule exceptionHandlingRule = aspectRule.getExceptionHandlingRule();
		
		if(settingsAdviceRule != null)
			aspectAdviceRuleRegistry.addAspectAdviceRule(settingsAdviceRule);
		
		if(aspectAdviceRuleList != null) {
			for(AspectAdviceRule aspectAdviceRule : aspectAdviceRuleList) {
				if(excludeAspectAdviceType == null || aspectAdviceRule.getAspectAdviceType() != excludeAspectAdviceType) {
					aspectAdviceRuleRegistry.addAspectAdviceRule(aspectAdviceRule);
				}
			}
		}
		
		if(exceptionHandlingRule != null) {
			aspectAdviceRuleRegistry.addExceptionHandlingRule(exceptionHandlingRule);
		}
		
		aspectAdviceRuleRegistry.increaseAspectRuleCount();
	}
	
}
