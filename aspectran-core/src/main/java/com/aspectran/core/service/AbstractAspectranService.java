/*
 * Copyright 2008-2017 Juho Jeong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.service;

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.builder.ActivityContextBuilder;
import com.aspectran.core.context.builder.ActivityContextBuilderException;
import com.aspectran.core.context.builder.HybridActivityContextBuilder;
import com.aspectran.core.context.builder.config.AspectranConfig;
import com.aspectran.core.context.builder.config.AspectranContextConfig;
import com.aspectran.core.context.builder.config.AspectranSchedulerConfig;
import com.aspectran.core.context.builder.resource.AspectranClassLoader;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.core.util.wildcard.PluralWildcardPattern;
import com.aspectran.scheduler.service.QuartzSchedulerService;
import com.aspectran.scheduler.service.SchedulerService;

/**
 * The Class AbstractAspectranService.
 */
public abstract class AbstractAspectranService extends AbstractServiceContoller implements AspectranService {

    protected final Log log = LogFactory.getLog(getClass());

    private final ApplicationAdapter applicationAdapter;

    private AspectranConfig aspectranConfig;

    private AspectranSchedulerConfig aspectranSchedulerConfig;

    private ActivityContextBuilder activityContextBuilder;

    private PluralWildcardPattern exposableTransletNamesPattern;

    private ActivityContext activityContext;

    private SchedulerService schedulerService;

    public AbstractAspectranService(ApplicationAdapter applicationAdapter) {
        if (applicationAdapter == null) {
            throw new IllegalArgumentException("Argument 'applicationAdapter' must not be null");
        }

        this.applicationAdapter = applicationAdapter;
    }

    public AbstractAspectranService(AspectranService rootAspectranService) {
        if (rootAspectranService == null) {
            throw new IllegalArgumentException("Argument 'rootAspectranService' must not be null");
        }

        this.applicationAdapter = rootAspectranService.getApplicationAdapter();
        this.activityContext = rootAspectranService.getActivityContext();
        this.aspectranConfig = rootAspectranService.getAspectranConfig();

        if (this.activityContext == null) {
            throw new IllegalStateException("Oops! ActivityContext is not yet created");
        }
    }

    @Override
    public ApplicationAdapter getApplicationAdapter() {
        return applicationAdapter;
    }

    @Override
    public ActivityContext getActivityContext() {
        return activityContext;
    }

    public void setActivityContext(ActivityContext activityContext) {
        this.activityContext = activityContext;
    }

    @Override
    public AspectranClassLoader getAspectranClassLoader() {
        if (activityContextBuilder == null) {
            throw new IllegalStateException("ActivityContextLoader is not initialized; Call prepare() method first");
        }
        return activityContextBuilder.getAspectranClassLoader();
    }

    @Override
    public AspectranConfig getAspectranConfig() {
        return aspectranConfig;
    }

    @Override
    public boolean isHardReload() {
        if (activityContextBuilder == null) {
            throw new IllegalStateException("ActivityContextLoader is not initialized; Call prepare() method first");
        }
        return activityContextBuilder.isHardReload();
    }

    protected void prepare(AspectranConfig aspectranConfig) throws AspectranServiceException {
        if (activityContext != null) {
            throw new IllegalStateException("AspectranService can not be initialized because ActivityContext has already been loaded");
        }

        try {
            this.aspectranConfig = aspectranConfig;
            this.aspectranSchedulerConfig = aspectranConfig.getAspectranSchedulerConfig();

            AspectranContextConfig aspectranContextConfig = aspectranConfig.getAspectranContextConfig();

            activityContextBuilder = new HybridActivityContextBuilder(this);
            activityContextBuilder.initialize(aspectranContextConfig);
            activityContextBuilder.setAspectranServiceController(this);
        } catch (Exception e) {
            throw new AspectranServiceException("Could not prepare the AspectranService", e);
        }
    }

    protected void setExposals(String[] exposals) {
        exposableTransletNamesPattern = PluralWildcardPattern.newInstance(exposals, ActivityContext.TRANSLET_NAME_SEPARATOR_CHAR);
    }

    protected boolean isExposable(String transletName) {
        return (exposableTransletNamesPattern == null || exposableTransletNamesPattern.matches(transletName));
    }

    protected void loadActivityContext() throws ActivityContextBuilderException {
        if (activityContextBuilder == null) {
            throw new IllegalStateException("ActivityContextLoader is not in an instantiated state; First, call the initialize() method");
        }
        if (activityContext != null) {
            throw new IllegalStateException("ActivityContext has already been loaded. Must destroy the current ActivityContext before reloading");
        }

        activityContextBuilder.build();
    }

    protected void destroyActivityContext() {
        if (activityContextBuilder == null) {
            throw new IllegalStateException("ActivityContextBuilder is not in an instantiated state; First, call the initialize() method");
        }

        activityContextBuilder.destroy();
    }

    protected void startSchedulerService() throws Exception {
        if (this.aspectranSchedulerConfig == null) {
            return;
        }

        String[] exposals = this.aspectranSchedulerConfig.getStringArray(AspectranSchedulerConfig.exposals);
        boolean schedulerStartup = this.aspectranSchedulerConfig.getBoolean(AspectranSchedulerConfig.startup);
        int startDelaySeconds = this.aspectranSchedulerConfig.getInt(AspectranSchedulerConfig.startDelaySeconds.getName(), -1);
        boolean waitOnShutdown = this.aspectranSchedulerConfig.getBoolean(AspectranSchedulerConfig.waitOnShutdown);

        if (schedulerStartup) {
            if (startDelaySeconds == -1) {
                log.info("Scheduler option 'startDelaySeconds' not specified; So defaulting to 5 seconds");
                startDelaySeconds = 5;
            }

            SchedulerService newSchedulerService = new QuartzSchedulerService(activityContext);
            if (waitOnShutdown) {
                newSchedulerService.setWaitOnShutdown(true);
            }
            newSchedulerService.setExposals(exposals);
            newSchedulerService.setStartDelaySeconds(startDelaySeconds);
            newSchedulerService.start();

            this.schedulerService = newSchedulerService;
        }
    }

    protected void stopSchedulerService() {
        if (schedulerService != null) {
            schedulerService.stop();
            schedulerService = null;
        }
    }

    protected void pauseSchedulerService() throws Exception {
        if (schedulerService != null) {
            schedulerService.pause();
        }
    }

    protected void pauseSchedulerService(long timeout) throws Exception {
        log.warn(schedulerService.getServiceName() + " does not support pausing for a certain period of time");
    }

    protected void resumeSchedulerService() throws Exception {
        if (schedulerService != null) {
            schedulerService.resume();
        }
    }

}
