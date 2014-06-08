/*
 *  Copyright (c) 2009 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.context.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.context.AspectranConstant;
import com.aspectran.core.util.ArrayStack;
import com.aspectran.core.util.ClassUtils;
import com.aspectran.core.var.rule.AspectRule;
import com.aspectran.core.var.rule.AspectRuleMap;
import com.aspectran.core.var.rule.BeanRule;
import com.aspectran.core.var.rule.BeanRuleMap;
import com.aspectran.core.var.rule.TransletRule;
import com.aspectran.core.var.rule.TransletRuleMap;
import com.aspectran.core.var.type.DefaultSettingType;


/**
 * <p>Created: 2008. 04. 01 오후 10:25:35</p>
 */
public class ContextBuilderAssistant {

	private final Logger logger = LoggerFactory.getLogger(ContextBuilderAssistant.class);
	
	private ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
	
	private ArrayStack objectStack = new ArrayStack();
	
	private Map<String, String> typeAliases = new HashMap<String, String>();
	
	private String namespace;
	
	private Map<DefaultSettingType, String> settings = new HashMap<DefaultSettingType, String>();
	
	private DefaultSettings defaultSettings = new DefaultSettings();

	private BeanReferenceInspector beanReferenceInspector = new BeanReferenceInspector();
	
	private AspectRuleMap aspectRuleMap = new AspectRuleMap();
	
	private BeanRuleMap beanRuleMap = new BeanRuleMap();
	
	private TransletRuleMap transletRuleMap = new TransletRuleMap();
	
	private List<ImportResource> importResourceList = new ArrayList<ImportResource>();

	private String applicationBasePath;
	
	public ContextBuilderAssistant(String applicationBasePath) {
		if(applicationBasePath == null)
			this.applicationBasePath = new File(".").getAbsoluteFile().toString();
		else
			this.applicationBasePath = applicationBasePath;
		
		logger.info("Application base directory path is [" + applicationBasePath + "]");
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Push object.
	 * 
	 * @param object the item
	 */
	public void pushObject(Object object) {
		objectStack.push(object);
	}
	
	/**
	 * Pop object.
	 * 
	 * @return the object
	 */
	public Object popObject() {
		return objectStack.pop();
	}
	
	/**
	 * Peek object.
	 * 
	 * @return the object
	 */
	public Object peekObject() {
		return objectStack.peek();
	}
	
	/**
	 * Peek object.
	 * 
	 * @return the object
	 */
	public Object peekObject(int n) {
		return objectStack.peek(n);
	}
	
	/**
	 * Clear object stack.
	 */
	public void clearObjectStack() {
		objectStack.clear();
	}
	
	/**
	 * Clear type aliases.
	 */
	public void clearTypeAliases() {
		typeAliases.clear();
	}

	public Map<DefaultSettingType, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<DefaultSettingType, String> settings) {
		this.settings = settings;
		applySettings();
	}

	public void putSetting(DefaultSettingType settingType, String value) {
		settings.put(settingType, value);
	}
	
	public Object getSetting(DefaultSettingType settingType) {
		return settings.get(settingType);
	}
	
	/**
	 * Adds the type alias.
	 * 
	 * @param alias the alias
	 * @param type the type
	 */
	public void addTypeAlias(String alias, String type) {
		typeAliases.put(alias, type);
	}
	
	/**
	 * Gets the alias type.
	 * 
	 * @param alias the alias
	 * 
	 * @return the alias type
	 */
	public String getAliasType(String alias) {
		return typeAliases.get(alias);
	}
	
	/**
	 * Sets the namespace.
	 * 
	 * @param namespace the new namespace
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public void applySettings() {
		defaultSettings.set(getSettings());
	}
	
	/**
	 * Returns the trnaslet name of the prefix and suffix are combined.
	 * 
	 * @param transletName
	 * @return
	 */
	public String getFullTransletName(String transletName) {
		if(transletName != null && transletName.length() > 0 && transletName.charAt(0) == AspectranConstant.TRANSLET_NAME_SEPARATOR)
			return transletName;
		
		if(defaultSettings.getTransletNamePatternPrefix() == null && 
				defaultSettings.getTransletNamePatternSuffix() == null)
			return transletName;
		
		StringBuilder sb = new StringBuilder();
		
		if(defaultSettings.getTransletNamePatternPrefix() != null)
			sb.append(defaultSettings.getTransletNamePatternPrefix());
		
		sb.append(transletName);
		
		if(defaultSettings.getTransletNamePatternSuffix() != null)
			sb.append(defaultSettings.getTransletNamePatternSuffix());
		
		return sb.toString();
	}
	
