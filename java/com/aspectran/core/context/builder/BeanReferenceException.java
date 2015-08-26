/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.builder;

import java.util.List;

/**
 * This exception will be thrown when a translet request is failed.
 * 
 * <p>Created: 2008. 01. 07 오전 3:35:55</p>
 */
public class BeanReferenceException extends RuntimeException {
	
	/** @serial */
	static final long serialVersionUID = -244633940486989865L;
	
	private BeanReferenceInspector beanReferenceInspector;

	/**
	 * Simple constructor.
	 */
	public BeanReferenceException() {
	}

	/**
	 * Constructor to create exception with a message.
	 * 
	 * @param msg A message to associate with the exception
	 */
	public BeanReferenceException(List<String> unknownBeanIdList) {
		super(getMessage(unknownBeanIdList));
	}
	
	public BeanReferenceInspector getBeanReferenceInspector() {
		return beanReferenceInspector;
	}

	public void setBeanReferenceInspector(BeanReferenceInspector beanReferenceInspector) {
		this.beanReferenceInspector = beanReferenceInspector;
	}

	private static String getMessage(List<String> unknownBeanIdList) {
		StringBuilder sb = new StringBuilder();
		sb.append("Cannot resolve reference to bean. unknown bean id [");
		
		for(int i = 0; i < unknownBeanIdList.size(); i++) {
			if(i > 0)
				sb.append(",");
			sb.append(unknownBeanIdList.get(i));
		}
		
		sb.append("]");
		
		return sb.toString();
	}

}
