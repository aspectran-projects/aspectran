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
package com.aspectran.core.context.rule.params;

import com.aspectran.core.context.rule.AppendRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.EnvironmentRule;
import com.aspectran.core.context.rule.ScheduleRule;
import com.aspectran.core.context.rule.TemplateRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.converter.RulesToParameters;
import com.aspectran.core.context.rule.type.DefaultSettingType;
import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterKey;
import com.aspectran.core.util.apon.Parameters;
import com.aspectran.core.util.apon.ValueType;

public class AspectranParameters extends AbstractParameters {

    public static final ParameterKey description;
    public static final ParameterKey settings;
    public static final ParameterKey environment;
    public static final ParameterKey typeAliases;
    public static final ParameterKey aspect;
    public static final ParameterKey bean;
    public static final ParameterKey schedule;
    public static final ParameterKey translet;
    public static final ParameterKey template;
    public static final ParameterKey append;

    private static final ParameterKey[] parameterKeys;

    static {
        description = new ParameterKey("description", ValueType.TEXT);
        settings = new ParameterKey("settings", SettingsParameters.class);
        typeAliases = new ParameterKey("typeAliases", TypeAliasesParameters.class);
        environment = new ParameterKey("environment", EnvironmentParameters.class, true, true);
        aspect = new ParameterKey("aspect", AspectParameters.class, true, true);
        bean = new ParameterKey("bean", BeanParameters.class, true, true);
        schedule = new ParameterKey("schedule", ScheduleParameters.class, true, true);
        translet = new ParameterKey("translet", TransletParameters.class, true, true);
        template = new ParameterKey("template", TemplateParameters.class, true, true);
        append = new ParameterKey("append", AppendParameters.class, true, true);

        parameterKeys = new ParameterKey[] {
                description,
                settings,
                typeAliases,
                environment,
                aspect,
                bean,
                schedule,
                translet,
                template,
                append
        };
    }

    public AspectranParameters() {
        super(parameterKeys);
    }

    public AspectranParameters setDescription(String desc) {
        putValue(description, desc);
        return this;
    }

    public AspectranParameters setTransletNamePattern(String namePattern) {
        SettingsParameters settingsParameters = touchParameters(settings);
        Parameters parameters = settingsParameters.touchParameters(SettingsParameters.setting);
        parameters.clearValue(DefaultSettingType.TRANSLET_NAME_PATTERN.toString());
        parameters.putValue(DefaultSettingType.TRANSLET_NAME_PATTERN.toString(), namePattern);
        return this;
    }

    public AspectranParameters setTransletNamePrefix(String prefixPattern) {
        SettingsParameters settingsParameters = touchParameters(settings);
        Parameters parameters = settingsParameters.touchParameters(SettingsParameters.setting);
        parameters.clearValue(DefaultSettingType.TRANSLET_NAME_PREFIX.toString());
        parameters.putValue(DefaultSettingType.TRANSLET_NAME_PREFIX.toString(), prefixPattern);
        return this;
    }

    public AspectranParameters setTransletNameSuffix(String suffixPattern) {
        SettingsParameters settingsParameters = touchParameters(settings);
        Parameters parameters = settingsParameters.touchParameters(SettingsParameters.setting);
        parameters.clearValue(DefaultSettingType.TRANSLET_NAME_SUFFIX.toString());
        parameters.putValue(DefaultSettingType.TRANSLET_NAME_SUFFIX.toString(), suffixPattern);
        return this;
    }

    public AspectranParameters setBeanProxifier(String proxifierName) {
        SettingsParameters settingsParameters = touchParameters(settings);
        Parameters parameters = settingsParameters.touchParameters(SettingsParameters.setting);
        parameters.clearValue(DefaultSettingType.BEAN_PROXIFIER.toString());
        parameters.putValue(DefaultSettingType.BEAN_PROXIFIER.toString(), proxifierName);
        return this;
    }

    public AspectranParameters setPointcutPatternVerifiable(boolean verifiable) {
        SettingsParameters settingsParameters = touchParameters(settings);
        Parameters parameters = settingsParameters.touchParameters(SettingsParameters.setting);
        parameters.clearValue(DefaultSettingType.POINTCUT_PATTERN_VERIFIABLE.toString());
        parameters.putValue(DefaultSettingType.POINTCUT_PATTERN_VERIFIABLE.toString(), verifiable);
        return this;
    }

