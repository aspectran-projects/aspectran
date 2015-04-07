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
package com.aspectran.core.activity.aspect;

/**
 * This exception will be thrown when a translet process is failed..
 * 
 * <p>Created: 2008. 01. 07 오전 3:35:55</p>
 */
public class AspectAdviceException extends Exception {
	
	/** @serial */
	private static final long serialVersionUID = 6813516429436576091L;

	/**
	 * Simple constructor.
	 */
	public AspectAdviceException() {
	}

	/**
	 * Constructor to create exception with a message.
	 * 
	 * @param msg A message to associate with the exception
	 */
	public AspectAdviceException(String msg) {
		super(msg);
	}

	/**
	 * Constructor to create exception to wrap another exception.
	 * 
	 * @param cause The real cause of the exception
	 */
	public AspectAdviceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor to create exception to wrap another exception and pass a
	 * message.
	 * 
	 * @param msg The message
	 * @param cause The real cause of the exception
	 */
	public AspectAdviceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
