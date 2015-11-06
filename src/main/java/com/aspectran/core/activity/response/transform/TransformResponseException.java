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
package com.aspectran.core.activity.response.transform;

import com.aspectran.core.activity.response.ResponseException;
import com.aspectran.core.context.rule.TransformRule;

/**
 * The Class TransformResponseException.
 * 
 * <p>Created: 2008. 01. 07 오전 3:35:55</p>
 */
public class TransformResponseException extends ResponseException {
	
	/** @serial */
	private static final long serialVersionUID = -2902489274291058715L;
	
	private TransformRule transformRule;

	/**
	 * Simple constructor.
	 */
	public TransformResponseException() {
		super();
	}

	/**
	 * Constructor to create exception with a message.
	 * 
	 * @param msg A message to associate with the exception
	 */
	public TransformResponseException(String msg) {
		super(msg);
	}

	/**
	 * Constructor to create exception to wrap another exception.
	 * 
	 * @param cause The real cause of the exception
	 */
	public TransformResponseException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor to create exception to wrap another exception and pass a
	 * message.
	 * 
	 * @param msg The message
	 * @param cause The real cause of the exception
	 */
	public TransformResponseException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * Instantiates a new transform response exception.
	 *
	 * @param transformRule the transform rule
	 * @param cause the cause
	 */
	public TransformResponseException(TransformRule transformRule, Throwable cause) {
		super("Failed to transform " + transformRule, cause);
		this.transformRule = transformRule;
	}

	public TransformRule getTransformRule() {
		return transformRule;
	}

}
