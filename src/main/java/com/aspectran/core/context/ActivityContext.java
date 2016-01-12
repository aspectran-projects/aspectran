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
package com.aspectran.core.context;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.aspect.AspectRuleRegistry;
import com.aspectran.core.context.bean.ContextBeanRegistry;
import com.aspectran.core.context.template.TemplateRuleRegistry;
import com.aspectran.core.context.translet.TransletRuleRegistry;

/**
 * The Class ActivityContext.
 * 
 * <p>Created: 2008. 06. 09 오후 2:12:40</p>
 */
public class ActivityContext {
	
	private static ThreadLocal<Activity> currentActivityHolder = new ThreadLocal<Activity>();

	private ApplicationAdapter applicationAdapter;
	
	private AspectRuleRegistry aspectRuleRegistry;
	
	private ContextBeanRegistry contextBeanRegistry;

	private TransletRuleRegistry transletRuleRegistry;
	
	private TemplateRuleRegistry templateRuleRegistry;
	
	/**
	 * Instantiates a new ActivityContext.
	 *
	 * @param applicationAdapter the application adapter
	 */
	public ActivityContext(ApplicationAdapter applicationAdapter) {
		this.applicationAdapter = applicationAdapter;
	}
	
	/**
	 * Gets the application adapter.
	 *
	 * @return the application adapter
	 */
	public ApplicationAdapter getApplicationAdapter() {
		return applicationAdapter;
	}

	public AspectRuleRegistry getAspectRuleRegistry() {
		return aspectRuleRegistry;
	}

	public void setAspectRuleRegistry(AspectRuleRegistry aspectRuleRegistry) {
		this.aspectRuleRegistry = aspectRuleRegistry;
	}

	/**
	 * Gets the bean registry.
	 *
	 * @return the bean registry
	 */
	public ContextBeanRegistry getContextBeanRegistry() {
		return contextBeanRegistry;
	}

	/**
	 * Sets the bean registry.
	 *
	 * @param contextBeanRegistry the new bean registry
	 */
	public void setContextBeanRegistry(ContextBeanRegistry contextBeanRegistry) {
		this.contextBeanRegistry = contextBeanRegistry;
	}

	/**
	 * Gets the translet rule registry.
	 *
	 * @return the translet rule registry
	 */
	public TransletRuleRegistry getTransletRuleRegistry() {
		return transletRuleRegistry;
	}

	/**
	 * Sets the translet rule registry.
	 *
	 * @param transletRuleRegistry the new translet rule registry
	 */
	public void setTransletRuleRegistry(TransletRuleRegistry transletRuleRegistry) {
		this.transletRuleRegistry = transletRuleRegistry;
	}
	
	/**
	 * Gets the template rule registry.
	 *
	 * @return the template rule registry
	 */
	public TemplateRuleRegistry getTemplateRuleRegistry() {
		return templateRuleRegistry;
	}

	/**
	 * Sets the template rule registry.
	 *
	 * @param templateRuleRegistry the new template rule registry
	 */
	public void setTemplateRuleRegistry(TemplateRuleRegistry templateRuleRegistry) {
		this.templateRuleRegistry = templateRuleRegistry;
	}

	/**
	 * Gets the current activity.
	 *
	 * @return the current activity
	 */
	public Activity getCurrentActivity() {
		return currentActivityHolder.get();
	}
	
	/**
	 * Sets the current activity.
	 *
	 * @param activity the new current activity
	 */
	public void setCurrentActivity(Activity activity) {
		if(currentActivityHolder.get() == null)
			currentActivityHolder.set(activity);
	}
	
	/**
	 * Removes the current activity.
	 */
	public void removeCurrentActivity() {
		currentActivityHolder.remove();
	}
	
	/**
	 * Destroy the aspectran context. 
	 */
	public void destroy() {
		if(aspectRuleRegistry != null) {
			aspectRuleRegistry.destroy();
			aspectRuleRegistry = null;
		}
		if(contextBeanRegistry != null) {
			contextBeanRegistry.destroy();
			contextBeanRegistry = null;
		}
		if(transletRuleRegistry != null) {
			transletRuleRegistry.destroy();
			transletRuleRegistry = null;
		}
		if(templateRuleRegistry != null) {
			templateRuleRegistry.destroy();
			templateRuleRegistry = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{applicationAdapter=").append(applicationAdapter);
		sb.append(", aspectRuleRegistry=").append(aspectRuleRegistry);
		sb.append(", beanRegistry=").append(contextBeanRegistry);
		sb.append(", transletRuleRegistry=").append(transletRuleRegistry);
		sb.append(", templateRuleRegistry=").append(templateRuleRegistry);
		sb.append("}");
		
		return sb.toString();
	}
	
}
