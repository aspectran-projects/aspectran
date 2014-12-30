package com.aspectran.web.context.loader;

import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.loader.XmlActivityContextLoader;

public class WebActivityContextLoader extends XmlActivityContextLoader {

	private static final String DEFAULT_ROOT_CONTEXT = "/WEB-INF/aspectran/aspectranContext.xml";
	
	public WebActivityContextLoader() {
		super();
	}
	
	public ActivityContext load(String rootContext) {
		if(rootContext == null || rootContext.length() == 0)
			rootContext = DEFAULT_ROOT_CONTEXT;
		
		return super.load(rootContext);
	}
	
}
