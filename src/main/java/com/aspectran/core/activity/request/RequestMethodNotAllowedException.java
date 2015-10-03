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
package com.aspectran.core.activity.request;

import com.aspectran.core.context.rule.type.RequestMethodType;

/**
 * Exception thrown when a request handler does not allow a specific request method.
 */
public class RequestMethodNotAllowedException extends RequestException {
	
	/** @serial */
	private static final long serialVersionUID = 4068498460127610368L;
	
	private RequestMethodType requestMethod;

	/**
	 * Instantiates a new request exception.
	 */
	public RequestMethodNotAllowedException() {
		super();
	}

	/**
	 * Constructor to create exception with a message.
	 * 
	 * @param msg A message to associate with the exception
	 */
	public RequestMethodNotAllowedException(String msg) {
		super(msg);
	}

	/**
	 * Constructor to create exception to wrap another exception.
	 * 
	 * @param cause The real cause of the exception
	 */
	public RequestMethodNotAllowedException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor to create exception to wrap another exception and pass a message.
	 * 
	 * @param msg The message
	 * @param cause The real cause of the exception
	 */
	public RequestMethodNotAllowedException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * Constructor to create exception to wrap another exception and pass a message.
	 * 
	 * @param msg The message
	 * @param cause The real cause of the exception
	 */
	public RequestMethodNotAllowedException(RequestMethodType requestMethod, String msg) {
		super(msg);
		this.requestMethod = requestMethod;
	}

	/**
	 * Constructor to create exception to wrap another exception and pass a message.
	 *
	 * @param requestMethod the request method
	 */
	public RequestMethodNotAllowedException(RequestMethodType requestMethod) {
		this(requestMethod, "Request method '" + requestMethod + "' not allowed");
	}
	
	/**
	 * Gets the request method.
	 *
	 * @return the request method
	 */
	public RequestMethodType getRequestMethod() {
		return requestMethod;
	}
	
}
