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

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ValueType;

public class CallParameters extends AbstractParameters {

    public static final ParameterDefinition bean;
    public static final ParameterDefinition template;
    public static final ParameterDefinition parameter;
    public static final ParameterDefinition attribute;
    public static final ParameterDefinition property;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        bean = new ParameterDefinition("bean", ValueType.STRING);
        template = new ParameterDefinition("template", ValueType.STRING);
        parameter = new ParameterDefinition("parameter", ValueType.STRING);
        attribute = new ParameterDefinition("attribute", ValueType.STRING);
        property = new ParameterDefinition("property", ValueType.STRING);

        parameterDefinitions = new ParameterDefinition[] {
                bean,
                template,
                parameter,
                attribute,
                property
        };
    }

    public CallParameters() {
        super(parameterDefinitions);
    }

}
