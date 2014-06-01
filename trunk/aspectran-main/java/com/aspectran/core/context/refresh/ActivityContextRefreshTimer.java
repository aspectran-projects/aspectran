package com.aspectran.core.context.refresh;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityContextRefreshTimer {
	
	private final Logger logger = LoggerFactory.getLogger(ActivityContextRefreshTimer.class);

	private ActivityContextRefreshable activityContextRefreshable;
	
	private Timer timer;
	
	private TimerTask timerTask;
	
	private int refreshTime = 5;
	
	public ActivityContextRefreshTimer(ActivityContextRefreshable activityContextRefreshable) {
		this.activityContextRefreshable = activityContextRefreshable;
		
		init();
	}
	
	public ActivityContextRefreshTimer(ActivityContextRefreshable activityContextRefreshable, int refreshTime) {
		this.activityContextRefreshable = activityContextRefreshable;
		this.refreshTime = refreshTime;
		
		init();
	}
	
	private void init() {
		logger.debug("ActivityContextRefreshTimer is initialized successfully.");
	}
	
	public void start(int refreshTime) {
		this.refreshTime = refreshTime;
		
		start();
	}
	
	public void start() {
		stop();
		
		logger.debug("starting ActivityContextRefreshTimer...");
		
		timerTask = new ActivityContextRefreshTimerTask(activityContextRefreshable);
		
		timer = new Timer();
		timer.schedule(timerTask, 0, refreshTime * 1000L);
	}
	
	public void cancel() {
		stop();
	}
	
	protected void stop() {
		if(timer != null) {
			logger.debug("stopping ActivityContextRefreshTimer...");
			
			timer.cancel();
			timer = null;
			
			timerTask.cancel();
			timerTask = null;
		}
	}

}