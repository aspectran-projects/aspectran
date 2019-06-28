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
package com.aspectran.core.util.apon;

import com.aspectran.core.util.ToStringBuilder;

/**
 * A class for defining parameter attributes.
 */
public class ParameterDefinition {

    private final String name;

    private final ParameterValueType valueType;

    private final Class<? extends AbstractParameters> parametersClass;

    private final boolean array;

    private final boolean noBracket;

    public ParameterDefinition(String name, ParameterValueType valueType) {
        this(name, valueType, false);
    }

    public ParameterDefinition(String name, ParameterValueType valueType, boolean array) {
        this(name, valueType, array, false);
    }

    public ParameterDefinition(String name, ParameterValueType valueType, boolean array, boolean noBracket) {
        this.name = name;
        this.valueType = valueType;
        this.parametersClass = null;
        this.array = array;
        this.noBracket = (array && valueType == ParameterValueType.PARAMETERS && noBracket);
    }

    public ParameterDefinition(String name, Class<? extends AbstractParameters> parametersClass) {
        this(name, parametersClass, false);
    }

    public ParameterDefinition(String name, Class<? extends AbstractParameters> parametersClass, boolean array) {
        this(name, parametersClass, array, false);
    }

    public ParameterDefinition(String name, Class<? extends AbstractParameters> parametersClass, boolean array, boolean noBracket) {
        this.name = name;
        this.valueType = ParameterValueType.PARAMETERS;
        this.parametersClass = parametersClass;
        this.array = array;
        this.noBracket = (array && noBracket);
    }

    public String getName() {
        return name;
    }

    public ParameterValueType getValueType() {
        return valueType;
    }

    public boolean isArray() {
        return array;
    }

    public boolean isNoBracket() {
        return noBracket;
    }

    public ParameterValue newParameterValue() {
        ParameterValue parameterValue;
        if (valueType == ParameterValueType.PARAMETERS && parametersClass != null) {
            parameterValue = new ParameterValue(name, parametersClass, array, noBracket, true);
        } else {
            parameterValue = new ParameterValue(name, valueType, array, noBracket, true);
        }
        return parameterValue;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("name", name);
        tsb.append("valueType", valueType);
        tsb.append("class", parametersClass);
        tsb.append("array", array);
        return tsb.toString();
    }

}
