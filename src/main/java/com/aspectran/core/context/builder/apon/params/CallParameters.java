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
package com.aspectran.core.context.builder.apon.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ParameterValueType;

public class CallParameters extends AbstractParameters {

    public static final ParameterDefinition bean;
    public static final ParameterDefinition template;
    public static final ParameterDefinition parameter;
    public static final ParameterDefinition attribute;
    public static final ParameterDefinition property;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        bean = new ParameterDefinition("bean", ParameterValueType.STRING);
        template = new ParameterDefinition("template", ParameterValueType.STRING);
        parameter = new ParameterDefinition("parameter", ParameterValueType.STRING);
        attribute = new ParameterDefinition("attribute", ParameterValueType.STRING);
        property = new ParameterDefinition("property", ParameterValueType.STRING);

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

    public CallParameters(String text) {
        super(parameterDefinitions, text);
    }

}
