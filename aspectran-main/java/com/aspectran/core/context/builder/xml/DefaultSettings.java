/*
 *  Copyright (c) 2008 Jeong Ju Ho, All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aspectran.core.context.builder.xml;

import java.util.Map;

import com.aspectran.core.context.AspectranConstant;
import com.aspectran.core.type.DefaultSettingType;

/**
 * <p>Created: 2008. 03. 22 오후 5:48:09</p>
 */
public class DefaultSettings implements Cloneable {

	private String transletNamePattern;
	
	private String transletNamePatternPrefix;

	private String transletNamePatternSuffix;
	
	private String transletInterfaceClass;

	private String transletInstanceClass;
	
	private String activityDefaultHandler;
	
	private boolean useNamespaces = true;

	private boolean nullableContentId = true;
	
	private boolean nullableActionId = true;
	
	public DefaultSettings() {
	}
	
	public String getTransletNamePattern() {
		return transletNamePattern;
	}

	public void setTransletNamePattern(String transletNamePattern) {
		this.transletNamePattern = transletNamePattern;
		
		if(transletNamePattern != null) {
			int index = transletNamePattern.indexOf(AspectranConstant.TRANSLET_NAME_PATTERN_SEPARATOR);
			
			if(index != -1) {
				if(index == 0) {
					transletNamePatternPrefix = null;
					transletNamePatternSuffix = transletNamePattern.substring(1);
				} else if(index == (transletNamePattern.length() - 1)) {
					transletNamePatternPrefix = transletNamePattern.substring(0, transletNamePattern.length() - 1);
					transletNamePatternSuffix = null;
				} else {
					transletNamePatternPrefix = transletNamePattern.substring(0, index);
					transletNamePatternSuffix = transletNamePattern.substring(index + 1);
				}
			}
		}
	}
	
	public void setTransletNamePattern(String transletNamePatternPrefix, String transletNamePatternSuffix) {
		transletNamePattern = transletNamePatternPrefix + AspectranConstant.TRANSLET_NAME_PATTERN_SEPARATOR + transletNamePatternSuffix;
	}
	
	public void setTransletNamePatternPrefix(String transletNamePatternPrefix) {
		this.transletNamePatternPrefix = transletNamePatternPrefix;
		
		if(transletNamePatternSuffix != null)
			setTransletNamePattern(transletNamePatternPrefix, transletNamePatternSuffix);
	}
	
	public void setTransletNamePatternSuffix(String transletNamePatternSuffix) {
		this.transletNamePatternSuffix = transletNamePatternSuffix;
		
		if(transletNamePatternPrefix != null)
			setTransletNamePattern(transletNamePatternPrefix, transletNamePatternSuffix);
	}
	
	public String getTransletNamePatternPrefix() {
		return transletNamePatternPrefix;
	}

	public String getTransletNamePatternSuffix() {
		return transletNamePatternSuffix;
	}
	
	public String getTransletInterfaceClass() {
		return transletInterfaceClass;
	}

	public void setTransletInterfaceClass(String transletInterfaceClass) {
		this.transletInterfaceClass = transletInterfaceClass;
	}

	public String getTransletInstanceClass() {
		return transletInstanceClass;
	}

	public void setTransletInstanceClass(String transletInstanceClass) {
		this.transletInstanceClass = transletInstanceClass;
	}

	public String getActivityDefaultHandler() {
		return activityDefaultHandler;
	}

	public void setActivityDefaultHandler(String activityDefaultHandler) {
		this.activityDefaultHandler = activityDefaultHandler;
	}

	public boolean isUseNamespaces() {
		return useNamespaces;
	}

	public void setUseNamespaces(boolean useNamespaces) {
		this.useNamespaces = useNamespaces;
	}

	public boolean isNullableContentId() {
		return nullableContentId;
	}

	public void setNullableContentId(boolean nullableContentId) {
		this.nullableContentId = nullableContentId;
	}

	public boolean isNullableActionId() {
		return nullableActionId;
	}

	public void setNullableActionId(boolean nullableActionId) {
		this.nullableActionId = nullableActionId;
	}

