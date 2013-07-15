/*
 *  Copyright (c) 2010 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.rule;

import com.aspectran.core.rule.ability.ArgumentPossessable;
import com.aspectran.core.rule.ability.PropertyPossessable;

/**
 * <p>Created: 2008. 03. 22 오후 5:50:35</p>
 */
public class BeanActionRule implements ArgumentPossessable, PropertyPossessable {
	
	protected String id;
	
	protected String beanId;
	
	protected String methodName;

	protected ItemRuleMap propertyItemRuleMap;
	
	protected ItemRuleMap argumentItemRuleMap;
	
	protected Boolean hidden;

	/**
	 * Gets the action id.
	 * 
	 * @return the action id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the action id.
	 * 
	 * @param id the new action id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	/**
	 * Gets the action method name.
	 * 
	 * @return the action method name
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Sets the action method name.
	 * 
	 * @param methodName the new action method name
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	/**
	 * Gets the hidden.
	 * 
	 * @return the hidden
	 */
	public Boolean getHidden() {
		return hidden;
	}

	/**
	 * Sets the hidden.
	 * 
	 * @param hidden the new hidden
	 */
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Gets the parameter rule map for properties.
	 * 
	 * @return the parameter rule map
	 */
	public ItemRuleMap getPropertyItemRuleMap() {
		return propertyItemRuleMap;
	}

	/**
	 * Sets the parameter rule map for properties.
	 * 
	 * @param parameterRuleMap the new parameter rule map
	 */
	public void setPropertyItemRuleMap(ItemRuleMap propertyItemRuleMap) {
		this.propertyItemRuleMap = propertyItemRuleMap;
	}

	/**
	 * Adds the parameter rule for property.
	 * 
	 * @param parameterRule the item rule for property
	 */
	public void addPropertyItemRule(ItemRule propertyItemRule) {
		if(propertyItemRuleMap == null) 
			propertyItemRuleMap = new ItemRuleMap();
		
		propertyItemRuleMap.putItemRule(propertyItemRule);
	}
	
	/**
	 * Gets the argument item rule map.
	 *
	 * @return the argument item rule map
	 */
	public ItemRuleMap getArgumentItemRuleMap() {
		return argumentItemRuleMap;
	}

	/**
	 * Sets the argument item rule map.
	 *
	 * @param argumentItemRuleMap the new argument item rule map
	 */
	public void setArgumentItemRuleMap(ItemRuleMap argumentItemRuleMap) {
		this.argumentItemRuleMap = argumentItemRuleMap;
	}

	/**
	 * Adds the item rule for argument.
	 * 
	 * @param parameterRule the item rule for argument
	 */
	public void addArgumentItemRule(ItemRule argumentItemRule) {
		if(argumentItemRuleMap == null) 
			argumentItemRuleMap = new ItemRuleMap();
		
		argumentItemRuleMap.putItemRule(argumentItemRule);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{id=").append(id);
		sb.append(", bean=").append(beanId);
		sb.append(", method=").append(methodName);
		sb.append(", hidden=").append(hidden);

		if(propertyItemRuleMap != null) {
			sb.append(", properties=[");
			int sbLength = sb.length();

			for(String name : propertyItemRuleMap.keySet()) {
				if(sb.length() > sbLength)
					sb.append(", ");
				
				sb.append(name);
			}

			sb.append("]");
		}
		
		if(argumentItemRuleMap != null) {
			sb.append(", arguments=[");
			int sbLength = sb.length();
			
			for(String name : argumentItemRuleMap.keySet()) {
				if(sb.length() > sbLength)
					sb.append(", ");
				
				sb.append(name);
			}
			
			sb.append("]");
		}
		
		sb.append("}");
		
		return sb.toString();
	}
}
