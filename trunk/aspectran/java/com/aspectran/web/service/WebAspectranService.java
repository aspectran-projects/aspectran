package com.aspectran.web.service;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContextException;
import com.aspectran.core.context.loader.AspectranClassLoader;
import com.aspectran.core.context.loader.config.AspectranConfig;
import com.aspectran.core.context.loader.config.AspectranContextConfig;
import com.aspectran.core.context.translet.TransletNotFoundException;
import com.aspectran.core.service.AspectranService;
import com.aspectran.core.service.AspectranServiceControllerListener;
import com.aspectran.core.service.CoreAspectranService;
import com.aspectran.core.util.apon.Parameter;
import com.aspectran.core.util.apon.Parameters;
import com.aspectran.web.activity.WebActivity;
import com.aspectran.web.activity.WebActivityDefaultHandler;
import com.aspectran.web.activity.WebActivityImpl;
import com.aspectran.web.adapter.WebApplicationAdapter;
import com.aspectran.web.startup.listener.AspectranServiceListener;
import com.aspectran.web.startup.servlet.WebActivityServlet;

public class WebAspectranService extends CoreAspectranService {
	
	private static final Logger logger = LoggerFactory.getLogger(CoreAspectranService.class);
	
	public static final String ASPECTRAN_CONFIG_PARAM = "aspectran:config";

	public static final String WEB_APPLICATION_ADAPTER_ATTRIBUTE =  WebApplicationAdapter.class.getName() + ".WEB_APPLICATION_ADAPTER";

	private static final String DEFAULT_ROOT_CONTEXT = "/WEB-INF/aspectran/root.xml";
	
	private static final String[] excludePackageNames;
	
	static {
		excludePackageNames = new String[] {
			"com.aspectran.core",
			"com.aspectran.scheduler",
			"com.aspectran.support",
			"com.aspectran.web"
		};
	}
	
	public WebAspectranService(ServletContext servletContext) {
		ApplicationAdapter aa = new WebApplicationAdapter(servletContext);
		setApplicationAdapter(aa);
	}
	
	public WebAspectranService(AspectranService aspectranService) {
		super(aspectranService);
	}
	
	private synchronized void initialize(String aspectranConfigParam) throws ActivityContextException {
		AspectranConfig aspectranConfig;
		
		if(aspectranConfigParam != null) {
			aspectranConfig = new AspectranConfig(aspectranConfigParam);
		} else {
			aspectranConfig = new AspectranConfig();
		}

		Parameters contextParams = aspectranConfig.getParameters(AspectranConfig.context);
		Parameter rootContextParam = contextParams.getParameter(AspectranContextConfig.root);
		String rootContext = rootContextParam.getValueAsString();

		if(rootContext == null || rootContext.length() == 0) {
			rootContextParam.putValue(DEFAULT_ROOT_CONTEXT);
		}

		initialize(aspectranConfig);
	}
	
