/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.aspectran.core.util.apon;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface Parameters {
	
	public Map<String, ParameterValue> getParameterValueMap();

	public void setPrototype(Parameter parent);

	public Parameter getPrototype();
	
	public String getQualifiedName();
	
	public Parameter getParent();
	
	public String[] getParameterNames();
	
	public Set<String> getParameterNameSet();
	
	public Parameter getParameter(String name);
	
	public Parameter getParameter(ParameterDefine parameterDefine);
	
	public Object getValue(String name);

	public Object getValue(ParameterDefine parameterDefine);
	
	public void putValue(String name, Object value);
	
	public void putValue(ParameterDefine parameterDefine, Object value);
	
	public String getString(String name);

	public String getString(String name, String defaultValue);

	public String[] getStringArray(String name);

	public String getString(ParameterDefine parameterDefine);
	
	public String getString(ParameterDefine parameterDefine, String defaultValue);
	
	public String[] getStringArray(ParameterDefine parameterDefine);
	
	public List<String> getStringList(String name);

	public List<String> getStringList(ParameterDefine parameterDefine);
	
	public Integer getInt(String name);
	
	public int getInt(String name, int defaultValue);
	
	public Integer[] getIntArray(String name);
	
	public Integer getInt(ParameterDefine parameterDefine);
	
	public int getInt(ParameterDefine parameterDefine, int defaultValue);
	
	public Integer[] getIntArray(ParameterDefine parameterDefine);
	
	public List<Integer> getIntList(String name);
	
	public List<Integer> getIntList(ParameterDefine parameterDefine);
	
	public Long getLong(String name);
	
	public long getLong(String name, long defaultValue);
	
	public Long[] getLongArray(String name);
	
	public Long getLong(ParameterDefine parameterDefine);
	
	public long getLong(ParameterDefine parameterDefine, long defaultValue);
	
	public Long[] getLongArray(ParameterDefine parameterDefine);
	
	public List<Long> getLongList(String name);
	
	public List<Long> getLongList(ParameterDefine parameterDefine);

	public Float getFloat(String name);
	
	public float getFloat(String name, float defaultValue);
	
	public Float[] getFloatArray(String name);
	
	public Float getFloat(ParameterDefine parameterDefine);
	
	public float getFloat(ParameterDefine parameterDefine, float defaultValue);
	
	public Float[] getFloatArray(ParameterDefine parameterDefine);
	
	public List<Float> getFloatList(String name);
	
	public List<Float> getFloatList(ParameterDefine parameterDefine);

	public Double getDouble(String name);
	
	public double getDouble(String name, double defaultValue);
	
	public Double[] getDoubleArray(String name);

	public Double getDouble(ParameterDefine parameterDefine);
	
	public double getDouble(ParameterDefine parameterDefine, double defaultValue);
	
	public Double[] getDoubleArray(ParameterDefine parameterDefine);
	
	public List<Double> getDoubleList(String name);
	
	public List<Double> getDoubleList(ParameterDefine parameterDefine);

	public Boolean getBoolean(String name);
	
	public boolean getBoolean(String name, boolean defaultValue);

	public Boolean[] getBooleanArray(String name);

	public Boolean getBoolean(ParameterDefine parameterDefine);
	
	public boolean getBoolean(ParameterDefine parameterDefine, boolean defaultValue);
	
	public Boolean[] getBooleanArray(ParameterDefine parameterDefine);
	
	public List<Boolean> getBooleanList(String name);
	
	public List<Boolean> getBooleanList(ParameterDefine parameterDefine);

	public <T extends Parameters> T getParameters(String name);

	public <T extends Parameters> T[] getParametersArray(String name);
	
	public <T extends Parameters> T getParameters(ParameterDefine parameterDefine);
	
	public <T extends Parameters> T[] getParametersArray(ParameterDefine parameterDefine);
	
	public <T extends Parameters> List<T> getParametersList(String name);
	
	public <T extends Parameters> List<T> getParametersList(ParameterDefine parameterDefine);
	
	public ParameterValue newParameterValue(String name, ParameterValueType parameterValueType);
	
	public ParameterValue newParameterValue(String name, ParameterValueType parameterValueType, boolean array);
	
	public <T extends Parameters> T newParameters(String name);
	
	public <T extends Parameters> T newParameters(ParameterDefine parameterDefine);
	
	public <T extends Parameters> T touchParameters(String name);
	
	public <T extends Parameters> T touchParameters(ParameterDefine parameterDefine);
	
	public boolean isAddable();
	
	public String describe();
	
}
