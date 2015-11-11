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
package com.aspectran.core.context.bean.proxy;

import java.util.List;

import com.aspectran.core.context.aspect.AspectAdviceRuleRegistry;
import com.aspectran.core.context.rule.AspectRule;

/**
 * The Class RelevantAspectRuleHolder.
 */
public class RelevantAspectRuleHolder {

	private AspectAdviceRuleRegistry aspectAdviceRuleRegistry;
	
	private List<AspectRule> activityAspectRuleList;

	public AspectAdviceRuleRegistry getAspectAdviceRuleRegistry() {
		return aspectAdviceRuleRegistry;
	}

	public void setAspectAdviceRuleRegistry(
			AspectAdviceRuleRegistry aspectAdviceRuleRegistry) {
		this.aspectAdviceRuleRegistry = aspectAdviceRuleRegistry;
	}

	public List<AspectRule> getActivityAspectRuleList() {
		return activityAspectRuleList;
	}

	public void setActivityAspectRuleList(List<AspectRule> activityAspectRuleList) {
		this.activityAspectRuleList = activityAspectRuleList;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{aspectAdviceRuleRegistry=").append(aspectAdviceRuleRegistry);
		sb.append(", activityAspectRuleList=").append(activityAspectRuleList);
		sb.append("}");
		
		return sb.toString();
	}
	
}
