/**
 * Copyright 2008-2016 Juho Jeong
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
package com.aspectran.core.adapter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.aspectran.core.activity.request.AbstractRequest;

/**
 * The Class AbstractRequestAdapter.
  *
 * @since 2011. 3. 13.
*/
public abstract class AbstractRequestAdapter extends AbstractRequest implements RequestAdapter {

	protected Object adaptee;
	
	/**
	 * Instantiates a new AbstractRequestAdapter.
	 *
	 * @param adaptee the adaptee
	 */
	public AbstractRequestAdapter(Object adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAdaptee() {
		return (T)adaptee;
	}

	@Override
	public void setAdaptee(Object adaptee) {
		this.adaptee = adaptee; 
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		fillPrameterMap(params);
		return params;
	}
	
	@Override
	public void fillPrameterMap(Map<String, Object> parameterMap) {
		if(parameterMap == null)
			return;
		
		Enumeration<String> enm = getParameterNames();
		
	    while(enm.hasMoreElements()) {
	        String name = enm.nextElement();
	        String[] values = getParameterValues(name);
			if(values != null && values.length == 1) {
				parameterMap.put(name, values[0]);
			} else {
				parameterMap.put(name, values);
			}
	    }
	}
	
	@Override
	public Map<String, Object> getAttributeMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		fillAttributeMap(params);
		return params;
	}
	
	public void fillAttributeMap(Map<String, Object> attributeMap) {
		if(attributeMap == null)
			return;
		
		Enumeration<String> enm = getAttributeNames();
		
		while(enm.hasMoreElements()) {
			String name = enm.nextElement();
			Object value = getAttribute(name);
			attributeMap.put(name, value);
		}
	}
	
}
