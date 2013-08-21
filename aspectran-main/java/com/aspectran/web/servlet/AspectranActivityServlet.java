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
package com.aspectran.web.servlet;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aspectran.core.activity.AspectranActivity;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.AspectranContext;
import com.aspectran.core.context.translet.TransletNotFoundException;
import com.aspectran.web.activity.WebAspectranActivity;
import com.aspectran.web.adapter.WebApplicationAdapter;
import com.aspectran.web.context.AspectranContextLoader;

/**
 * Servlet implementation class for Servlet: Translets.
 */
public class AspectranActivityServlet extends HttpServlet implements Servlet {

	/** @serial */
	static final long serialVersionUID = 6659683668233267847L;

	private static final Log log = LogFactory.getLog(AspectranActivityServlet.class);
	
	protected AspectranContext aspectranContext;
	
	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	/**
	 * Instantiates a new action servlet.
	 */
	public AspectranActivityServlet() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		try {
			AspectranContextLoader aspectranContextLoader = (AspectranContextLoader)getServletContext().getAttribute(AspectranContextLoader.ASPECTRAN_CONTEXT_LOADER_ATTRIBUTE);

			if(aspectranContextLoader == null) {
				aspectranContextLoader = new AspectranContextLoader(getServletContext());
				aspectranContext = aspectranContextLoader.getAspectranContext();
			}
			
			ApplicationAdapter applicationAdapter = new WebApplicationAdapter(getServletContext());
			aspectranContext.setApplicationAdapter(applicationAdapter);
		} catch(Exception e) {
			log.error("Unable to initialize WebAspectranActivityServlet.", e);
			throw new UnavailableException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			String requestUri = req.getRequestURI();

			AspectranActivity activity = new WebAspectranActivity(aspectranContext, req, res);
			activity.run(requestUri);
			
		} catch(TransletNotFoundException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			log.error(e.getMessage(), e);
		} catch(Exception e) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			log.error(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		log.info("Closing WebAspectranActivityServlet. AspectranContext " + aspectranContext);
		
		super.destroy();
		
		if(aspectranContext != null) {
			aspectranContext.destroy();
			aspectranContext = null;
		}
	}
}