package com.aspectran.scheduler.quartz;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.AspectranConstant;
import com.aspectran.core.context.builder.apon.params.SimpleTriggerParameters;
import com.aspectran.core.var.apon.Parameters;
import com.aspectran.core.var.rule.AspectJobAdviceRule;
import com.aspectran.core.var.rule.AspectRule;
import com.aspectran.core.var.rule.AspectRuleMap;
import com.aspectran.core.var.rule.PointcutRule;
import com.aspectran.core.var.type.AspectTargetType;
import com.aspectran.core.var.type.PointcutType;
import com.aspectran.scheduler.AspectranScheduler;

public class QuartzAspectranScheduler implements AspectranScheduler {

	public final static String ASPECTRAN_CONTEXT_DATA_KEY = "ASPECTRAN_CONTEXT";

	public final static String TRANSLET_NAME_DATA_KEY = "TRANSLET_NAME";
	
	private final Logger logger = LoggerFactory.getLogger(QuartzAspectranScheduler.class);

	private ActivityContext context;
	
	private List<Scheduler> startedSchedulerList = new ArrayList<Scheduler>();
	
	private Map<String, Scheduler> eachAspectSchedulerMap = new LinkedHashMap<String, Scheduler>();
	
	private int startDelaySeconds = 0;
	
	private boolean waitOnShutdown = false;
	
	public QuartzAspectranScheduler(ActivityContext context) {
		this.context = context;
	}
	
	public int getStartDelaySeconds() {
		return startDelaySeconds;
	}

	public void setStartDelaySeconds(int startDelaySeconds) {
		this.startDelaySeconds = startDelaySeconds;
	}

	public boolean isWaitOnShutdown() {
		return waitOnShutdown;
	}

	public void setWaitOnShutdown(boolean waitOnShutdown) {
		this.waitOnShutdown = waitOnShutdown;
	}

	public void startup(int delaySeconds) throws SchedulerException {
		this.startDelaySeconds = delaySeconds;
		startup();
	}
	
	public void startup() throws SchedulerException {
		AspectRuleMap aspectRuleMap = context.getAspectRuleRegistry().getAspectRuleMap();
		
		if(aspectRuleMap == null)
			return;
		
		try {
			Date startDate = new Date();
			
			if(startDelaySeconds > 0) {
				startDate = new Date(startDate.getTime() + (startDelaySeconds * 1000L));
			}
			
			for(AspectRule aspectRule : aspectRuleMap) {
				AspectTargetType aspectTargetType = aspectRule.getAspectTargetType();
				
				if(aspectTargetType == AspectTargetType.SCHEDULER) {
					String schedulerFactoryBeanId = aspectRule.getAdviceBeanId();
					PointcutRule pointcutRule = aspectRule.getPointcutRule();
					
					SchedulerFactory schedulerFactory = (SchedulerFactory)context.getContextBeanRegistry().getBean(schedulerFactoryBeanId);
					Scheduler scheduler = schedulerFactory.getScheduler();
					JobDetail[] jobDetails = buildJobDetails(aspectRule.getAspectJobAdviceRuleList());
					
					if(jobDetails.length > 0) {
						for(JobDetail jobDetail : jobDetails) {
							String triggerName = jobDetail.getKey().getName();
							String triggerGroup = aspectRule.getId();
							Trigger trigger = buildTrigger(triggerName, triggerGroup, pointcutRule, startDate);
	
							scheduler.scheduleJob(jobDetail, trigger);
						}
					}
	
					if(!startedSchedulerList.contains(scheduler) && !scheduler.isStarted()) {
						logger.info("Now try to start scheduler '{}'.", scheduler.getSchedulerName());
						
						if(startDelaySeconds > 0)
							scheduler.startDelayed(startDelaySeconds);
						else
							scheduler.start();
						
						startedSchedulerList.add(scheduler);
					}
	
					eachAspectSchedulerMap.put(aspectRule.getId(), scheduler);
				}
			}
		} catch(Exception e) {
			throw new SchedulerException("AspectranScheduler startup failed.", e);
		}
	}
	
	public void shutdown(boolean waitForJobsToComplete) throws SchedulerException {
		this.waitOnShutdown = waitForJobsToComplete;
		shutdown(waitOnShutdown);
	}
	
	public void shutdown() throws SchedulerException {
		for(Scheduler scheduler : startedSchedulerList) {
			if(!scheduler.isShutdown()) {
				logger.info("Now try to stop scheduler '{}'.", scheduler.getSchedulerName());
				scheduler.shutdown(waitOnShutdown);
			}
		}
	}
	
	public void refresh(ActivityContext context) throws SchedulerException {
		this.context = context;
		shutdown();
		startup();
	}
	
