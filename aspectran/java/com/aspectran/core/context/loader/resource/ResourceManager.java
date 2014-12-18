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
package com.aspectran.core.context.loader.resource;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


/**
 * <p>Created: 2014. 12. 18 오후 5:51:13</p>
 */
public class ResourceManager {

	private ResourceManager parent;
	
	private String resourceLocation;
	
	private Map<String, URL> resourceCache;
	
	private Map<String, Class<?>> classCache;
	
	public ResourceManager(String resourceLocation) {
		this(resourceLocation, null);
	}
	
	public ResourceManager(String resourceLocation, ResourceManager parent) {
		File f = new File(resourceLocation);
		
		if(!f.isDirectory())
			throw new InvalidResourceException("invalid resource directory name: " + resourceLocation);
		
		this.parent = parent;
		this.resourceLocation = resourceLocation;
	}
	
	public Enumeration<URL> getResources() {
		Enumeration localEnumeration = getBootstrapClassPath().getResources(paramString);
		return new Enumeration() {

			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public Object nextElement() {
				return null;
			}
			
		};
	}
	
	public Enumeration<URL> getResources(String name) {
		List<String> list = new ArrayList<String>();
		
		for(URI resource : resources) {
//			if(resource.startsWith(name)) {
//				list.add(resource);
//			}
		}
		
		
		
		return null;
	}
	
	public void release() {
		if(resourceCache != null) {
			resourceCache.clear();
			resourceCache = null;
		}

		if(classCache != null) {
			classCache.clear();
			classCache = null;
		}
	}
}
