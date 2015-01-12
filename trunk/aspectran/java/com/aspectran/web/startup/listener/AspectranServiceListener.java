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
package com.aspectran.web.startup.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.service.AspectranService;
import com.aspectran.web.service.WebAspectranService;

public class AspectranServiceListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(AspectranServiceListener.class);

	public static final String ASPECTRAN_SERVICE_ATTRIBUTE = 
			AspectranServiceListener.class.getName() + ".ASPECTRAN_SERVICE";
	
	private AspectranService aspectranService;

	/**
	 * Initialize the translets root context.
	 * 
	 * @param event the event
	 */
	public void contextInitialized(ServletContextEvent event) {
		logger.info("Initializing AspectranServiceListener...");
		
		try {
			aspectranService = WebAspectranService.newInstance(event.getServletContext());
		} catch(Exception e) {
			logger.error("Failed to initialize AspectranServiceListener.", e);
		}
	}
	
	/**
	 * Close the translets root context.
	 * 
	 * @param event the event
	 */
	public void contextDestroyed(ServletContextEvent event) {
		boolean cleanlyDestoryed = aspectranService.dispose();
		
		if(cleanlyDestoryed)
			logger.info("Successfully destroyed AspectranServiceListener.");
		else
			logger.error("AspectranServiceListener were not destroyed cleanly.");

		logger.info("Do not terminate the server while the all scoped bean destroying.");
	}
	
}
