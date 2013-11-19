package com.aspectran.web.context;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aspectran.core.context.AspectranContext;
import com.aspectran.core.context.builder.AspectranContextBuilder;
import com.aspectran.core.context.builder.xml.XmlAspectranContextBuilder;

public class AspectranContextLoader {

	private final Log log = LogFactory.getLog(AspectranContextLoader.class);

	public static final String ASPECTRAN_CONTEXT_LOADER_ATTRIBUTE = AspectranContextLoader.class.getName();
	
	public static final String CONTEXT_CONFIG_LOCATION_PARAM = "contextConfigLocation";
	
	private static final String DEFAULT_CONTEXT_CONFIG_LOCATION = "WEB-INF/aspectran/aspectran.xml";
	
	private ServletContext servletContext;
	
	private AspectranContext aspectranContext;
	
	public AspectranContextLoader(ServletContext servletContext, String contextConfigLocation) {
		this.servletContext = servletContext;
		
		if(contextConfigLocation == null)
			contextConfigLocation = DEFAULT_CONTEXT_CONFIG_LOCATION;

		loadAspectranContext(contextConfigLocation);
	}

	private void loadAspectranContext(String contextConfigLocation) {
		//servletContext.log("Loading AspectranContext: " + contextConfigLocation);
		
		log.info("loading AspectranContext [" + contextConfigLocation + "]");
		
		long startTime = System.currentTimeMillis();

		try {
			String applicationBasePath = servletContext.getRealPath("/");
			
			AspectranContextBuilder builder = new XmlAspectranContextBuilder(applicationBasePath);
			aspectranContext = builder.build(contextConfigLocation);
			
			long elapsedTime = System.currentTimeMillis() - startTime;
			log.info("AspectranContext: initialization completed in " + elapsedTime + " ms");
		} catch(RuntimeException ex) {
			log.error("AspectranContext initialization failed", ex);
			throw ex;
		} catch(Error err) {
			log.error("AspectranContext initialization failed", err);
			throw err;
		}
	}
	
	public AspectranContext getAspectranContext() {
		return aspectranContext;
	}
	
}
