/*
 * Copyright (c) 2008-2019 The Aspectran Project
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
package com.aspectran.core.context;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.DefaultActivity;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.component.AbstractComponent;
import com.aspectran.core.component.aspect.AspectRuleRegistry;
import com.aspectran.core.component.bean.BeanRegistry;
import com.aspectran.core.component.bean.ContextBeanRegistry;
import com.aspectran.core.component.schedule.ScheduleRuleRegistry;
import com.aspectran.core.component.template.ContextTemplateRenderer;
import com.aspectran.core.component.template.TemplateRenderer;
import com.aspectran.core.component.translet.TransletRuleRegistry;
import com.aspectran.core.context.env.ContextEnvironment;
import com.aspectran.core.context.env.Environment;
import com.aspectran.core.service.CoreService;
import com.aspectran.core.support.i18n.message.DelegatingMessageSource;
import com.aspectran.core.support.i18n.message.MessageSource;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class AspectranActivityContext.
 * 
 * <p>Created: 2008. 06. 09 PM 2:12:40</p>
 */
public class AspectranActivityContext extends AbstractComponent implements ActivityContext {

    private static final Log log = LogFactory.getLog(AspectranActivityContext.class);

    private final ThreadLocal<Activity> currentActivityHolder = new ThreadLocal<>();

    private final ApplicationAdapter applicationAdapter;

    private final ContextEnvironment contextEnvironment;

    private final Activity defaultActivity;

    private String description;

    private CoreService rootService;

    private AspectRuleRegistry aspectRuleRegistry;

    private ContextBeanRegistry contextBeanRegistry;

    private ContextTemplateRenderer contextTemplateRenderer;

    private ScheduleRuleRegistry scheduleRuleRegistry;

    private TransletRuleRegistry transletRuleRegistry;

    private MessageSource messageSource;

    /**
     * Instantiates a new AspectranActivityContext.
     *
     * @param applicationAdapter the application adapter
     * @param contextEnvironment the context environment
     */
    public AspectranActivityContext(ApplicationAdapter applicationAdapter, ContextEnvironment contextEnvironment) {
        this.applicationAdapter = applicationAdapter;
        this.contextEnvironment = contextEnvironment;
        this.defaultActivity = new DefaultActivity(this);
    }

    @Override
    public ApplicationAdapter getApplicationAdapter() {
        return applicationAdapter;
    }

    @Override
    public Environment getEnvironment() {
        return contextEnvironment;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public CoreService getRootService() {
        return rootService;
    }

    @Override
    public void setRootService(CoreService rootService) {
        if (isInitialized()) {
            throw new IllegalStateException("ActivityContext is already initialized");
        }
        this.rootService = rootService;
    }

    @Override
    public AspectRuleRegistry getAspectRuleRegistry() {
        return aspectRuleRegistry;
    }

    public void setAspectRuleRegistry(AspectRuleRegistry aspectRuleRegistry) {
        this.aspectRuleRegistry = aspectRuleRegistry;
    }

    @Override
    public BeanRegistry getBeanRegistry() {
        return contextBeanRegistry;
    }

    /**
     * Sets the context bean registry.
     *
     * @param contextBeanRegistry the new context bean registry
     */
    public void setContextBeanRegistry(ContextBeanRegistry contextBeanRegistry) {
        this.contextBeanRegistry = contextBeanRegistry;
    }

    @Override
    public TemplateRenderer getTemplateRenderer() {
        return contextTemplateRenderer;
    }

    /**
     * Sets the template processor.
     *
     * @param contextTemplateRenderer the new template processor
     */
    public void setContextTemplateRenderer(ContextTemplateRenderer contextTemplateRenderer) {
        this.contextTemplateRenderer = contextTemplateRenderer;
    }

    @Override
    public ScheduleRuleRegistry getScheduleRuleRegistry() {
        return scheduleRuleRegistry;
    }

    public void setScheduleRuleRegistry(ScheduleRuleRegistry scheduleRuleRegistry) {
        this.scheduleRuleRegistry = scheduleRuleRegistry;
    }

    @Override
    public TransletRuleRegistry getTransletRuleRegistry() {
        return transletRuleRegistry;
    }

    /**
     * Sets the translet rule registry.
     *
     * @param transletRuleRegistry the new translet rule registry
     */
    public void setTransletRuleRegistry(TransletRuleRegistry transletRuleRegistry) {
        this.transletRuleRegistry = transletRuleRegistry;
    }

    @Override
    public MessageSource getMessageSource() {
        if (this.messageSource == null) {
            throw new IllegalStateException("MessageSource not initialized");
        }
        return messageSource;
    }

    @Override
    public Activity getDefaultActivity() {
        return defaultActivity;
    }

    @Override
    public Activity getCurrentActivity() {
        Activity activity = currentActivityHolder.get();
        return (activity != null ? activity : getDefaultActivity());
    }

    @Override
    public void setCurrentActivity(Activity activity) {
        currentActivityHolder.set(activity);
    }

    @Override
    public void removeCurrentActivity() {
        currentActivityHolder.remove();
    }

    /**
     * Initialize the MessageSource.
     * Use parent's if none defined in this context.
     */
    private void initMessageSource() {
        if (contextBeanRegistry.containsBean(MESSAGE_SOURCE_BEAN_ID)) {
            messageSource = contextBeanRegistry.getBean(MESSAGE_SOURCE_BEAN_ID, MessageSource.class);
            if (log.isDebugEnabled()) {
                log.debug("Using MessageSource [" + messageSource + "]");
            }
        } else {
            // Use empty MessageSource to be able to accept getMessage calls.
            messageSource = new DelegatingMessageSource();
            if (log.isDebugEnabled()) {
                log.debug("Unable to locate MessageSource with name '" + MESSAGE_SOURCE_BEAN_ID +
                        "': using default [" + messageSource + "]");
            }
        }
    }

    @Override
    protected void doInitialize() throws Exception {
        if (aspectRuleRegistry != null) {
            aspectRuleRegistry.initialize();
        }
        if (contextBeanRegistry != null) {
            contextBeanRegistry.initialize();
        }
        if (contextTemplateRenderer != null) {
            contextTemplateRenderer.initialize();
        }
        if (scheduleRuleRegistry != null) {
            scheduleRuleRegistry.initialize();
        }
        if (transletRuleRegistry != null) {
            transletRuleRegistry.initialize();
        }
        if (contextBeanRegistry != null) {
            initMessageSource();
        }
    }

    @Override
    protected void doDestroy() {
        if (transletRuleRegistry != null) {
            transletRuleRegistry.destroy();
            transletRuleRegistry = null;
        }
        if (scheduleRuleRegistry != null) {
            scheduleRuleRegistry.destroy();
            scheduleRuleRegistry = null;
        }
        if (contextTemplateRenderer != null) {
            contextTemplateRenderer.destroy();
            contextTemplateRenderer = null;
        }
        if (contextBeanRegistry != null) {
            contextBeanRegistry.destroy();
            contextBeanRegistry = null;
        }
        if (aspectRuleRegistry != null) {
            aspectRuleRegistry.destroy();
            aspectRuleRegistry = null;
        }
    }

}
