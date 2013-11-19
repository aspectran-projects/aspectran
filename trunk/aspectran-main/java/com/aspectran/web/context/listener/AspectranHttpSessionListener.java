package com.aspectran.web.context.listener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.context.AspectranContext;
import com.aspectran.core.context.bean.scope.SessionScope;
import com.aspectran.web.context.AspectranContextLoader;

public class AspectranHttpSessionListener implements HttpSessionListener {

	private final Logger logger = LoggerFactory.getLogger(AspectranHttpSessionListener.class);
	
	public void sessionCreated(HttpSessionEvent se) {
		if(logger.isDebugEnabled())
			logger.debug("session created: " + se);
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		if(logger.isDebugEnabled())
			logger.debug("session destroyed: " + se);
		
		HttpSession session = se.getSession();
		
		SessionScope scope = (SessionScope)session.getAttribute(SessionScope.SESSION_SCOPE_ATTRIBUTE);
		
		if(scope != null)
			scope.destroy();
	}
	
	protected AspectranContext getAspectranContext(ServletContext servletContext) {
		AspectranContextLoader aspectranContextLoader = (AspectranContextLoader)servletContext.getAttribute(AspectranContextLoader.ASPECTRAN_CONTEXT_LOADER_ATTRIBUTE);
		
		if(aspectranContextLoader != null)
			return aspectranContextLoader.getAspectranContext();
		
		return null;
	}

}