    public AspectranParameters setDefaultTemplateEngineBean(String beanName) {
        SettingsParameters settingsParameters = touchParameters(settings);
        Parameters parameters = settingsParameters.touchParameters(SettingsParameters.setting);
        parameters.clearValue(DefaultSettingType.DEFAULT_TEMPLATE_ENGINE_BEAN.toString());
        parameters.putValue(DefaultSettingType.DEFAULT_TEMPLATE_ENGINE_BEAN.toString(), beanName);
        return this;
    }

    public AspectranParameters setDefaultSchedulerBean(String beanName) {
        SettingsParameters settingsParameters = touchParameters(settings);
        Parameters parameters = settingsParameters.touchParameters(SettingsParameters.setting);
        parameters.clearValue(DefaultSettingType.DEFAULT_SCHEDULER_BEAN.toString());
        parameters.putValue(DefaultSettingType.DEFAULT_SCHEDULER_BEAN.toString(), beanName);
        return this;
    }

    public AspectranParameters addTypeAlias(String alias, String type) {
        TypeAliasesParameters typeAliasesParameters = touchParameters(typeAliases);
        Parameters parameters = typeAliasesParameters.touchParameters(TypeAliasesParameters.typeAlias);
        parameters.clearValue(alias);
        parameters.putValue(alias, type);
        return this;
    }

    public AspectranParameters addRule(EnvironmentRule environmentRule) {
        putValue(environment, RulesToParameters.toEnvironmentParameters(environmentRule));
        return this;
    }

    public EnvironmentRule newEnvironmentRule() {
        EnvironmentRule environmentRule = new EnvironmentRule();
        addRule(environmentRule);
        return environmentRule;
    }

    public AspectranParameters addRule(AspectRule aspectRule) {
        putValue(aspect, RulesToParameters.toAspectParameters(aspectRule));
        return this;
    }

    public AspectRule newAspectRule() {
        AspectRule aspectRule = new AspectRule();
        addRule(aspectRule);
        return aspectRule;
    }

    public AspectranParameters addRule(BeanRule beanRule) {
        putValue(bean, RulesToParameters.toBeanParameters(beanRule));
        return this;
    }

    public BeanRule newBeanRule() {
        BeanRule beanRule = new BeanRule();
        addRule(beanRule);
        return beanRule;
    }

    public AspectranParameters addRule(ScheduleRule scheduleRule) {
        putValue(schedule, RulesToParameters.toScheduleParameters(scheduleRule));
        return this;
    }

    public ScheduleRule newScheduleRule() {
        ScheduleRule scheduleRule = new ScheduleRule();
        addRule(scheduleRule);
        return scheduleRule;
    }

    public AspectranParameters addRule(TransletRule transletRule) {
        putValue(translet, RulesToParameters.toTransletParameters(transletRule));
        return this;
    }

    public TransletRule newTransletRule() {
        TransletRule transletRule = new TransletRule();
        addRule(transletRule);
        return transletRule;
    }

    public AspectranParameters addRule(TemplateRule templateRule) {
        putValue(template, RulesToParameters.toTemplateParameters(templateRule));
        return this;
    }

    public TemplateRule newTemplateRule() {
        TemplateRule templateRule = new TemplateRule();
        addRule(templateRule);
        return templateRule;
    }

    public AspectranParameters addRule(AppendRule appendRule) {
        putValue(append, RulesToParameters.toAppendParameters(appendRule));
        return this;
    }

    public AppendRule newAppendRule() {
        AppendRule appendRule = new AppendRule();
        addRule(appendRule);
        return appendRule;
    }

    public AspectranParameters append(AspectranParameters aspectranParameters) {
        return append(aspectranParameters, null);
    }

    public AspectranParameters append(AspectranParameters aspectranParameters, String profile) {
        AppendParameters appendParameters = new AppendParameters();
        appendParameters.putValue(AppendParameters.aspectran, aspectranParameters);
        if (profile != null && !profile.isEmpty()) {
            appendParameters.putValue(AppendParameters.profile, profile);
        }
        putValue(append, appendParameters);
        return this;
    }

    public AspectranParameters newAspectranParameters() {
        return newAspectranParameters(null);
    }

    public AspectranParameters newAspectranParameters(String profile) {
        AspectranParameters aspectranParameters = new AspectranParameters();
        append(aspectranParameters, profile);
        return aspectranParameters;
    }

}
