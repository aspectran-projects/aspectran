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
package com.aspectran.core.util;

import java.security.AccessControlException;

import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * Helpers for java.lang.System.
 */
public class SystemUtils {
	
	private static final Log log = LogFactory.getLog(SystemUtils.class);
	
	public static String getProperty(String name) {
		try {
			return System.getProperty(name);
		} catch (AccessControlException ex) {
			log.info(String.format(
					"Caught AccessControlException when accessing system property [%s]; its value will be returned [null]. Reason: %s",
					name, ex.getMessage()));
		}
		return null;
	}
	
}