	public void service(WebActivityServlet servlet, HttpServletRequest req, HttpServletResponse res) throws IOException {
		String requestUri = req.getRequestURI();

		if(servlet.pauseTimeout > 0L) {
			if(servlet.pauseTimeout >= System.currentTimeMillis()) {
				logger.info("aspectran service is paused, did not respond to the request uri [" + requestUri + "]");
				res.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				return;
			} else {
				servlet.pauseTimeout = 0L;
			}
		}
		
		WebActivity activity = null;

		try {
			activity = new WebActivityImpl(activityContext, req, res);
			activity.ready(requestUri);
			activity.perform();
			activity.finish();
		} catch(TransletNotFoundException e) {
			if(activity != null) {
				String activityDefaultHandler = activityContext.getActivityDefaultHandler();

				if(activityDefaultHandler != null) {
					try {
						System.out.println("activity.getBean(activityDefaultHandler):" + activity.getBean(activityDefaultHandler));
						
						WebActivityDefaultHandler handler = (WebActivityDefaultHandler)activity.getBean(activityDefaultHandler);
						handler.setServletContext(servlet.getServletContext());
						handler.handle(req, res);
					} catch(Exception e2) {
						res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						logger.error(e.getMessage(), e2);
					}

					return;
				}
			}

			logger.debug(e.getMessage());
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch(Exception e) {
			logger.error("WebActivity service failed.", e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}
	
	public static WebAspectranService newInstance(ServletContext servletContext) {
		String aspectranConfigParam = servletContext.getInitParameter(ASPECTRAN_CONFIG_PARAM);

		WebAspectranService aspectranService = newInstance(servletContext, null, aspectranConfigParam);
		
		servletContext.setAttribute(AspectranServiceListener.ASPECTRAN_SERVICE_ATTRIBUTE, aspectranService);
		logger.debug("AspectranServiceListener attribute in ServletContext was created. {}: {}", AspectranServiceListener.ASPECTRAN_SERVICE_ATTRIBUTE, aspectranService);
		
		return aspectranService;
	}
	
	public static WebAspectranService newInstance(WebActivityServlet servlet) {
		ServletContext servletContext = servlet.getServletContext();
		ServletConfig servletConfig = servlet.getServletConfig();
		
		String aspectranConfigParam = servletConfig.getInitParameter(ASPECTRAN_CONFIG_PARAM);
		
		return newInstance(servletContext, servlet, aspectranConfigParam);
	}

	public static WebAspectranService newInstance(WebActivityServlet servlet, AspectranService rootAspectranService) {
		ServletContext servletContext = servlet.getServletContext();
		ServletConfig servletConfig = servlet.getServletConfig();
		
		String aspectranConfigParam = servletConfig.getInitParameter(ASPECTRAN_CONFIG_PARAM);
		
		if(aspectranConfigParam != null) {
			return newInstance(servletContext, servlet, aspectranConfigParam);
		} else {
			WebAspectranService aspectranService = new WebAspectranService(rootAspectranService);

			setAspectranServiceControllerListener(rootAspectranService, servlet);
			
			aspectranService.start();
			
			return aspectranService;
		}
	}

	private static WebAspectranService newInstance(ServletContext servletContext, WebActivityServlet servlet, String aspectranConfigParam) {
		AspectranClassLoader aspectranClassLoader = new AspectranClassLoader();
		aspectranClassLoader.excludePackage(excludePackageNames);
		
		WebAspectranService aspectranService = new WebAspectranService(servletContext);
		aspectranService.setAspectranClassLoader(aspectranClassLoader);
		aspectranService.initialize(aspectranConfigParam);
		
		if(servlet != null)
			setAspectranServiceControllerListener(aspectranService, servlet);
		
		aspectranService.start();
		
		return aspectranService;
	}
	
	private static void setAspectranServiceControllerListener(AspectranService aspectranService, final WebActivityServlet servlet) {
		aspectranService.setAspectranServiceControllerListener(new AspectranServiceControllerListener() {
			public void started() {
				servlet.pauseTimeout = 0;
			}
			
			public void restarted() {
				started();
			}
			
			public void paused(long timeout) {
				if(timeout <= 0)
					timeout = 315360000000L; //86400000 * 365 * 10 = 10 Years;
				servlet.pauseTimeout = System.currentTimeMillis() + timeout;
			}
			
			public void resumed() {
				servlet.pauseTimeout = 0;
			}
			
			public void stopped() {
				paused(-1L);
			}
		});
	}
	
	/*
	private WebApplicationAdapter determineWebApplicationAdapter() {
		WebApplicationAdapter webApplicationAdapter = (WebApplicationAdapter)servletContext.getAttribute(WEB_APPLICATION_ADAPTER_ATTRIBUTE);
		
		if(webApplicationAdapter == null)
			webApplicationAdapter = createWebApplicationAdapter();

		return webApplicationAdapter;
	}
	
	private WebApplicationAdapter getWebApplicationAdapter(ServletContext servletContext) {
		return (WebApplicationAdapter)servletContext.getAttribute(WEB_APPLICATION_ADAPTER_ATTRIBUTE);
	}
	
	private WebApplicationAdapter createWebApplicationAdapter() {
		WebApplicationAdapter webApplicationAdapter = new WebApplicationAdapter(servletContext);
		servletContext.setAttribute(WEB_APPLICATION_ADAPTER_ATTRIBUTE, webApplicationAdapter);
		
		logger.debug("WebApplicationAdapter attribute was created. " + webApplicationAdapter);
		
		return webApplicationAdapter;
	}

	private void destoryWebApplicationAdapter() {
		WebApplicationAdapter webApplicationAdapter = getWebApplicationAdapter(servletContext);
		
		if(webApplicationAdapter != null) {
			Scope scope = webApplicationAdapter.getScope();
	
			if(scope != null)
				scope.destroy();

			if(servletContext.getAttribute(WEB_APPLICATION_ADAPTER_ATTRIBUTE) != null) {
				servletContext.removeAttribute(WEB_APPLICATION_ADAPTER_ATTRIBUTE);
				logger.debug("WebApplicationAdapter attribute was removed.");
			} else {
				logger.debug("WebApplicationAdapter attribute was already removed.");
			}
		}
	}
	*/
}
