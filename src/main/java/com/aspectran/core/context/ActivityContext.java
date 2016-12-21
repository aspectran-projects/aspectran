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
package com.aspectran.core.context;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.aspect.AspectRuleRegistry;
import com.aspectran.core.context.bean.BeanRegistry;
import com.aspectran.core.context.env.ContextEnvironment;
import com.aspectran.core.context.message.MessageSource;
import com.aspectran.core.context.schedule.ScheduleRuleRegistry;
import com.aspectran.core.context.template.TemplateProcessor;
import com.aspectran.core.context.translet.TransletRuleRegistry;
import com.aspectran.core.service.AspectranService;

/**
 * Central interface to provide configuration for performing various activities.
 *
 * <p>Created: 2008. 06. 09 PM 2:12:40</p>
 */
public interface ActivityContext extends MessageSource {

	String ID_SEPARATOR = ".";

	char ID_SEPARATOR_CHAR = '.';

	char TRANSLET_NAME_SEPARATOR_CHAR = '/';

	String LINE_SEPARATOR = "\n";

	String DEFAULT_ENCODING = "UTF-8";

	String MESSAGE_SOURCE_BEAN_ID = "messageSource";

	/**
	 * Gets the context environment.
	 *
	 * @return the context environment
	 */
	ContextEnvironment getContextEnvironment();

	/**
	 * Gets the class loader.
	 *
	 * @return the class loader
	 */
	ClassLoader getClassLoader();
	
	/**
	 * Gets the application adapter.
	 *
	 * @return the application adapter
	 */
	ApplicationAdapter getApplicationAdapter();

	/**
	 * Returns the Aspectran Service that created the current ActivityContext.
	 *
	 * @return the origin aspectran service
	 */
	AspectranService getOriginAspectranService();

	/**
	 * Sets the Aspectran Service that created the current ActivityContext.
	 * It is set only once, just after the ActivityContext is created.
	 *
	 * @param originAspectranService the origin aspectran service
	 * @throws AspectranCheckedException if it is already set, it throws an exception
	 */
	void setOriginAspectranService(AspectranService originAspectranService) throws AspectranCheckedException;

	/**
	 * Gets the aspect rule registry.
	 *
	 * @return the aspect rule registry
	 */
	AspectRuleRegistry getAspectRuleRegistry();

	/**
	 * Gets the bean registry.
	 *
	 * @return the bean registry
	 */
	BeanRegistry getBeanRegistry();

	/**
	 * Gets the schedule rule registry.
	 *
	 * @return the schedule rule registry
	 */
	ScheduleRuleRegistry getScheduleRuleRegistry();

	/**
	 * Gets the template processor.
	 *
	 * @return the template processor
	 */
	TemplateProcessor getTemplateProcessor();

	/**
	 * Gets the translet rule registry.
	 *
	 * @return the translet rule registry
	 */
	TransletRuleRegistry getTransletRuleRegistry();

	/**
	 * Gets the message source.
	 *
	 * @return the message source
	 */
	MessageSource getMessageSource();

	/**
	 * Gets the default activity.
	 *
	 * @return the default activity
	 */
	Activity getDefaultActivity();

	/**
	 * Gets the current activity.
	 *
	 * @return the current activity
	 */
	Activity getCurrentActivity();
	
	/**
	 * Sets the current activity.
	 *
	 * @param activity the new current activity
	 */
	void setCurrentActivity(Activity activity);
	
	/**
	 * Removes the current activity.
	 */
	void removeCurrentActivity();
	
	/**
	 * Destroy the aspectran context. 
	 */
	void destroy();

}
