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
 * The Class ItemType.
 * 
 * <p>Created: 2008. 03. 29 PM 3:47:00</p>
 */
public final class ItemType extends Type {
	
	public static final ItemType SINGULAR;

	public static final ItemType ARRAY;
	
	public static final ItemType LIST;
	
	public static final ItemType MAP;

	public static final ItemType SET;
	
	public static final ItemType PROPERTIES;
	
	private static final Map<String, ItemType> types;
	
	static {
		SINGULAR = new ItemType("singular");
		ARRAY = new ItemType("array");
		LIST = new ItemType("list");
		MAP = new ItemType("map");
		SET = new ItemType("set");
		PROPERTIES = new ItemType("properties");

		types = new HashMap<String, ItemType>();
		types.put(SINGULAR.toString(), SINGULAR);
		types.put(ARRAY.toString(), ARRAY);
		types.put(LIST.toString(), LIST);
		types.put(MAP.toString(), MAP);
		types.put(SET.toString(), SET);
		types.put(PROPERTIES.toString(), PROPERTIES);
	}

	/**
	 * Instantiates a new ItemType.
	 * 
	 * @param type the type
	 */
	private ItemType(String type) {
		super(type);
	}

	/**
	 * Returns a <code>ItemType</code> with a value represented by the specified String.
	 * 
	 * @param type the type
	 * 
	 * @return the item type
	 */
	public static ItemType valueOf(String type) {
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
