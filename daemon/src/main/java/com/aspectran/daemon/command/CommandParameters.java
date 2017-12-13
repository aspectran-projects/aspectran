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
package com.aspectran.daemon.command;

import com.aspectran.core.context.rule.params.ItemHolderParameters;
import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ParameterValueType;

/**
 * <p>Created: 2017. 12. 11.</p>
 */
public class CommandParameters extends AbstractParameters {

    public static final ParameterDefinition command;
    public static final ParameterDefinition bean;
    public static final ParameterDefinition method;
    public static final ParameterDefinition arguments;
    public static final ParameterDefinition translet;
    public static final ParameterDefinition template;
    public static final ParameterDefinition parameters;
    public static final ParameterDefinition attributes;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        command = new ParameterDefinition("command", ParameterValueType.STRING);
        bean = new ParameterDefinition("bean", ParameterValueType.STRING);
        method = new ParameterDefinition("method", ParameterValueType.STRING);
        arguments = new ParameterDefinition("arguments", ItemHolderParameters.class);
        translet = new ParameterDefinition("translet", ParameterValueType.STRING);
        template = new ParameterDefinition("template", ParameterValueType.STRING);
        parameters = new ParameterDefinition("parameters", ItemHolderParameters.class);
        attributes = new ParameterDefinition("attributes", ItemHolderParameters.class);

        parameterDefinitions = new ParameterDefinition[] {
                command,
                bean,
                method,
                arguments,
                translet,
                template,
                parameters,
                attributes
        };
    }

    public CommandParameters() {
        super(parameterDefinitions);
    }

    public CommandParameters(String text) {
        super(parameterDefinitions, text);
    }

}
