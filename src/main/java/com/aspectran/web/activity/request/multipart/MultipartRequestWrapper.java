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
package com.aspectran.web.activity.request.multipart;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;

import com.aspectran.core.activity.request.parameter.FileParameter;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.type.ItemType;

/**
 * This class functions as a wrapper around HttpServletRequest to provide
 * working getParameter methods for multipart requests.
 *
 * <p>Created: 2008. 04. 11 PM 1:47:48</p>
 */
public class MultipartRequestWrapper extends HttpServletRequestWrapper {

	private MultipartFormDataParser parser;

	/**
	 * Instantiates a new MultipartRequestWrapper.
	 *
	 * @param parser the handler
	 * @throws MultipartRequestException the multipart request exception
	 */
	public MultipartRequestWrapper(MultipartFormDataParser parser) {
		super(parser.getRequest());
		this.parser = parser;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return parser.getParameterNames();
	}
	
	@Override
	public String getParameter(String name) {
		return parser.getParameter(name);
	}

	@Override
	public String[] getParameterValues(String name) {
		return parser.getParameterValues(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<String, String[]>();
		Enumeration<String> enumeration = getParameterNames();
		
		while(enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();
			map.put(name, getParameterValues(name));
		}
		
		return map;
	}

	/**
	 * Gets the multipart file parameter names.
	 *
	 * @return the multipart item names
	 */
	public Enumeration<String> getFileParameterNames() {
        return parser.getFileParameterNames();
    }

	/**
	 * Gets the file parameter.
	 *
	 * @param name the name of the file parameter
	 * @return the file parameter
	 */
	public FileParameter getFileParameter(String name) {
		return parser.getFileParameter(name);
	}

	/**
	 * Gets the file parameters.
	 *
	 * @param name the name of the file parameter
	 * @return the file parameters
	 */
	public FileParameter[] getFileParameters(String name) {
		return parser.getFileParameters(name);
	}

	public List<FileParameter> getFileParameterList(String name) {
		return parser.getFileParameterList(name);
	}

	/**
	 * Checks if is max length exceeded.
	 *
	 * @return true, if is max length exceeded
	 */
	public boolean isMaxLengthExceeded() {
		return parser.isMaxLengthExceeded();
	}

	/**
	 * Returns the file parameter for the given parameter name.
	 *
	 * @param name     the file parameter name
	 * @param itemRule the item rule
	 * @return the file parameter object
	 */
	public Object getFileParameter(String name, ItemRule itemRule) {
		if(itemRule.getType() == ItemType.ARRAY) {
			return getFileParameters(name);
		} else if(itemRule.getType() == ItemType.LIST) {
			return getFileParameterList(name);
		} else if(itemRule.getType() == ItemType.SET) {
			List<FileParameter> fileParameterList = getFileParameterList(name);
			
			if(fileParameterList != null) {
				return new LinkedHashSet<FileParameter>(fileParameterList);
			}
		} else {
			return getFileParameter(name);
		}
		
		return null;
	}
	
}
