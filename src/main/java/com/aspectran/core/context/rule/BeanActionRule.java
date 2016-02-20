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
package com.aspectran.core.context.rule;

import java.lang.reflect.Method;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.context.aspect.AspectAdviceRuleRegistry;
import com.aspectran.core.context.rule.ability.ArgumentPossessable;
import com.aspectran.core.context.rule.ability.BeanReferenceInspectable;
import com.aspectran.core.context.rule.ability.PropertyPossessable;
import com.aspectran.core.context.rule.type.BeanReferrerType;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.MethodUtils;
import com.aspectran.core.util.ToStringBuilder;

/**
 * The Class BeanActionRule.
 * 
 * <p>Created: 2008. 03. 22 PM 5:50:35</p>
 */
public class BeanActionRule implements ArgumentPossessable, PropertyPossessable, BeanReferenceInspectable {

	public static Class<?>[] TRANSLET_ACTION_PARAMETER_TYPES = { Translet.class };

	private static final BeanReferrerType BEAN_REFERABLE_RULE_TYPE = BeanReferrerType.BEAN_ACTION_RULE;

	private String actionId;

	private String beanId;

	private Class<?> beanClass;

	private String methodName;

	private Method method;
	
	private boolean requiresTranslet;

	private ItemRuleMap propertyItemRuleMap;

	private ItemRuleMap argumentItemRuleMap;

	private Boolean hidden;
	
	private AspectAdviceRule aspectAdviceRule;
	
	private AspectAdviceRuleRegistry aspectAdviceRuleRegistry;

	/**
	 * Gets the action id.
	 * 
	 * @return the action id
	 */
	public String getActionId() {
		return actionId;
	}

	/**
	 * Sets the action id.
	 * 
	 * @param actionId the new action id
	 */
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	/**
	 * Gets bean id.
	 *
	 * @return the bean id
	 */
	public String getBeanId() {
		return beanId;
	}

	/**
	 * Sets bean id.
	 *
	 * @param beanId the bean id
	 */
	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
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

	public boolean isRequiresTranslet() {
		return requiresTranslet;
	}

	public void setRequiresTranslet(boolean requiresTranslet) {
		this.requiresTranslet = requiresTranslet;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * Returns whether to hide result of the action.
	 *
	 * @return true, if is hidden
	 */
	public Boolean getHidden() {
		return hidden;
	}

	/**
	 * Returns whether to hide result of the action.
	 *
	 * @return true, if is hidden
	 */
	public boolean isHidden() {
		return BooleanUtils.toBoolean(hidden);
	}

	/**
	 * Sets whether to hide result of the action.
	 * 
	 * @param hidden whether to hide result of the action
	 */
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public ItemRuleMap getPropertyItemRuleMap() {
		return propertyItemRuleMap;
	}

	@Override
	public void setPropertyItemRuleMap(ItemRuleMap propertyItemRuleMap) {
		this.propertyItemRuleMap = propertyItemRuleMap;
	}

	@Override
	public void addPropertyItemRule(ItemRule propertyItemRule) {
		if(propertyItemRuleMap == null) 
			propertyItemRuleMap = new ItemRuleMap();
		
		propertyItemRuleMap.putItemRule(propertyItemRule);
	}

	@Override
	public ItemRuleMap getArgumentItemRuleMap() {
		return argumentItemRuleMap;
	}

	@Override
	public void setArgumentItemRuleMap(ItemRuleMap argumentItemRuleMap) {
		this.argumentItemRuleMap = argumentItemRuleMap;
	}

	@Override
	public void addArgumentItemRule(ItemRule argumentItemRule) {
		if(argumentItemRuleMap == null) 
			argumentItemRuleMap = new ItemRuleMap();
		
		argumentItemRuleMap.putItemRule(argumentItemRule);
	}

	/**
	 * Gets the aspect advice rule.
	 *
	 * @return the aspect advice rule
	 */
	public AspectAdviceRule getAspectAdviceRule() {
		return aspectAdviceRule;
	}

	/**
	 * Sets the aspect advice rule.
	 *
	 * @param aspectAdviceRule the new aspect advice rule
	 */
	public void setAspectAdviceRule(AspectAdviceRule aspectAdviceRule) {
		this.aspectAdviceRule = aspectAdviceRule;
	}

	/**
	 * Gets the aspect advice rule registry.
	 *
	 * @return the aspect advice rule registry
	 */
	public AspectAdviceRuleRegistry getAspectAdviceRuleRegistry() {
		return aspectAdviceRuleRegistry;
	}

	/**
	 * Sets the aspect advice rule registry.
	 *
	 * @param aspectAdviceRuleRegistry the new aspect advice rule registry
	 */
	public void setAspectAdviceRuleRegistry(AspectAdviceRuleRegistry aspectAdviceRuleRegistry) {
		this.aspectAdviceRuleRegistry = aspectAdviceRuleRegistry;
	}

	@Override
	public BeanReferrerType getBeanReferrerType() {
		return BEAN_REFERABLE_RULE_TYPE;
	}
	
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder();
		tsb.append("id", actionId);
		tsb.append("bean", beanId);
		tsb.append("method", methodName);
		tsb.append("hidden", hidden);
		if(propertyItemRuleMap != null)
			tsb.append("properties", propertyItemRuleMap.keySet());
		if(argumentItemRuleMap != null)
			tsb.append("arguments", argumentItemRuleMap.keySet());
		if(aspectAdviceRule != null)
			tsb.append("aspectAdviceRule", aspectAdviceRule.toString(true));
		tsb.append("aspectAdviceRuleRegistry", aspectAdviceRuleRegistry);
		return tsb.toString();
	}
	
	/**
	 * Returns a new derived instance of BeanActionRule.
	 *
	 * @param id the action id
	 * @param beanId the bean id
	 * @param methodName the method name
	 * @param hidden whether to hide result of the action
	 * @return the bean action rule
	 */
	public static BeanActionRule newInstance(String id, String beanId, String methodName, Boolean hidden) {
		BeanActionRule beanActionRule = new BeanActionRule();
		beanActionRule.setActionId(id);
		beanActionRule.setBeanId(beanId);
		beanActionRule.setMethodName(methodName);
		beanActionRule.setHidden(hidden);

		return beanActionRule;
	}
	
	public static void checkTransletActionParameter(BeanActionRule beanActionRule, BeanRule beanRule) {
		Class<?> beanClass = beanRule.getTargetBeanClass();
		String methodName = beanActionRule.getMethodName();

		Method m1 = MethodUtils.getAccessibleMethod(beanClass, methodName, null);
		Method m2 = MethodUtils.getAccessibleMethod(beanClass, methodName, TRANSLET_ACTION_PARAMETER_TYPES);

		if(m2 != null) {
			beanActionRule.setMethod(m2);
			beanActionRule.setRequiresTranslet(true);
		} else {
			beanActionRule.setMethod(m1);
		}
	}
	
}
