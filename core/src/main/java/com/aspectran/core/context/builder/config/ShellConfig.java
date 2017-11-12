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
package com.aspectran.core.context.builder.config;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ParameterValueType;

public class ShellConfig extends AbstractParameters {

    public static final ParameterDefinition prompt;
    public static final ParameterDefinition commands;
    public static final ParameterDefinition verbose;
    public static final ParameterDefinition greetings;
    public static final ParameterDefinition exposals;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        prompt = new ParameterDefinition("prompt", ParameterValueType.STRING);
        commands = new ParameterDefinition("commands", ParameterValueType.STRING, true);
        verbose = new ParameterDefinition("verbose", ParameterValueType.BOOLEAN);
        greetings = new ParameterDefinition("greetings", ParameterValueType.TEXT);
        exposals = new ParameterDefinition("exposals", ParameterValueType.STRING, true);

        parameterDefinitions = new ParameterDefinition[] {
                prompt,
                commands,
                verbose,
                greetings,
                exposals
        };
    }

    public ShellConfig() {
        super(parameterDefinitions);
    }

    public ShellConfig(String text) {
        super(parameterDefinitions, text);
    }

}