	/**
	 * Apply namespace for a translet.
	 * 
	 * @param transletName the name
	 * 
	 * @return the string
	 */
	public String applyNamespaceForTranslet(String transletName) {
		if(transletName != null && transletName.length() > 0 && transletName.charAt(0) == AspectranConstant.TRANSLET_NAME_SEPARATOR)
			return transletName;
		
		StringBuilder sb = new StringBuilder();
		
		if(defaultSettings.getTransletNamePatternPrefix() != null)
			sb.append(defaultSettings.getTransletNamePatternPrefix());
		
		if(defaultSettings.isUseNamespaces() && namespace != null) {
			sb.append(namespace);
			sb.append(AspectranConstant.TRANSLET_NAME_SEPARATOR);
		}
		
		sb.append(transletName);
		
		if(defaultSettings.getTransletNamePatternSuffix() != null)
			sb.append(defaultSettings.getTransletNamePatternSuffix());
		
		return sb.toString();
	}
	
	public String applyNamespaceForBean(String beanId) {
		if(!defaultSettings.isUseNamespaces() || namespace == null)
			return beanId;
		
		StringBuilder sb = new StringBuilder();
		sb.append(namespace);
		sb.append(AspectranConstant.ID_SEPARATOR);
		sb.append(beanId);
		
		return sb.toString();
	}
/*	
	public String replaceTransletNameSuffix(String name, String transletNameSuffix) {
		if(inheritedAspectranSettings.getTransletNamePatternSuffix() == null)
			return name + AspectranConstant.TRANSLET_NAME_EXTENSION_DELIMITER + transletNameSuffix;
		
		int index = name.indexOf(inheritedAspectranSettings.getTransletNamePatternSuffix());
		
		StringBuilder sb = new StringBuilder();
		sb.append(name.substring(0, index));
		sb.append(AspectranConstant.TRANSLET_NAME_EXTENSION_DELIMITER);
		sb.append(transletNameSuffix);
		
		return sb.toString();
	}
*/	
	/**
	 * Checks if is allow null content id.
	 * 
	 * @return true, if is allow null content id
	 */
	public boolean isNullableContentId() {
		return defaultSettings.isNullableContentId();
	}

	/**
	 * Checks if is allow null action id.
	 * 
	 * @return true, if is allow null action id
	 */
	public boolean isNullableActionId() {
		return defaultSettings.isNullableActionId();
	}

	public String getApplicationBasePath() {
		return applicationBasePath;
	}
	
	public DefaultSettings getDefaultSettings() {
		return defaultSettings;
	}

	public void setDefaultSettings(DefaultSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	/**
	 * Gets the bean rule map.
	 * 
	 * @return the bean rule map
	 */
	public BeanRuleMap getBeanRuleMap() {
		return beanRuleMap;
	}

	/**
	 * Sets the bean rule map.
	 * 
	 * @param beanRuleMap the new bean rule map
	 */
	public void setBeanRuleMap(BeanRuleMap beanRuleMap) {
		this.beanRuleMap = beanRuleMap;
	}
	
	/**
	 * Adds the bean rule.
	 *
	 * @param beanRule the bean rule
	 */
	public void addBeanRule(BeanRule beanRule) {
		beanRuleMap.putBeanRule(beanRule);
	}
	
	public AspectRuleMap getAspectRuleMap() {
		return aspectRuleMap;
	}

	public void setAspectRuleMap(AspectRuleMap aspectRuleMap) {
		this.aspectRuleMap = aspectRuleMap;
	}
	
	public void addAspectRule(AspectRule aspectRule) {
		aspectRuleMap.putAspectRule(aspectRule);
	}

	/**
	 * To real path as file.
	 * 
	 * @param filePath the file path
	 * 
	 * @return the file
	 */
	public File toRealPathFile(String filePath) {
		File file;

		if(applicationBasePath != null && !filePath.startsWith("/"))
			file = new File(applicationBasePath, filePath);
		else
			file = new File(filePath);
		
		return file;
	}
	
	public TransletRuleMap getTransletRuleMap() {
		return transletRuleMap;
	}

	public void addTransletRule(TransletRule transletRule) {
		transletRuleMap.put(transletRule.getName(), transletRule);
	}
	
	public String putBeanReference(String beanId, Object rule) {
		if(!beanRuleMap.containsKey(beanId)) {
			beanReferenceInspector.putRelation(beanId, rule);
		}
		
		return beanId;
	}
	
	public BeanReferenceInspector getBeanReferenceInspector() {
		return beanReferenceInspector;
	}
	
	public void addImportResource(ImportResource importResource) {
		importResourceList.add(importResource);
	}
	
	public List<ImportResource> getImportResourceList() {
		return importResourceList;
	}
	
}
