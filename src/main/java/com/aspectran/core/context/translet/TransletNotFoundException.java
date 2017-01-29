/**
 * Copyright 2008-2017 Juho Jeong
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
package com.aspectran.core.context.translet;

/**
 * This exception will be thrown when a translet not found.
 * 
 * <p>Created: 2008. 01. 07 AM 3:35:55</p>
 */
public class TransletNotFoundException extends TransletException {
	
	/** @serial */
	private static final long serialVersionUID = -5619283297296999361L;

	private String transletName;
	
	/**
	 * Constructor to create exception with a message.
	 *
	 * @param transletName the translet name
	 */
	public TransletNotFoundException(String transletName) {
		super("Translet is not found: " + transletName);
		this.transletName = transletName;
	}

	/**
	 * Constructor to create exception to wrap another exception.
	 * 
	 * @param cause the real cause of the exception
	 */
	public TransletNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor to create exception to wrap another exception and pass a
	 * message.
	 *
	 * @param transletName the translet name
	 * @param cause the real cause of the exception
	 */
	public TransletNotFoundException(String transletName, Throwable cause) {
		super("Translet is not found: " + transletName, cause);
		this.transletName = transletName;
	}
	
	public String getTransletName() {
		return transletName;
	}
	
}