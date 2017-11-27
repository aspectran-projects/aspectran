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
package com.aspectran.core.context.rule.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;

public class ConstructorParameters extends AbstractParameters {

    public static final ParameterDefinition arguments;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        arguments = new ParameterDefinition("arguments", ItemHolderParameters.class);

        parameterDefinitions = new ParameterDefinition[] {
                arguments
        };
    }

    public ConstructorParameters() {
        super(parameterDefinitions);
    }

    public ConstructorParameters(String text) {
        super(parameterDefinitions, text);
    }

}
