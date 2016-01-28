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
package com.aspectran.core.context.template;

import com.aspectran.core.context.rule.TemplateRule;

/**
 * This class is the basic exception that gets thrown from the template pacakge.
 * 
 * <p>Created: 2016. 01. 15.</p>
 */
public class TemplateProcessorException extends TemplateRuleException {

	/** @serial */
	private static final long serialVersionUID = -1495281620922964138L;

	/**
	 * Instantiates a new TemplateProcessorException.
	 *
	 * @param templateRule the template rule
	 * @param msg the detail message
	 */
	public TemplateProcessorException(TemplateRule templateRule, String msg) {
		super(templateRule, msg);
	}

	/**
	 * Instantiates a new TemplateProcessorException.
	 *
	 * @param templateRule the template rule
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public TemplateProcessorException(TemplateRule templateRule, String msg, Throwable cause) {
		super(templateRule, msg, cause);
	}

}
