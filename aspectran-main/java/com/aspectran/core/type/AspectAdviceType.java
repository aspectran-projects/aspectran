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
package com.aspectran.core.type;

import java.util.HashMap;
import java.util.Map;


public final class AspectAdviceType extends Type {

	public static final AspectAdviceType DEFAULT;

	public static final AspectAdviceType BEFORE;
	
	public static final AspectAdviceType AFTER;
	
	public static final AspectAdviceType AROUND;
	
	public static final AspectAdviceType FINALLY;
	
	public static final AspectAdviceType EXCPETION_RAIZED;
	
	private static final Map<String, AspectAdviceType> types;
	
	static {
		DEFAULT = new AspectAdviceType("default");
		BEFORE = new AspectAdviceType("before");
		AFTER = new AspectAdviceType("after");
		AROUND = new AspectAdviceType("around");
		FINALLY = new AspectAdviceType("finally");
		EXCPETION_RAIZED = new AspectAdviceType("exceptionRaized");

		types = new HashMap<String, AspectAdviceType>();
		types.put(DEFAULT.toString(), DEFAULT);
		types.put(BEFORE.toString(), BEFORE);
		types.put(AFTER.toString(), AFTER);
		types.put(AROUND.toString(), AROUND);
		types.put(FINALLY.toString(), FINALLY);
		types.put(EXCPETION_RAIZED.toString(), EXCPETION_RAIZED);
	}

	private AspectAdviceType(String type) {
		super(type);
	}

	/**
	 * Returns a <code>AdviceType</code> with a value represented by the specified String.
	 * 
	 * @param type the type
	 * 
	 * @return the content type
	 */
	public static AspectAdviceType valueOf(String type) {
		if(type == null)
			return null;
		
		return types.get(type);
	}
}
