/*
 * Copyright (c) 2008-2021 The Aspectran Project
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

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.adapter.DefaultApplicationAdapter;
import com.aspectran.core.component.aspect.AspectAdviceRulePreRegister;
import com.aspectran.core.component.aspect.AspectRuleRegistry;
import com.aspectran.core.component.aspect.InvalidPointcutPatternException;
import com.aspectran.core.component.aspect.pointcut.Pointcut;
import com.aspectran.core.component.aspect.pointcut.PointcutFactory;
import com.aspectran.core.component.aspect.pointcut.PointcutPattern;
import com.aspectran.core.component.bean.BeanRuleRegistry;
import com.aspectran.core.component.bean.DefaultBeanRegistry;
import com.aspectran.core.component.schedule.ScheduleRuleRegistry;
import com.aspectran.core.component.template.DefaultTemplateRenderer;
import com.aspectran.core.component.template.TemplateRuleRegistry;
import com.aspectran.core.component.translet.TransletRuleRegistry;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.DefaultActivityContext;
import com.aspectran.core.context.builder.reload.ActivityContextReloader;
import com.aspectran.core.context.config.ContextAutoReloadConfig;
import com.aspectran.core.context.config.ContextConfig;
import com.aspectran.core.context.config.ContextProfilesConfig;
import com.aspectran.core.context.env.ActivityEnvironment;
import com.aspectran.core.context.env.EnvironmentProfiles;
import com.aspectran.core.context.resource.AspectranClassLoader;
import com.aspectran.core.context.resource.InvalidResourceException;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.EnvironmentRule;
import com.aspectran.core.context.rule.IllegalRuleException;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.PointcutPatternRule;
import com.aspectran.core.context.rule.PointcutRule;
import com.aspectran.core.context.rule.assistant.ActivityRuleAssistant;
import com.aspectran.core.context.rule.assistant.BeanReferenceException;
import com.aspectran.core.context.rule.assistant.BeanReferenceInspector;
import com.aspectran.core.context.rule.params.AspectranParameters;
import com.aspectran.core.context.rule.type.AutoReloadType;
import com.aspectran.core.context.rule.type.BeanProxifierType;
import com.aspectran.core.context.rule.type.DefaultSettingType;
import com.aspectran.core.service.ServiceController;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.SystemUtils;
import com.aspectran.core.util.logging.Logger;
import com.aspectran.core.util.logging.LoggerFactory;

import java.util.List;

public abstract class AbstractActivityContextBuilder implements ActivityContextBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AbstractActivityContextBuilder.class);

    private ContextConfig contextConfig;

    private AspectranParameters aspectranParameters;

    private String basePath;

    private String rootFile;

    private String encoding;

    private String[] resourceLocations;

    private String[] basePackages;

    private String[] activeProfiles;

    private String[] defaultProfiles;

    private ItemRuleMap propertyItemRuleMap;

    private boolean hardReload;

    private boolean autoReloadEnabled;

    private int scanIntervalSeconds;

    private ActivityContextReloader contextReloader;

    private ServiceController serviceController;

    private AspectranClassLoader aspectranClassLoader;

    private boolean useAponToLoadXml;

    private boolean debugMode;

    public AbstractActivityContextBuilder() {
        this.useAponToLoadXml = Boolean.parseBoolean(SystemUtils.getProperty(USE_APON_TO_LOAD_XML_PROPERTY_NAME));
        this.debugMode = Boolean.parseBoolean(SystemUtils.getProperty(DEBUG_MODE_PROPERTY_NAME));
    }

    @Override
    public ContextConfig getContextConfig() {
        return contextConfig;
    }

    @Override
    public AspectranParameters getAspectranParameters() {
        return aspectranParameters;
    }

    @Override
    public void setAspectranParameters(AspectranParameters aspectranParameters) {
        this.aspectranParameters = aspectranParameters;
        this.rootFile = null;
    }

    @Override
    public String getBasePath() {
        return basePath;
    }

    @Override
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String getRootFile() {
        return rootFile;
    }

    @Override
    public void setRootFile(String rootFile) {
        this.rootFile = rootFile;
        this.aspectranParameters = null;
    }

    @Override
    public String getEncoding() {
        return (encoding == null ? ActivityContext.DEFAULT_ENCODING : encoding);
    }

    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String[] getResourceLocations() {
        return resourceLocations;
    }

    @Override
    public void setResourceLocations(String... resourceLocations) {
        this.resourceLocations = resourceLocations;
    }

    @Override
    public String[] getBasePackages() {
        return basePackages;
    }

    @Override
    public void setBasePackages(String... basePackages) {
        if (basePackages != null && basePackages.length > 0) {
            this.basePackages = basePackages;
        }
    }

    @Override
    public String[] getActiveProfiles() {
        return activeProfiles;
    }

    @Override
    public void setActiveProfiles(String... activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    @Override
    public String[] getDefaultProfiles() {
        return defaultProfiles;
    }

    @Override
    public void setDefaultProfiles(String... defaultProfiles) {
        this.defaultProfiles = defaultProfiles;
    }

    @Override
    public ItemRuleMap getPropertyItemRuleMap() {
        return propertyItemRuleMap;
    }

    @Override
    public void setPropertyItemRuleMap(ItemRuleMap propertyItemRuleMap) {
        this.propertyItemRuleMap = propertyItemRuleMap;
    }

    @Override
    public void addPropertyItemRule(ItemRuleMap propertyItemRuleMap) {
        if (this.propertyItemRuleMap == null) {
            this.propertyItemRuleMap = new ItemRuleMap(propertyItemRuleMap);
        } else {
            this.propertyItemRuleMap.putAll(propertyItemRuleMap);
        }
    }

    @Override
    public boolean isHardReload() {
        return hardReload;
    }

    @Override
    public void setHardReload(boolean hardReload) {
        this.hardReload = hardReload;
    }

    @Override
    public ServiceController getServiceController() {
        return serviceController;
    }

    @Override
    public void setServiceController(ServiceController serviceController) {
        this.serviceController = serviceController;
    }

    @Override
    public AspectranClassLoader getAspectranClassLoader() {
        return aspectranClassLoader;
    }

    @Override
    public void setContextConfig(ContextConfig contextConfig) throws InvalidResourceException {
        if (contextConfig == null) {
            throw new IllegalArgumentException("contextConfig must not be null");
        }

        this.contextConfig = contextConfig;

        if (getBasePath() == null) {
            setBasePath(contextConfig.getBasePath());
        }

        this.rootFile = contextConfig.getRootFile();

        AspectranParameters aspectranParameters = contextConfig.getAspectranParameters();
        if (aspectranParameters != null) {
            this.aspectranParameters = aspectranParameters;
        }

        this.encoding = contextConfig.getEncoding();

        String[] resourceLocations = contextConfig.getResourceLocations();
        this.resourceLocations = AspectranClassLoader.checkResourceLocations(resourceLocations, getBasePath());

        this.basePackages = contextConfig.getBasePackages();

        ContextProfilesConfig profilesConfig = contextConfig.getProfilesConfig();
        if (profilesConfig != null) {
            setActiveProfiles(profilesConfig.getActiveProfiles());
            setDefaultProfiles(profilesConfig.getDefaultProfiles());
        }

        ContextAutoReloadConfig autoReloadConfig = contextConfig.getAutoReloadConfig();
        if (autoReloadConfig != null) {
            String reloadMode = autoReloadConfig.getReloadMode();
            int scanIntervalSeconds = autoReloadConfig.getScanIntervalSeconds();
            boolean autoReloadEnabled = autoReloadConfig.isEnabled();
            this.hardReload = AutoReloadType.HARD.toString().equals(reloadMode);
            this.autoReloadEnabled = autoReloadEnabled;
            this.scanIntervalSeconds = scanIntervalSeconds;
        }
        if (this.autoReloadEnabled && (this.resourceLocations == null || this.resourceLocations.length == 0)) {
            this.autoReloadEnabled = false;
        }
        if (this.autoReloadEnabled) {
            if (this.scanIntervalSeconds == -1) {
                this.scanIntervalSeconds = 10;
                if (logger.isDebugEnabled()) {
                    logger.debug("Context option 'autoReload' not specified, defaulting to 10 seconds");
                }
            }
        }
    }

    protected boolean isUseAponToLoadXml() {
        return useAponToLoadXml;
    }

    @Override
    public void setUseAponToLoadXml(boolean useAponToLoadXml) {
        this.useAponToLoadXml = useAponToLoadXml;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    protected ApplicationAdapter createApplicationAdapter() throws InvalidResourceException {
        AspectranClassLoader acl = newAspectranClassLoader();
        return new DefaultApplicationAdapter(basePath, acl);
    }

    protected EnvironmentProfiles createEnvironmentProfiles() {
        EnvironmentProfiles environmentProfiles = new EnvironmentProfiles();
        if (activeProfiles != null) {
            environmentProfiles.setActiveProfiles(activeProfiles);
        }
        if (defaultProfiles != null) {
            environmentProfiles.setDefaultProfiles(defaultProfiles);
        }
        return environmentProfiles;
    }

    /**
     * Returns a new instance of ActivityContext.
     * @param assistant the activity rule assistant
     * @return the activity context
     * @throws BeanReferenceException will be thrown when cannot resolve reference to bean
     * @throws IllegalRuleException if an illegal rule is found
     */
    protected ActivityContext createActivityContext(ActivityRuleAssistant assistant)
            throws BeanReferenceException, IllegalRuleException {
        DefaultActivityContext activityContext = new DefaultActivityContext(assistant.getApplicationAdapter());
        activityContext.setDescriptionRule(assistant.getAssistantLocal().getDescriptionRule());

        ActivityEnvironment activityEnvironment = createActivityEnvironment(assistant, activityContext);
        activityContext.setActivityEnvironment(activityEnvironment);

        AspectRuleRegistry aspectRuleRegistry = assistant.getAspectRuleRegistry();

        BeanRuleRegistry beanRuleRegistry = assistant.getBeanRuleRegistry();
        beanRuleRegistry.postProcess(assistant);

        BeanReferenceInspector beanReferenceInspector = assistant.getBeanReferenceInspector();
        beanReferenceInspector.inspect(beanRuleRegistry);

        initAspectRuleRegistry(assistant);

        BeanProxifierType beanProxifierType = BeanProxifierType.resolve(
                (String)assistant.getSetting(DefaultSettingType.BEAN_PROXIFIER));
        DefaultBeanRegistry defaultBeanRegistry = new DefaultBeanRegistry(
                activityContext, beanRuleRegistry, beanProxifierType);

        TemplateRuleRegistry templateRuleRegistry = assistant.getTemplateRuleRegistry();
        DefaultTemplateRenderer defaultTemplateRenderer = new DefaultTemplateRenderer(
                activityContext, templateRuleRegistry);

        ScheduleRuleRegistry scheduleRuleRegistry = assistant.getScheduleRuleRegistry();
        TransletRuleRegistry transletRuleRegistry = assistant.getTransletRuleRegistry();

        activityContext.setAspectRuleRegistry(aspectRuleRegistry);
        activityContext.setDefaultBeanRegistry(defaultBeanRegistry);
        activityContext.setScheduleRuleRegistry(scheduleRuleRegistry);
        activityContext.setDefaultTemplateRenderer(defaultTemplateRenderer);
        activityContext.setTransletRuleRegistry(transletRuleRegistry);
        return activityContext;
    }

    protected void startContextReloader() {
        if (autoReloadEnabled && aspectranClassLoader != null) {
            contextReloader = new ActivityContextReloader(serviceController);
            contextReloader.setResources(aspectranClassLoader.getAllResources());
            contextReloader.start(scanIntervalSeconds);
        }
    }

    protected void stopContextReloader() {
        if (contextReloader != null) {
            contextReloader.stop();
            contextReloader = null;
        }
    }

    private AspectranClassLoader newAspectranClassLoader() throws InvalidResourceException {
        if (aspectranClassLoader == null || hardReload) {
            AspectranClassLoader acl = new AspectranClassLoader();
            if (resourceLocations != null && resourceLocations.length > 0) {
                acl.setResourceLocations(resourceLocations);
            }
            aspectranClassLoader = acl;
        }
        return aspectranClassLoader;
    }

    private ActivityEnvironment createActivityEnvironment(ActivityRuleAssistant assistant, ActivityContext activityContext) {
        EnvironmentProfiles environmentProfiles = assistant.getEnvironmentProfiles();
        ActivityEnvironment environment = new ActivityEnvironment(environmentProfiles, activityContext);
        if (propertyItemRuleMap != null && !propertyItemRuleMap.isEmpty()) {
            environment.setPropertyItemRuleMap(propertyItemRuleMap);
        }
        for (EnvironmentRule environmentRule : assistant.getEnvironmentRules()) {
            String[] profiles = StringUtils.splitCommaDelimitedString(environmentRule.getProfile());
            if (environmentProfiles.acceptsProfiles(profiles)) {
                if (environmentRule.getPropertyItemRuleMapList() != null) {
                    for (ItemRuleMap propertyItemRuleMap : environmentRule.getPropertyItemRuleMapList()) {
                        String[] profiles2 = StringUtils.splitCommaDelimitedString(propertyItemRuleMap.getProfile());
                        if (environmentProfiles.acceptsProfiles(profiles2)) {
                            environment.addPropertyItemRule(propertyItemRuleMap);
                        }
                    }
                }
            }
        }
        return environment;
    }

    /**
     * Initialize the aspect rule registry.
     * @param assistant the activity rule assistant
     */
    private void initAspectRuleRegistry(ActivityRuleAssistant assistant) {
        AspectRuleRegistry aspectRuleRegistry = assistant.getAspectRuleRegistry();
        BeanRuleRegistry beanRuleRegistry = assistant.getBeanRuleRegistry();
        TransletRuleRegistry transletRuleRegistry = assistant.getTransletRuleRegistry();

        for (AspectRule aspectRule : aspectRuleRegistry.getAspectRules()) {
            PointcutRule pointcutRule = aspectRule.getPointcutRule();
            if (pointcutRule != null) {
                Pointcut pointcut = PointcutFactory.createPointcut(pointcutRule);
                aspectRule.setPointcut(pointcut);
            }
        }

        boolean pointcutPatternVerifiable = assistant.isPointcutPatternVerifiable();

        AspectAdviceRulePreRegister preRegister = new AspectAdviceRulePreRegister(aspectRuleRegistry);
        preRegister.setPointcutPatternVerifiable(pointcutPatternVerifiable || logger.isDebugEnabled());
        preRegister.register(beanRuleRegistry);
        preRegister.register(transletRuleRegistry);

        // check invalid pointcut pattern
        if (pointcutPatternVerifiable || logger.isDebugEnabled()) {
            int invalidPointcutPatterns = 0;
            for (AspectRule aspectRule : aspectRuleRegistry.getAspectRules()) {
                Pointcut pointcut = aspectRule.getPointcut();
                if (pointcut != null) {
                    List<PointcutPatternRule> pointcutPatternRuleList = pointcut.getPointcutPatternRuleList();
                    if (pointcutPatternRuleList != null) {
                        for (PointcutPatternRule ppr : pointcutPatternRuleList) {
                            PointcutPattern pp = ppr.getPointcutPattern();
                            if (pp != null) {
                                if (pp.getBeanIdPattern() != null && ppr.getMatchedBeanIdCount() == 0) {
                                    invalidPointcutPatterns++;
                                    String msg = "No beans matching to '" + pp.getBeanIdPattern() +
                                            "'; aspectRule " + aspectRule;
                                    if (pointcutPatternVerifiable) {
                                        logger.error(msg);
                                    } else if (logger.isDebugEnabled()) {
                                        logger.debug(msg);
                                    }
                                }
                                if (pp.getClassNamePattern() != null && ppr.getMatchedClassNameCount() == 0) {
                                    invalidPointcutPatterns++;
                                    String msg = "No beans matching to '@class:" + pp.getClassNamePattern() +
                                            "'; aspectRule " + aspectRule;
                                    if (pointcutPatternVerifiable) {
                                        logger.error(msg);
                                    } else if (logger.isDebugEnabled()) {
                                        logger.debug(msg);
                                    }
                                }
                                if (pp.getMethodNamePattern() != null && ppr.getMatchedMethodNameCount() == 0) {
                                    invalidPointcutPatterns++;
                                    String msg = "No beans have methods matching to '^" + pp.getMethodNamePattern() +
                                            "'; aspectRule " + aspectRule;
                                    if (pointcutPatternVerifiable) {
                                        logger.error(msg);
                                    } else if (logger.isDebugEnabled()) {
                                        logger.debug(msg);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (invalidPointcutPatterns > 0) {
                String msg = "Invalid pointcut detected: " + invalidPointcutPatterns +
                        "; Please check the logs for more information";
                if (pointcutPatternVerifiable) {
                    logger.error(msg);
                    throw new InvalidPointcutPatternException(msg);
                } else {
                    logger.debug(msg);
                }
            }
        }
    }

}