	public void pause(String aspectId) throws SchedulerException {
		Scheduler scheduler = getScheduler(aspectId);
		
		if(scheduler != null && scheduler.isStarted()) {
			scheduler.pauseJobs(GroupMatcher.jobGroupEquals(aspectId));
		}
	}
	
	public void resume(String aspectId) throws SchedulerException {
		Scheduler scheduler = getScheduler(aspectId);
		
		if(scheduler != null && scheduler.isStarted()) {
			scheduler.resumeJobs(GroupMatcher.jobGroupEquals(aspectId));
		}
	}

	private Scheduler getScheduler(String aspectId) throws SchedulerException {
		return eachAspectSchedulerMap.get(aspectId);
	}
	
	private Trigger buildTrigger(String name, String group, PointcutRule pointcutRule, Date startDate) {
		Trigger trigger = null;

		if(pointcutRule.getPointcutType() == PointcutType.SIMPLE_TRIGGER) {
			Parameters simpleTriggerParameters = pointcutRule.getSimpleTriggerParameters();
			Integer withIntervalInMilliseconds = (Integer)simpleTriggerParameters.getValue(SimpleTriggerParameters.withIntervalInMilliseconds);
			Integer withIntervalInMinutes = (Integer)simpleTriggerParameters.getValue(SimpleTriggerParameters.withIntervalInMinutes);
			Integer withIntervalInSeconds = (Integer)simpleTriggerParameters.getValue(SimpleTriggerParameters.withIntervalInSeconds);
			Integer withIntervalInHours = (Integer)simpleTriggerParameters.getValue(SimpleTriggerParameters.withIntervalInHours);
			Integer withRepeatCount = (Integer)simpleTriggerParameters.getValue(SimpleTriggerParameters.withRepeatCount);
			Boolean repeatForever = (Boolean)simpleTriggerParameters.getValue(SimpleTriggerParameters.repeatForever);

			SimpleScheduleBuilder simpleSchedule = SimpleScheduleBuilder.simpleSchedule();

			if(withIntervalInMilliseconds != null)
				simpleSchedule.withIntervalInMilliseconds(withIntervalInMilliseconds);
			if(withIntervalInMinutes != null)
				simpleSchedule.withIntervalInMinutes(withIntervalInMinutes);
			if(withIntervalInSeconds != null)
				simpleSchedule.withIntervalInSeconds(withIntervalInSeconds);
			if(withIntervalInHours != null)
				simpleSchedule.withIntervalInHours(withIntervalInHours);
			if(withRepeatCount != null)
				simpleSchedule.withRepeatCount(withRepeatCount);
			if(Boolean.TRUE.equals(repeatForever))
				simpleSchedule.repeatForever();
				
			trigger = TriggerBuilder.newTrigger()
					.withIdentity(name, group)
					.startAt(startDate)
					.withSchedule(simpleSchedule)
					.build();
		} else {
			CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(pointcutRule.getPatternString());
			
			trigger = TriggerBuilder.newTrigger()
					.withIdentity(name, group)
					.startAt(startDate)
					.withSchedule(cronSchedule)
					.build();
			
		}
		
		return trigger;
	}
	
	private JobDetail[] buildJobDetails(List<AspectJobAdviceRule> aspectJobAdviceRuleList) {
		List<JobDetail> jobDetailList = new ArrayList<JobDetail>();
		
		for(int i = 0; i < aspectJobAdviceRuleList.size(); i++) {
			AspectJobAdviceRule aspectJobAdviceRule = (AspectJobAdviceRule)aspectJobAdviceRuleList.get(i);
			JobDetail jobDetail = buildJobDetail(aspectJobAdviceRule, i);
			
			if(jobDetail != null)
				jobDetailList.add(jobDetail);
		}
		
		return jobDetailList.toArray(new JobDetail[jobDetailList.size()]);
	}

	private JobDetail buildJobDetail(AspectJobAdviceRule aspectJobAdviceRule, int index) {
		if(aspectJobAdviceRule.isDisabled())
			return null;
		
		String jobName = index + (AspectranConstant.TRANSLET_NAME_SEPARATOR + aspectJobAdviceRule.getJobTransletName());
		String jobGroup = aspectJobAdviceRule.getAspectId();
		
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(ASPECTRAN_CONTEXT_DATA_KEY, context);
		jobDataMap.put(TRANSLET_NAME_DATA_KEY, aspectJobAdviceRule.getJobTransletName());
		
		JobDetail jobDetail = JobBuilder.newJob(JobActivityRunJob.class)
				.withIdentity(jobName, jobGroup)
				.setJobData(jobDataMap)
				.build();
		
		return jobDetail;
	}
	
}
