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

import java.util.HashMap;
import java.util.Map;

/**
 * The Class ScopeType.
 * 
 * <p>Created: 2008. 12. 22 PM 2:48:00</p>
 */
public final class ScopeType extends Type {
	
	public static final ScopeType SINGLETON;
	
	public static final ScopeType PROTOTYPE;
	
	public static final ScopeType REQUEST;
	
	public static final ScopeType SESSION;

	public static final ScopeType APPLICATION;
	
	private static final Map<String, ScopeType> types;
	
	static {
		SINGLETON = new ScopeType("singleton");
		PROTOTYPE = new ScopeType("prototype");
		REQUEST = new ScopeType("request");
		SESSION = new ScopeType("session");
		APPLICATION = new ScopeType("application");

		types = new HashMap<String, ScopeType>();
		types.put(SINGLETON.toString(), SINGLETON);
		types.put(PROTOTYPE.toString(), PROTOTYPE);
		types.put(REQUEST.toString(), REQUEST);
		types.put(SESSION.toString(), SESSION);
		types.put(APPLICATION.toString(), APPLICATION);
	}

	/**
	 * Instantiates a new ScopeType.
	 * 
	 * @param type the type
	 */
	private ScopeType(String type) {
		super(type);
	}

	/**
	 * Returns a <code>ScopeType</code> with a value represented by the specified String.
	 * 
	 * @param type the type
	 * 
	 * @return the scope type
	 */
	public static ScopeType valueOf(String type) {
		return types.get(type);
	}
	
	/**
	 * Returns an array containing the constants of this type, in the order they are declared.
	 *
	 * @return the string[]
	 */
	public static String[] values() {
		return types.keySet().toArray(new String[types.size()]);
	}

}
