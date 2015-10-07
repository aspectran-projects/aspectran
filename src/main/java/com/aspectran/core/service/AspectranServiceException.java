/*******************************************************************************
 * Copyright (c) 2008 Jeong Ju Ho.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Jeong Ju Ho - initial API and implementation
 ******************************************************************************/
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
package com.aspectran.core.service;

import com.aspectran.core.context.ActivityContextException;

/**
 * The Class AspectranServiceException.
 * 
 * <p>Created: 2008. 01. 07 오전 3:35:55</p>
 * 
 * @author Juho Jeong
 */
public class AspectranServiceException extends ActivityContextException {

	/** @serial */
	private static final long serialVersionUID = 3684447750947343719L;

	/**
	 * Simple constructor
	 */
	public AspectranServiceException() {
		super();
	}

	/**
	 * Constructor to create exception with a message
	 * 
	 * @param msg A message to associate with the exception
	 */
	public AspectranServiceException(String msg) {
		super(msg);
	}

	/**
	 * Constructor to create exception to wrap another exception
	 * 
	 * @param cause The real cause of the exception
	 */
	public AspectranServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor to create exception to wrap another exception and pass a message
	 * 
	 * @param msg The message
	 * @param cause The real cause of the exception
	 */
	public AspectranServiceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