	public void set(Map<DefaultSettingType, String> settings) {
		if(settings.get(DefaultSettingType.USE_NAMESPACES) != null)
			useNamespaces = Boolean.valueOf(settings.get(DefaultSettingType.USE_NAMESPACES));

		if(settings.get(DefaultSettingType.NULLABLE_CONTENT_ID) != null)
			nullableContentId = Boolean.valueOf(settings.get(DefaultSettingType.NULLABLE_CONTENT_ID));
		
		if(settings.get(DefaultSettingType.NULLABLE_ACTION_ID) != null)
			nullableActionId = Boolean.valueOf(settings.get(DefaultSettingType.NULLABLE_ACTION_ID));
		
		if(settings.get(DefaultSettingType.TRANSLET_NAME_PATTERN_PREFIX) != null)
			setTransletNamePatternPrefix(settings.get(DefaultSettingType.TRANSLET_NAME_PATTERN_PREFIX));
		
		if(settings.get(DefaultSettingType.TRANSLET_NAME_PATTERN_SUFFIX) != null)
			setTransletNamePatternSuffix(settings.get(DefaultSettingType.TRANSLET_NAME_PATTERN_SUFFIX));
		
		if(settings.get(DefaultSettingType.TRANSLET_NAME_PATTERN) != null)
			setTransletNamePattern(settings.get(DefaultSettingType.TRANSLET_NAME_PATTERN));
		
		if(settings.get(DefaultSettingType.TRANSLET_INTERFACE_CLASS) != null)
			transletInterfaceClass = settings.get(DefaultSettingType.TRANSLET_INTERFACE_CLASS);
		
		if(settings.get(DefaultSettingType.TRANSLET_IMPLEMENT_CLASS) != null)
			transletInstanceClass = settings.get(DefaultSettingType.TRANSLET_IMPLEMENT_CLASS);
		
		if(settings.get(DefaultSettingType.ACTIVITY_DEFAULT_HANDLER) != null)
			activityDefaultHandler = settings.get(DefaultSettingType.ACTIVITY_DEFAULT_HANDLER);
	}
	
	public Object clone() throws CloneNotSupportedException {                      
		return super.clone();              
	}
	
//	
//	public String toRequestUri(String transletPath) {
//		return toRequestUri(transletPathPatternPrefix, transletPathPatternSuffix, transletPath);
//	}
//	
//	/**
//	 * To service uri.
//	 * 
//	 * @param requestUriPatternPrefix the uri pattern prefix
//	 * @param requestUriPatternSuffix the uri pattern suffix
//	 * @param transletPath the translet path
//	 * 
//	 * @return the string
//	 * @deprecated
//	 */
//	public static String toRequestUri(String requestUriPatternPrefix, String requestUriPatternSuffix, String transletPath) {
//		if(requestUriPatternPrefix == null || requestUriPatternSuffix == null || transletPath == null)
//			return transletPath;
//		
//		StringBuilder sb = new StringBuilder(128);
//		
//		if(requestUriPatternPrefix != null)
//			sb.append(requestUriPatternPrefix);
//
//		sb.append(transletPath);
//		
//		if(requestUriPatternSuffix != null)
//			sb.append(requestUriPatternSuffix);
//		
//		return sb.toString();
//	}
//
//	public String toTransletPath(String requestUri) {
//		return toTransletPath(transletPathPatternPrefix, transletPathPatternSuffix, requestUri);
//	}
//
//	public static String toTransletPath(String requestUriPatternPrefix, String requestUriPatternSuffix, String requestUri) {
//		if(requestUriPatternPrefix == null || requestUriPatternSuffix == null)
//			return requestUri;
//		
//		int beginIndex;
//		int endIndex;
//		
//		if(requestUriPatternPrefix != null && requestUri.startsWith(requestUriPatternPrefix))
//			beginIndex = requestUriPatternPrefix.length();
//		else
//			beginIndex = 0;
//		
//		if(requestUriPatternSuffix != null && requestUri.endsWith(requestUriPatternSuffix))
//			endIndex = requestUri.length() - requestUriPatternSuffix.length();
//		else
//			endIndex = requestUri.length();
//		
//		return requestUri.substring(beginIndex, endIndex);
//	}
	
	
}
