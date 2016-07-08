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
 * The Class MethodType.
 * 
 * <p>Created: 2008. 03. 26 AM 12:58:38</p>
 */
public enum MethodType {

	ALL,
	
	/**
	 * retrieves a representation of a resource without side-effects
	 * (nothing changes on the server).
	 */
	GET,

	/**
	 * creates a resource.
	 */
	POST,
	
	/**
	 * (completely) replaces an existing resource.
	 */
	PUT,

	/**
	 * partial modification of a resource.
	 */
	PATCH,

	/**
	 * deletes a resource.
	 */
	DELETE,
	
	/**
	 * retrieves just the resource meta-information (headers)
	 * i.e. same as GET but without the response body - also without side-effects.
	 */
	HEAD,

	/**
	 * returns the actions supported for specified the resource - also without side-effects.
	 */
	OPTIONS,

	TRACE;

	private static final int MAX_COUNT = 9;

	public boolean containsTo(MethodType[] types) {
		for(MethodType type : types) {
			if(equals(type) || ALL.equals(type))
				return true;
		}
		return false;
	}

	/**
	 * Returns a <code>MethodType</code> with a value represented by the specified String.
	 *
	 * @param alias the specified String
	 * @return the method type
	 */
	public static MethodType lookup(String alias) {
		if(alias != null) {
			for(MethodType type : values()) {
				if(type.name().equals(alias.toUpperCase()))
					return type;
			}
		}
		return null;
	}


	public static MethodType[] parse(String value) {
		MethodType[] types = new MethodType[MAX_COUNT];
		int count = 0;

		StringTokenizer st = new StringTokenizer(value, ", ");
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if(!token.isEmpty()) {
				MethodType type = lookup(token);
				if(type != null) {
					int ord = type.ordinal();
					if(ord == 0) {
						return new MethodType[] { ALL };
					} else {
						if(types[ord] == null) {
							types[ord] = type;
							count++;
						}
					}
				}
			}
		}

		if(count == 0)
			return null;

		MethodType[] orderedTypes = new MethodType[count];

		for(int i = 1, seq = 0; i < MAX_COUNT; i++) {
			if(types[i] != null) {
				orderedTypes[seq++] = types[i];
			}
		}

		return orderedTypes;
	}

	public static String stringify(MethodType[] types) {
		if(types == null || types.length == 0)
			return null;
		
		StringBuilder sb = new StringBuilder(types.length * 7);
		for(int i = 0; i < types.length; i++) {
			if(i > 0)
				sb.append(",");
			sb.append(types[i]);
		}
		return sb.toString();
	}

}