/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.rule.ability;

import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;

/**
 * The Interface PropertyPossessable.
 * 
 * @author Juho Jeong
 * @since 2011. 2. 21.
 */
public interface PropertyPossessable {

	/**
	 * Gets the parameter rule map for properties.
	 * 
	 * @return the parameter rule map
	 */
	public ItemRuleMap getPropertyItemRuleMap();
	
	/**
	 * Sets the parameter rule map for properties.
	 * 
	 * @param parameterRuleMap the new parameter rule map
	 */
	public void setPropertyItemRuleMap(ItemRuleMap propertyItemRuleMap);

	/**
	 * Adds the parameter rule for property.
	 * 
	 * @param parameterRule the item rule for property
	 */
	public void addPropertyItemRule(ItemRule propertyItemRule);

}
