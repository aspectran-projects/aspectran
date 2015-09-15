/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.web.activity;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebActivityDefaultHandler {

	/** Default Servlet name used by Tomcat, Jetty, JBoss, and Glassfish */
	private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

	/** Default Servlet name used by Resin */
	private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";

	/** Default Servlet name used by WebLogic */
	private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";

	/** Default Servlet name used by WebSphere */
	private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";

	private ServletContext servletContext;

	private String defaultServletName;

	public WebActivityDefaultHandler() {
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		
		if(defaultServletName == null) {
			if(servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null) {
				defaultServletName = COMMON_DEFAULT_SERVLET_NAME;
			} else if(servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null) {
				defaultServletName = RESIN_DEFAULT_SERVLET_NAME;
			} else if(servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null) {
				defaultServletName = WEBLOGIC_DEFAULT_SERVLET_NAME;
			} else if(servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null) {
				defaultServletName = WEBSPHERE_DEFAULT_SERVLET_NAME;
			}
		}
	}

	public String getDefaultServletName() {
		return defaultServletName;
	}

	public void setDefaultServletName(String defaultServletName) {
		this.defaultServletName = defaultServletName;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = servletContext.getNamedDispatcher(defaultServletName);
		rd.forward(request, response);
	}

}
