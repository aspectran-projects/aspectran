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
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;


/**
 * <p>Created: 2014. 12. 18 오후 5:51:13</p>
 */
public class ResourceManager {

	private ResourceManager parent;
	
	private final String resourceLocation;
	
	private final Map<String, URL> resourcePool = new LinkedHashMap<String, URL>();
	
	private final Map<String, Class<?>> classPool = new HashMap<String, Class<?>>();
	
	public ResourceManager(String resourceLocation) {
		this(resourceLocation, null);
	}
	
	protected String getResourceLocation() {
		return resourceLocation;
	}

	public ResourceManager(String resourceLocation, ResourceManager parent) {
		File f = new File(resourceLocation);
		
		if(!f.isDirectory())
			throw new InvalidResourceException("invalid resource directory name: " + resourceLocation);
		
		this.parent = parent;
		this.resourceLocation = resourceLocation;
	}
	
	public Enumeration<URL> getResources() {
		final Enumeration<URL> parentResources = (parent != null) ? parent.getResources() : null;
		final Iterator<URL> currentResources = resourcePool.values().iterator();
		
		return new Enumeration<URL>() {
			public boolean hasMoreElements() {
				if(parentResources != null && parentResources.hasMoreElements())
					return true;
				
				return currentResources.hasNext();
			}

			public URL nextElement() {
				if(parentResources != null && parentResources.hasMoreElements())
					return parentResources.nextElement();
				
				return currentResources.next();
			}
		};
	}
	
	public Enumeration<URL> getResources(String name) {
		final String filterName = name;
		final Enumeration<URL> parentResources = (parent != null) ? parent.getResources(name) : null;
		final Iterator<Map.Entry<String, URL>> currentResources = resourcePool.entrySet().iterator();
		
		return new Enumeration<URL>() {
			private Map.Entry<String, URL> entry;
			
			public synchronized boolean hasMoreElements() {
				if(entry != null)
					return true;
				
				if(parentResources != null && parentResources.hasMoreElements())
					return true;

				while(currentResources.hasNext()) {
					Map.Entry<String, URL> entry2 = currentResources.next();
					
					if(entry2.getKey().startsWith(filterName)) {
						entry = entry2;
						return true;
					}					
				}
				
				return false;
			}

			public synchronized URL nextElement() {
				if(entry == null) {
					if(parentResources != null && parentResources.hasMoreElements())
						return parentResources.nextElement();
					
					if(!hasMoreElements())
						throw new NoSuchElementException();
				}

				URL url = entry.getValue();
				entry = null;

				return url;
			}
		};
	}
	
	public void release() {
		if(resourcePool != null) {
			resourcePool.clear();
		}

		if(classPool != null) {
			classPool.clear();
		}
	}
}
