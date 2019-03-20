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
package com.aspectran.core.context.config;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ParameterValueType;

public class SchedulerConfig extends AbstractParameters {

    private static final ParameterDefinition startDelaySeconds;
    private static final ParameterDefinition waitOnShutdown;
    private static final ParameterDefinition startup;
    private static final ParameterDefinition exposals;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        startDelaySeconds = new ParameterDefinition("startDelaySeconds", ParameterValueType.INT);
        waitOnShutdown = new ParameterDefinition("waitOnShutdown", ParameterValueType.BOOLEAN);
        startup = new ParameterDefinition("startup", ParameterValueType.BOOLEAN);
        exposals = new ParameterDefinition("exposals", ExposalsConfig.class);

        parameterDefinitions = new ParameterDefinition[] {
                startDelaySeconds,
                waitOnShutdown,
                startup,
                exposals
        };
    }

    public SchedulerConfig() {
        super(parameterDefinitions);
    }

    public int getStartDelaySeconds() {
        return getInt(startDelaySeconds, -1);
    }

    public SchedulerConfig setStartDelaySeconds(int startDelaySeconds) {
        putValue(SchedulerConfig.startDelaySeconds, startDelaySeconds);
        return this;
    }

    public boolean isWaitOnShutdown() {
        return getBoolean(waitOnShutdown, false);
    }

    public SchedulerConfig setWaitOnShutdown(boolean waitOnShutdown) {
        putValue(SchedulerConfig.waitOnShutdown, waitOnShutdown);
        return this;
    }

    public boolean isStartup() {
        return getBoolean(startup, false);
    }

    public SchedulerConfig setStartup(boolean startup) {
        putValue(SchedulerConfig.startup, startup);
        return this;
    }

    public ExposalsConfig getExposalsConfig() {
        return getParameters(exposals);
    }

    public ExposalsConfig newExposalsConfig() {
        return newParameters(exposals);
    }

    public ExposalsConfig touchExposalsConfig() {
        return touchParameters(exposals);
    }

}
