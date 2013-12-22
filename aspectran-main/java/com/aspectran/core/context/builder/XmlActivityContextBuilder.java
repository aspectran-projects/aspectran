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
package com.aspectran.core.context.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.builder.xml.parser.AspectranNodeParser;
import com.aspectran.core.util.Assert;
import com.aspectran.core.util.ResourceUtils;

/**
 * XmlAspectranContextBuilder.
 * 
 * <p>Created: 2008. 06. 14 오후 8:53:29</p>
 */
public class XmlActivityContextBuilder extends AbstractActivityContextBuilder implements ActivityContextBuilder {
	
	private final Logger logger = LoggerFactory.getLogger(XmlActivityContextBuilder.class);
	
	private String contextConfigLocation;
	
	public XmlActivityContextBuilder(String contextConfigLocation) {
		this(null, contextConfigLocation);
	}

	public XmlActivityContextBuilder(String applicationBasePath, String contextConfigLocation) {
		super(applicationBasePath);
		
		Assert.notNull(contextConfigLocation, "contextConfigLocation must not be null");
		
		this.contextConfigLocation = contextConfigLocation;
	}

	public ActivityContext build() throws ActivityContextBuilderException {
		try {
			ImportResource importResource = new ImportResource(getClassLoader());

			if(contextConfigLocation.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
				String resource = contextConfigLocation.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
				
				importResource.setResource(resource);
			} else {
				importResource.setFile(getApplicationBasePath(), contextConfigLocation);
			}
			
			return build(importResource);
			
		} catch(Exception e) {
			logger.error("ActivityContext build failed", e);
			throw new ActivityContextBuilderException("ActivityContext build failed: " + e.toString(), e);
		}
	}
	
	private ActivityContext build(ImportResource importResource) throws Exception {
		AspectranNodeParser parser = new AspectranNodeParser(this);
		parser.parse(importResource);
		
		ActivityContext aspectranContext = makeActivityContext();
		
		return aspectranContext;
	}
	
}
