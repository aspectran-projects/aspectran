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
package com.aspectran.core.context.rule.type;

import java.util.HashMap;
import java.util.Map;


/**
 * Resource Import File Type
 * 
 * <p>Created: 2015. 02. 22 오전 4:52:38</p>
 */
public final class ImportFileType extends Type {

	public static final ImportFileType XML;

	public static final ImportFileType APON;

	private static final Map<String, ImportFileType> types;
	
	static {
		XML = new ImportFileType("xml");
		APON = new ImportFileType("apon");

		types = new HashMap<String, ImportFileType>();
		types.put(XML.toString(), XML);
		types.put(APON.toString(), APON);
	}

	/**
	 * Instantiates a resource import type.
	 * 
	 * @param type the type
	 */
	private ImportFileType(String type) {
		super(type);
	}

	/**
	 * Returns a <code>ResourceImportType</code> with a value represented by the specified String.
	 * 
	 * @param type the type
	 * 
	 * @return the transform type
	 */
	public static ImportFileType valueOf(String type) {
		if(type == null)
			return null;
		
		return types.get(type);
	}
	
}
