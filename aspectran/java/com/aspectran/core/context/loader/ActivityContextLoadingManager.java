package com.aspectran.core.context.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.ActivityContextException;
import com.aspectran.core.context.loader.config.AspectranConfig;
import com.aspectran.core.context.loader.config.AspectranContextAutoReloadingConfig;
import com.aspectran.core.context.loader.config.AspectranContextConfig;
import com.aspectran.core.context.loader.config.AspectranSchedulerConfig;
import com.aspectran.core.context.loader.reload.ActivityContextReloadingHandler;
import com.aspectran.core.context.loader.reload.ActivityContextReloadingTimer;
import com.aspectran.core.var.apon.Parameters;
import com.aspectran.scheduler.AspectranScheduler;
import com.aspectran.scheduler.quartz.QuartzAspectranScheduler;

public class ActivityContextLoadingManager {

	private final Logger logger = LoggerFactory.getLogger(ActivityContextLoadingManager.class);

	private Parameters aspectranConfig;
	
	private Parameters aspectranSchedulerConfig;
	
	protected ActivityContext activityContext;

	private AspectranScheduler aspectranScheduler;
	
	private ActivityContextReloadingTimer contextReloadingTimer;
	
	public ActivityContextLoadingManager(AspectranConfig aspectranConfig) {
		this.aspectranConfig = aspectranConfig;
	}
	
	public ActivityContext createActivityContext(ActivityContextLoader activityContextLoader) throws ActivityContextException {
		logger.info("loading ActivityContext...");

		try {
			Parameters aspectranContextConfig = aspectranConfig.getParameters(AspectranConfig.context.getName());
			Parameters aspectranContextAutoReloadingConfig = aspectranContextConfig.getParameters(AspectranContextConfig.autoReloading.getName());
			Parameters aspectranSchedulerConfig = aspectranConfig.getParameters(AspectranConfig.scheduler.getName());
			
			String rootContext = aspectranContextConfig.getString(AspectranContextConfig.root.getName());
			String[] resourceLocations = aspectranContextConfig.getStringArray(AspectranContextConfig.resources.getName());
			int observationInterval = aspectranContextAutoReloadingConfig.getInt(AspectranContextAutoReloadingConfig.observationInterval.getName(), -1);
			boolean autoReloadingStartup = aspectranContextAutoReloadingConfig.getBoolean(AspectranContextAutoReloadingConfig.startup.getName(), true);

			if(autoReloadingStartup && resourceLocations == null || resourceLocations.length == 0)
				autoReloadingStartup = false;
			
			AspectranClassLoader aspectranClassLoader = activityContextLoader.getAspectranClassLoader();
			
			if(activityContextLoader != null)
				aspectranClassLoader.setResourceLocations(resourceLocations);
			
			if(!autoReloadingStartup) {
				activityContext = activityContextLoader.load(rootContext);
			} else {
				if(observationInterval == -1) {
					logger.info("[Aspectran Config] 'observationInterval' is not specified, defaulting to 10 seconds.");
					observationInterval = 10;
				}

				ActivityContextReloadingHandler contextReloadingHandler = new ActivityContextReloadingHandler() {
					public void handle(ActivityContext newActivityContext) {
						reloadActivityContext(newActivityContext);
					}
				};

				activityContext = activityContextLoader.load(rootContext);
				contextReloadingTimer = activityContextLoader.startTimer(contextReloadingHandler, observationInterval);
			}
			
			startupAspectranScheduler(aspectranSchedulerConfig);
			
			return activityContext;
			
		} catch(Exception e) {
			throw new ActivityContextLoadingFailedException("Failed to load the ActivityContext", e);
		}
	}
	
	public boolean destroyActivityContext() {
		if(contextReloadingTimer != null)
			contextReloadingTimer.cancel();
		
		boolean cleanlyDestoryed = true;

		if(!shutdownAspectranScheduler())
			cleanlyDestoryed = false;

		if(activityContext != null) {
			try {
				activityContext.destroy();
				activityContext = null;
				logger.info("AspectranContext was destroyed successfully.");
			} catch(Exception e) {
				logger.error("AspectranContext was failed to destroy: " + e.toString(), e);
				cleanlyDestoryed = false;
			}
		}
		
		return cleanlyDestoryed;
	}
	
	protected ActivityContext reloadActivityContext(ActivityContext newActivityContext) {
		destroyActivityContext();
		
		activityContext = newActivityContext;
		
		try {
			if(this.aspectranSchedulerConfig != null)
				startupAspectranScheduler(null);
		} catch(Exception e) {
			logger.error("AspectranScheduler was failed to initialize: " + e.toString(), e);
		}
		
		if(contextReloadingTimer != null)
			contextReloadingTimer.start();
		
		return activityContext;
	}

	
	protected void startupAspectranScheduler(Parameters aspectranSchedulerConfig) throws Exception {
		if(aspectranSchedulerConfig != null)
			this.aspectranSchedulerConfig = aspectranSchedulerConfig;
		
		if(this.aspectranSchedulerConfig == null)
			return;
		
		logger.info("starting the AspectranScheduler: " + this.aspectranSchedulerConfig);
		
		boolean startup = this.aspectranSchedulerConfig.getBoolean(AspectranSchedulerConfig.startup.getName());
		int startDelaySeconds = this.aspectranSchedulerConfig.getInt(AspectranSchedulerConfig.startDelaySeconds.getName(), -1);
		boolean waitOnShutdown = this.aspectranSchedulerConfig.getBoolean(AspectranSchedulerConfig.waitOnShutdown.getName());
		
		if(startup) {
			aspectranScheduler = new QuartzAspectranScheduler(activityContext);
			
			if(waitOnShutdown)
				aspectranScheduler.setWaitOnShutdown(true);
			
			if(startDelaySeconds == -1) {
				logger.info("Scheduler option 'startDelaySeconds' is not specified. So defaulting to 5 seconds.");
				startDelaySeconds = 5;
			}
			
			aspectranScheduler.startup(startDelaySeconds);
		}
	}
	
	
	protected boolean shutdownAspectranScheduler() {
		if(aspectranScheduler != null) {
			try {
				aspectranScheduler.shutdown();
				aspectranScheduler = null;
				logger.info("AspectranScheduler has been shutdown successfully.");
			} catch(Exception e) {
				logger.error("AspectranScheduler was failed to shutdown cleanly: " + e.toString(), e);
				return false;
			}
		}
		
		return true;
	}
	
}
