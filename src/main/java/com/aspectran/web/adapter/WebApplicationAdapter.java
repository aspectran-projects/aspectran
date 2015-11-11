/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.aspectran.web.adapter;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import com.aspectran.core.adapter.AbstractApplicationAdapter;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.service.AspectranService;

/**
 * The Class WebApplicationAdapter.
 * 
 * @author Juho Jeong
 * @since 2011. 3. 13.
 */
public class WebApplicationAdapter extends AbstractApplicationAdapter implements ApplicationAdapter {
	
	/**
	 * Instantiates a new web application adapter.
	 *
	 * @param aspectranService the aspectran service
	 * @param servletContext the servlet context
	 */
	public WebApplicationAdapter(AspectranService aspectranService, ServletContext servletContext) {
		super(aspectranService, servletContext);
		super.setApplicationBasePath(servletContext.getRealPath("/"));
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.adapter.ApplicationAdapter#getAttribute(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T)((ServletContext)adaptee).getAttribute(name);
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.adapter.ApplicationAdapter#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String name, Object o) {
		((ServletContext)adaptee).setAttribute(name, o);
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.adapter.ApplicationAdapter#getAttributeNames()
	 */
	@SuppressWarnings("unchecked")
	public Enumeration<String> getAttributeNames() {
		return ((ServletContext)adaptee).getAttributeNames();
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.adapter.ApplicationAdapter#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name) {
		((ServletContext)adaptee).removeAttribute(name);
	}
	
}
