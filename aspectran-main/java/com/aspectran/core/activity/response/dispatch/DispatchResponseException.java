/*
 *  Copyright (c) 2008 Jeong Ju Ho, All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aspectran.core.activity.response.dispatch;

import com.aspectran.core.activity.response.ResponseException;

/**
 * <p>Created: 2008. 01. 07 오전 3:35:55</p>
 */
public class DispatchResponseException extends ResponseException {
	
	/** @serial */
	static final long serialVersionUID = 7415015339312418407L;

	/**
	 * Simple constructor.
	 */
	public DispatchResponseException() {
	}

	/**
	 * Constructor to create exception with a message.
	 * 
	 * @param msg A message to associate with the exception
	 */
	public DispatchResponseException(String msg) {
		super(msg);
	}

	/**
	 * Constructor to create exception to wrap another exception.
	 * 
	 * @param cause The real cause of the exception
	 */
	public DispatchResponseException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor to create exception to wrap another exception and pass a
	 * message.
	 * 
	 * @param msg The message
	 * @param cause The real cause of the exception
	 */
	public DispatchResponseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
