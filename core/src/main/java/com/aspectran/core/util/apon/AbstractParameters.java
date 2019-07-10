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

import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.ToStringBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractParameters implements Parameters {

    private final Map<String, ParameterValue> parameterValueMap;

    private final Map<String, ParameterValue> alternateParameterValueMap;

    private final boolean structureFixed;

    private Parameter identifier;

    protected AbstractParameters(ParameterKey[] parameterKeys) {
        Map<String, ParameterValue> valueMap = new LinkedHashMap<>();
        if (parameterKeys != null) {
            Map<String, ParameterValue> alternateValueMap = new HashMap<>();
            for (ParameterKey pk : parameterKeys) {
                ParameterValue pv = pk.newParameterValue();
                pv.setContainer(this);
                valueMap.put(pk.getName(), pv);
                if (pk.getAlternateName() != null) {
                    alternateValueMap.put(pk.getAlternateName(), pv);
                }
            }
            this.parameterValueMap = Collections.unmodifiableMap(valueMap);
            this.alternateParameterValueMap = (alternateValueMap.isEmpty() ?
                Collections.emptyMap() : Collections.unmodifiableMap(alternateValueMap));
            this.structureFixed = true;
        } else {
            this.parameterValueMap = valueMap;
            this.alternateParameterValueMap = Collections.emptyMap();
            this.structureFixed = false;
        }
    }

    @Override
    public boolean isStructureFixed() {
        return structureFixed;
    }

    @Override
    public Parameter getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(Parameter identifier) {
        this.identifier = identifier;
    }

    @Override
    public String getQualifiedName() {
        if (identifier != null) {
            return identifier.getQualifiedName();
        }
        return this.getClass().getName();
    }

    @Override
    public Parameter getParent() {
        if (identifier != null && identifier.getContainer() != null) {
            return identifier.getContainer().getIdentifier();
        } else {
            return null;
        }
    }

    @Override
    public boolean isRoot() {
        return (getParent() == null);
    }

    @Override
    public void updateContainer(Parameters parameters) {
        for (ParameterValue parameterValue : parameters.getParameterValueMap().values()) {
            parameterValue.setContainer(parameters);
        }
    }

    @Override
    public ParameterValue getParameterValue(String name) {
        ParameterValue pv = parameterValueMap.get(name);
        if (pv != null) {
            return pv;
        }
        if (alternateParameterValueMap != null) {
            return alternateParameterValueMap.get(name);
        }
        return null;
    }

    @Override
    public Map<String, ParameterValue> getParameterValueMap() {
        return parameterValueMap;
    }

    @Override
    public String[] getParameterNames() {
        return parameterValueMap.keySet().toArray(new String[0]);
    }

    @Override
    public Set<String> getParameterNameSet() {
        return parameterValueMap.keySet();
    }

    @Override
    public boolean hasParameter(String name) {
        return (parameterValueMap.containsKey(name) ||
            alternateParameterValueMap != null && alternateParameterValueMap.containsKey(name));
    }

    @Override
    public boolean hasParameter(ParameterKey parameterKey) {
        return hasParameter(parameterKey.getName());
    }

    @Override
    public boolean isAssigned(String name) {
        Parameter p = getParameterValue(name);
        return (p != null && p.isAssigned());
    }

    @Override
    public boolean isAssigned(ParameterKey parameterKey) {
        return isAssigned(parameterKey.getName());
    }

    @Override
    public boolean hasValue(String name) {
        Parameter p = getParameterValue(name);
        return (p != null && p.hasValue());
    }

    @Override
    public boolean hasValue(ParameterKey parameterKey) {
        Parameter p = getParameterValue(parameterKey.getName());
        return (p != null && p.hasValue());
    }

    @Override
    public Parameter getParameter(String name) {
        Parameter p = getParameterValue(name);
        if (structureFixed && p == null) {
            throw new UnknownParameterException(name, this);
        }
        return p;
    }

    @Override
    public Parameter getParameter(ParameterKey parameterKey) {
        return getParameter(parameterKey.getName());
    }

    @Override
    public Object getValue(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValue() : null);
    }

    @Override
    public Object getValue(ParameterKey parameterKey) {
        return getValue(parameterKey.getName());
    }

    @Override
    public void putAll(Parameters parameters) {
        if (structureFixed) {
            throw new IllegalStateException();
        }
        for (ParameterValue parameterValue : parameters.getParameterValueMap().values()) {
            parameterValue.setContainer(this);
        }
        parameterValueMap.putAll(parameters.getParameterValueMap());
    }

    @Override
    public void putValue(String name, Object value) {
        Parameter p = getParameter(name);
        if (p == null) {
            p = newParameterValue(name, ValueType.determineValueType(value));
        }
        p.putValue(value);
        if (value instanceof Parameters) {
            ((Parameters)value).updateContainer(this);
        }
    }

    @Override
    public void putValue(ParameterKey parameterKey, Object value) {
        putValue(parameterKey.getName(), value);
    }

    @Override
    public void putValueNonNull(String name, Object value) {
        if (value != null) {
            putValue(name, value);
        }
    }

    @Override
    public void putValueNonNull(ParameterKey parameterKey, Object value) {
        if (value != null) {
            putValue(parameterKey.getName(), value);
        }
    }

    @Override
    public void clearValue(String name) {
        if (structureFixed) {
            Parameter p = getParameter(name);
            if (p != null) {
                p.clearValue();
            }
        } else {
            parameterValueMap.remove(name);
        }
    }

    @Override
    public void clearValue(ParameterKey parameterKey) {
        clearValue(parameterKey.getName());
    }

    @Override
    public String getString(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsString() : null);
    }

    @Override
    public String getString(String name, String defaultValue) {
        String s = getString(name);
        return (s != null ? s : defaultValue);
    }

    @Override
    public String getString(ParameterKey parameterKey) {
        return getString(parameterKey.getName());
    }

    @Override
    public String getString(ParameterKey parameterKey, String defaultValue) {
        return getString(parameterKey.getName(), defaultValue);
    }

    @Override
    public String[] getStringArray(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsStringArray() : null);
    }

    @Override
    public String[] getStringArray(ParameterKey parameterKey) {
        return getStringArray(parameterKey.getName());
    }

    @Override
    public List<String> getStringList(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsStringList() : null);
    }

    @Override
    public List<String> getStringList(ParameterKey parameterKey) {
        return getStringList(parameterKey.getName());
    }

    @Override
    public Integer getInt(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsInt() : null);
    }

    @Override
    public int getInt(String name, int defaultValue) {
        Parameter p = getParameter(name);
        if (p != null) {
            Integer val = p.getValueAsInt();
            return (val != null ? val : defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Integer[] getIntArray(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsIntArray() : null);
    }

    @Override
    public Integer getInt(ParameterKey parameterKey) {
        return getInt(parameterKey.getName());
    }

    @Override
    public int getInt(ParameterKey parameterKey, int defaultValue) {
        return getInt(parameterKey.getName(), defaultValue);
    }

    @Override
    public Integer[] getIntArray(ParameterKey parameterKey) {
        return getIntArray(parameterKey.getName());
    }

    @Override
    public List<Integer> getIntList(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsIntList() : null);
    }

    @Override
    public List<Integer> getIntList(ParameterKey parameterKey) {
        return getIntList(parameterKey.getName());
    }

    @Override
    public Long getLong(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsLong() : null);
    }

    @Override
    public long getLong(String name, long defaultValue) {
        Parameter p = getParameter(name);
        if (p != null) {
            Long val = p.getValueAsLong();
            return (val != null ? val : defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Long getLong(ParameterKey parameterKey) {
        return getLong(parameterKey.getName());
    }

    @Override
    public long getLong(ParameterKey parameterKey, long defaultValue) {
        return getLong(parameterKey.getName(), defaultValue);
    }

    @Override
    public Long[] getLongArray(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsLongArray() : null);
    }

    @Override
    public Long[] getLongArray(ParameterKey parameterKey) {
        return getLongArray(parameterKey.getName());
    }

    @Override
    public List<Long> getLongList(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsLongList() : null);
    }

    @Override
    public List<Long> getLongList(ParameterKey parameterKey) {
        return getLongList(parameterKey.getName());
    }

    @Override
    public Float getFloat(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsFloat() : null);
    }

    @Override
    public float getFloat(String name, float defaultValue) {
        Parameter p = getParameter(name);
        if (p != null) {
            Float val = p.getValueAsFloat();
            return (val != null ? val : defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Float getFloat(ParameterKey parameterKey) {
        return getFloat(parameterKey.getName());
    }

    @Override
    public float getFloat(ParameterKey parameterKey, float defaultValue) {
        return getFloat(parameterKey.getName(), defaultValue);
    }

    @Override
    public Float[] getFloatArray(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsFloatArray() : null);
    }

    @Override
    public Float[] getFloatArray(ParameterKey parameterKey) {
        return getFloatArray(parameterKey.getName());
    }

    @Override
    public List<Float> getFloatList(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsFloatList() : null);
    }

    @Override
    public List<Float> getFloatList(ParameterKey parameterKey) {
        return getFloatList(parameterKey.getName());
    }

    @Override
    public Double getDouble(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsDouble() : null);
    }

    @Override
    public double getDouble(String name, double defaultValue) {
        Parameter p = getParameter(name);
        if (p != null) {
            Double val = p.getValueAsDouble();
            return (val != null ? val : defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Double getDouble(ParameterKey parameterKey) {
        return getDouble(parameterKey.getName());
    }

    @Override
    public double getDouble(ParameterKey parameterKey, double defaultValue) {
        return getDouble(parameterKey.getName(), defaultValue);
    }

    @Override
    public Double[] getDoubleArray(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsDoubleArray() : null);
    }

    @Override
    public Double[] getDoubleArray(ParameterKey parameterKey) {
        return getDoubleArray(parameterKey.getName());
    }

    @Override
    public List<Double> getDoubleList(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsDoubleList() : null);
    }

    @Override
    public List<Double> getDoubleList(ParameterKey parameterKey) {
        return getDoubleList(parameterKey.getName());
    }

    @Override
    public Boolean getBoolean(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsBoolean() : null);
    }

    @Override
    public boolean getBoolean(String name, boolean defaultValue) {
        Parameter p = getParameter(name);
        return (p != null ? BooleanUtils.toBoolean(p.getValueAsBoolean(), defaultValue) : defaultValue);
    }

    @Override
    public Boolean getBoolean(ParameterKey parameterKey) {
        return getBoolean(parameterKey.getName());
    }

    @Override
    public boolean getBoolean(ParameterKey parameterKey, boolean defaultValue) {
        return getBoolean(parameterKey.getName(), defaultValue);
    }

    @Override
    public Boolean[] getBooleanArray(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsBooleanArray() : null);
    }

    @Override
    public Boolean[] getBooleanArray(ParameterKey parameterKey) {
        return getBooleanArray(parameterKey.getName());
    }

    @Override
    public List<Boolean> getBooleanList(String name) {
        Parameter p = getParameter(name);
        return (p != null ? p.getValueAsBooleanList() : null);
    }

    @Override
    public List<Boolean> getBooleanList(ParameterKey parameterKey) {
        return getBooleanList(parameterKey.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Parameters> T getParameters(String name) {
        Parameter p = getParameter(name);
        return (p != null ? (T)p.getValue() : null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Parameters> T getParameters(ParameterKey parameterKey) {
        return (T)getParameters(parameterKey.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Parameters> T[] getParametersArray(String name) {
        Parameter p = getParameter(name);
        return (p != null ? (T[])p.getValueAsParametersArray() : null);
    }

    @Override
    public <T extends Parameters> T[] getParametersArray(ParameterKey parameterKey) {
        return getParametersArray(parameterKey.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Parameters> List<T> getParametersList(String name) {
        Parameter p = getParameter(name);
        return (p != null ? (List<T>)p.getValueAsParametersList() : null);
    }

    @Override
    public <T extends Parameters> List<T> getParametersList(ParameterKey parameterKey) {
        return getParametersList(parameterKey.getName());
    }

    @Override
    public ParameterValue newParameterValue(String name, ValueType valueType) {
        return newParameterValue(name, valueType, false);
    }

    @Override
    public ParameterValue newParameterValue(String name, ValueType valueType, boolean array) {
        if (structureFixed) {
            throw new IllegalStateException();
        }
        ParameterValue pv = new ParameterValue(name, valueType, array);
        pv.setContainer(this);
        parameterValueMap.put(name, pv);
        return pv;
    }

    @Override
    public <T extends Parameters> T newParameters(String name) {
        Parameter p = getParameter(name);
        if (structureFixed) {
            if (p == null) {
                throw new UnknownParameterException(name, this);
            }
        } else {
            if (p == null) {
                p = newParameterValue(name, ValueType.PARAMETERS);
            }
        }
        return p.newParameters(p);
    }

    @Override
    public <T extends Parameters> T newParameters(ParameterKey parameterKey) {
        return newParameters(parameterKey.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Parameters> T touchParameters(String name) {
        Parameters parameters = getParameters(name);
        if (parameters == null) {
            parameters = newParameters(name);
        }
        return (T)parameters;
    }

    @Override
    public <T extends Parameters> T touchParameters(ParameterKey parameterKey) {
        return touchParameters(parameterKey.getName());
    }

    @Override
    public void readFrom(String text) throws IOException {
        if (text != null) {
            AponReader.parse(text, this);
        }
    }

    @Override
    public String describe() {
        return describe(false);
    }

    @Override
    public String describe(boolean details) {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("qualifiedName", getQualifiedName());
        if (details) {
            tsb.append("parameters", parameterValueMap);
        } else {
            tsb.append("parameters", getParameterNames());
        }
        tsb.append("class", getClass().getName());
        tsb.append("parent", getParent());
        return tsb.toString();
    }

    @Override
    public String toString() {
        try {
            return new AponWriter().nullWritable(false).write(this).toString();
        } catch (IOException e) {
            return StringUtils.EMPTY;
        }
    }

}
