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
package com.aspectran.core.var.type;

import java.util.HashMap;
import java.util.Map;


/**
 * Resource Import Type
 * 
 * <p>Created: 2008. 04. 25 오전 16:47:38</p>
 */
public final class ImportStreamType extends Type {

	public static final ImportStreamType RESOURCE;

	public static final ImportStreamType FILE;

	public static final ImportStreamType URL;
	
	private static final Map<String, ImportStreamType> types;
	
	static {
		RESOURCE = new ImportStreamType("resource");
		FILE = new ImportStreamType("file");
		URL = new ImportStreamType("url");

		types = new HashMap<String, ImportStreamType>();
		types.put(RESOURCE.toString(), RESOURCE);
		types.put(FILE.toString(), FILE);
		types.put(URL.toString(), URL);
	}

	/**
	 * Instantiates a resource import type.
	 * 
	 * @param type the type
	 */
	private ImportStreamType(String type) {
		super(type);
	}

	/**
	 * Returns a <code>ResourceImportType</code> with a value represented by the specified String.
	 * 
	 * @param type the type
	 * 
	 * @return the transform type
	 */
	public static ImportStreamType valueOf(String type) {
		if(type == null)
			return null;
		
		return types.get(type);
	}
	
}
