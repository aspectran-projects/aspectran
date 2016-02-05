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
package com.aspectran.core.context.rule.type;

import java.util.StringTokenizer;

/**
 * The Class RequestMethodType.
 * 
 * <p>Created: 2008. 03. 26 AM 12:58:38</p>
 */
public enum RequestMethodType {

	GET,
	POST,
	PUT,
	PATCH,
	DELETE,
	HEAD,
	OPTIONS,
	TRACE;

	private static final int MAX_COUNT = 8;

	public boolean containsTo(RequestMethodType[] types) {
		for(RequestMethodType type : types) {
			if(equals(type))
				return true;
		}
		return false;
	}


	/**
	 * Returns a <code>RequestMethodType</code> with a value represented by the specified String.
	 *
	 * @param alias the specified String
	 * @return the pointcut type
	 */
	public static RequestMethodType lookup(String alias) {
		return valueOf(RequestMethodType.class, alias.toUpperCase());
	}

	public static RequestMethodType[] parse(String value) {
		RequestMethodType[] types = new RequestMethodType[MAX_COUNT];
		int count = 0;

		StringTokenizer st = new StringTokenizer(value, ",");
		while(st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			if(!token.isEmpty()) {
				RequestMethodType type = lookup(token);
				int ord = type.ordinal();
				if(types[ord] != null) {
					types[ord] = type;
					count++;
				}
			}
		}

		if(count == 0)
			return null;

		RequestMethodType[] orderedTypes = new RequestMethodType[count];
		int seq = 0;

		for(int i = 0; i < MAX_COUNT; i++) {
			if(types[i] != null) {
				orderedTypes[seq++] = types[i];
			}
		}

		return orderedTypes;
	}

}