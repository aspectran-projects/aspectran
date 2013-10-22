/*
 *  Copyright (c) 2009 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.context;

/**
 * <p>Created: 2008. 04. 01 오후 10:25:35</p>
 */
public interface AspectranConstant {

	public static final String LINE_SEPARATOR = System.getProperty("line.separator");

	/** The Constant WILDCARD_CHAR. */
	public static final char TRANSLET_NAME_PATTERN_SEPARATOR = '*';

	/** The Constant NAMESPACE_SEPARATOR. */
	public static final char TRANSLET_NAME_SEPARATOR = '/';

	public static final char TRANSLET_NAME_EXTENTION_DELIMITER = '.';

	/** The Constant BEAN_ID_SEPARATOR. */
	public static final char BEAN_ID_SEPARATOR = '.';

	/** The Constant CONTENT_ID_SEPARATOR. */
	public static final char ACTION_ID_SEPARATOR = '.';
	
	public static final char POINTCUT_ACTION_DELIMITER = '@';
	
	public static final char POINTCUT_ACTION_BEAN_METHOD_DELIMITER = '^';

}
