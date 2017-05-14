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
package com.aspectran.core.context.rule.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ParameterValueType;

public class PointcutParameters extends AbstractParameters {

    public static final ParameterDefinition type;
    public static final ParameterDefinition plus;
    public static final ParameterDefinition minus;
    public static final ParameterDefinition include;
    public static final ParameterDefinition execlude;

    private static final ParameterDefinition[] parameterDefinitions;

    static {
        type = new ParameterDefinition("type", ParameterValueType.STRING);
        plus = new ParameterDefinition("+", ParameterValueType.STRING, true, true);
        minus = new ParameterDefinition("-", ParameterValueType.STRING, true, true);
        include = new ParameterDefinition("include", PointcutTargetParameters.class, true, true);
        execlude = new ParameterDefinition("execlude", PointcutTargetParameters.class, true, true);

        parameterDefinitions = new ParameterDefinition[] {
            type,
            plus,
            minus,
            include,
            execlude
        };
    }

    public PointcutParameters() {
        super(parameterDefinitions);
    }

    public PointcutParameters(String text) {
        super(parameterDefinitions, text);
    }

    public void addIncludePattern(String pattern) {
        putValue(plus, pattern);
    }

    public void addExecludePattern(String pattern) {
        putValue(minus, pattern);
    }

}
