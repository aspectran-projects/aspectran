/*
 * Copyright (c) 2008-2018 The Aspectran Project
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
package com.aspectran.daemon.service;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.activity.request.parameter.ParameterMap;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.context.rule.type.MethodType;
import com.aspectran.core.service.CoreService;

import java.util.Map;

/**
 * The Interface DaemonService.
 *
 * <p>Created: 2017. 10. 28.</p>
 */
public interface DaemonService extends CoreService {

    /**
     * Creates a new session adapter for the daemon service and returns.
     *
     * @return the session adapter
     */
    SessionAdapter newSessionAdapter();

    /**
     * Execute the translet.
     *
     * @param name the translet name
     * @param parameterMap the parameter map
     * @param attributeMap the attribute map
     * @return the {@code Translet} object
     */
    Translet translate(String name, ParameterMap parameterMap, Map<String, Object> attributeMap);

    /**
     * Execute the translet.
     *
     * @param name the translet name
     * @param method the request method
     * @param parameterMap the parameter map
     * @param attributeMap the attribute map
     * @return the {@code Translet} object
     */
    Translet translate(String name, MethodType method, ParameterMap parameterMap, Map<String, Object> attributeMap);

    /**
     * Evaluate the template with a set of parameters and a set of attributes.
     *
     * @param templateId the template id
     * @param parameterMap the parameter map
     * @param attributeMap the attribute map
     * @return the output string of the template
     */
    String template(String templateId, ParameterMap parameterMap, Map<String, Object> attributeMap);

}
