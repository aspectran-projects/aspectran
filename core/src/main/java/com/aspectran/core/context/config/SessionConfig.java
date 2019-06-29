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

import com.aspectran.core.context.rule.type.SessionStoreType;
import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ValueType;

public class SessionConfig extends AbstractParameters {

    private static final ParameterDefinition timeout;
    private static final ParameterDefinition evictionPolicy;
    private static final ParameterDefinition saveOnCreate;
    private static final ParameterDefinition saveOnInactiveEviction;
    private static final ParameterDefinition removeUnloadableSessions;
    private static final ParameterDefinition storeType;
    private static final ParameterDefinition fileStore;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        timeout = new ParameterDefinition("timeout", ValueType.INT);
        evictionPolicy = new ParameterDefinition("evictionPolicy", ValueType.INT);
        saveOnCreate = new ParameterDefinition("saveOnCreate", ValueType.BOOLEAN);
        saveOnInactiveEviction = new ParameterDefinition("saveOnInactiveEviction", ValueType.BOOLEAN);
        removeUnloadableSessions = new ParameterDefinition("removeUnloadableSessions", ValueType.BOOLEAN);
        storeType = new ParameterDefinition("storeType", ValueType.STRING);
        fileStore = new ParameterDefinition("fileStore", SessionFileStoreConfig.class);

        parameterDefinitions = new ParameterDefinition[] {
                timeout,
                evictionPolicy,
                saveOnCreate,
                saveOnInactiveEviction,
                removeUnloadableSessions,
                storeType,
                fileStore
        };
    }

    public SessionConfig() {
        super(parameterDefinitions);
    }

    public int getTimeout() {
        return getInt(timeout, -1);
    }

    public SessionConfig setTimeout(int timeout) {
        putValue(SessionConfig.timeout, timeout);
        return this;
    }

    public boolean hasTimeout() {
        return hasValue(timeout);
    }

    public String getStoreType() {
        return getString(storeType);
    }

    public SessionConfig setStoreType(SessionStoreType sessionStoreType) {
        putValue(storeType, sessionStoreType.toString());
        return this;
    }

    public SessionFileStoreConfig getFileStoreConfig() {
        return getParameters(fileStore);
    }

    public SessionFileStoreConfig newFileStoreConfig() {
        return newParameters(fileStore);
    }

    public SessionFileStoreConfig touchFileStoreConfig() {
        return touchParameters(fileStore);
    }

}
