/*
 * Copyright (c) 2008-2017 The Aspectran Project
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
package com.aspectran.core.context.builder;

import com.aspectran.core.adapter.BasicApplicationAdapter;
import com.aspectran.core.component.Component;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.rule.params.AspectranParameters;
import com.aspectran.core.context.rule.parser.ActivityContextParser;
import com.aspectran.core.context.rule.parser.HybridActivityContextParser;
import com.aspectran.core.service.AbstractCoreService;
import com.aspectran.core.util.thread.ShutdownHooks;

import java.util.concurrent.atomic.AtomicBoolean;

public class HybridActivityContextBuilder extends AbstractActivityContextBuilder {

    private final AbstractCoreService coreService;

    private ActivityContext activityContext;

    /** Flag that indicates whether an ActivityContext is activated */
    private final AtomicBoolean active = new AtomicBoolean();

    /** Synchronization monitor for the "build" and "destroy" */
    private final Object buildDestroyMonitor = new Object();

    /** Reference to the shutdown task, if registered */
    private ShutdownHooks.Task shutdownTask;

    public HybridActivityContextBuilder() {
        super(new BasicApplicationAdapter());
        this.coreService = null;
    }

    public HybridActivityContextBuilder(AbstractCoreService coreService) {
        super(coreService.getApplicationAdapter());
        this.coreService = coreService;
        setServiceController(coreService);
    }

    @Override
    public ActivityContext build(AspectranParameters aspectranParameters) throws ActivityContextBuilderException {
        synchronized (this.buildDestroyMonitor) {
            setAspectranParameters(aspectranParameters);
            return doBuild();
        }
    }

    @Override
    public ActivityContext build(String rootConfigLocation) throws ActivityContextBuilderException {
        synchronized (this.buildDestroyMonitor) {
            setRootConfigLocation(rootConfigLocation);
            return doBuild();
        }
    }

    @Override
    public ActivityContext build() throws ActivityContextBuilderException {
        synchronized (this.buildDestroyMonitor) {
            return doBuild();
        }
    }

    private ActivityContext doBuild() throws ActivityContextBuilderException {
        try {
            if (this.active.get()) {
                throw new IllegalStateException("An ActivityContext already activated");
            }

            String rootConfigLocation = getRootConfigLocation();
            AspectranParameters aspectranParameters = getAspectranParameters();

            if (rootConfigLocation == null && aspectranParameters == null) {
                throw new IllegalArgumentException("Either context.root or context.parameters must be specified in ContextConfig " + getContextConfig());
            }

            newAspectranClassLoader();

            log.info("Building an ActivityContext with " + rootConfigLocation);

            long startTime = System.currentTimeMillis();

            ActivityContextParser parser = new HybridActivityContextParser(getApplicationAdapter());
            parser.setActiveProfiles(getActiveProfiles());
            parser.setDefaultProfiles(getDefaultProfiles());
            parser.setEncoding(getEncoding());
            parser.setHybridLoad(isHybridLoad());

            ActivityContext activityContext;
            if (rootConfigLocation != null) {
                activityContext = parser.parse(rootConfigLocation);
            } else {
                activityContext = parser.parse(aspectranParameters);
            }

            this.activityContext = activityContext;

            if (coreService != null) {
                coreService.setActivityContext(activityContext);
                activityContext.setRootService(coreService);
            }

            ((Component)activityContext).initialize();

            long elapsedTime = System.currentTimeMillis() - startTime;

            log.info("ActivityContext build completed in " + elapsedTime + " ms");

            registerDestroyTask();

            startReloadingTimer();

            this.active.set(true);

            return activityContext;
        } catch (Exception e) {
            if (getContextConfig() != null) {
                throw new ActivityContextBuilderException("Failed to build an ActivityContext with " + getContextConfig(), e);
            } else {
                throw new ActivityContextBuilderException("Failed to build an ActivityContext", e);
            }
        }
    }

    @Override
    public void destroy() {
        synchronized (this.buildDestroyMonitor) {
            doDestroy();
            removeDestroyTask();
        }
    }

    private void doDestroy() {
        if (this.active.get()) {
            stopReloadingTimer();

            getApplicationAdapter().getApplicationScope().destroy();

            if (activityContext != null) {
                ((Component)activityContext).destroy();
                activityContext = null;
            }

            if (coreService != null) {
                coreService.setActivityContext(null);
            }

            this.active.set(false);
        }
    }

    /**
     * Registers a shutdown hook with the JVM runtime, closing this context
     * on JVM shutdown unless it has already been closed at that time.
     */
    private void registerDestroyTask() {
        if (this.coreService == null && this.shutdownTask == null) {
            // Register a task to destroy the activity context on shutdown
            this.shutdownTask = ShutdownHooks.add(() -> {
                synchronized (this.buildDestroyMonitor) {
                    doDestroy();
                    removeDestroyTask();
                }
            });
        }
    }

    /**
     * De-registers a shutdown hook with the JVM runtime.
     */
    private void removeDestroyTask() {
        // If we registered a JVM shutdown hook, we don't need it anymore now:
        // We've already explicitly closed the context.
        if (this.shutdownTask != null) {
            ShutdownHooks.remove(this.shutdownTask);
            this.shutdownTask = null;
        }
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

}
