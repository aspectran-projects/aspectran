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
package com.aspectran.core.var.rule;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.aspectran.core.context.bean.ablility.DisposableBean;
import com.aspectran.core.context.bean.ablility.InitializableBean;
import com.aspectran.core.context.bean.scan.BeanClassScanner;
import com.aspectran.core.util.MethodUtils;
import com.aspectran.core.util.wildcard.WildcardPattern;
import com.aspectran.core.var.apon.Parameters;
import com.aspectran.core.var.type.ScopeType;

/**
 * <p>
 * Created: 2009. 03. 09 오후 23:48:09
 * </p>
 */
public class BeanRule {

	protected String id;

	protected String className;

	protected Class<?> beanClass;

	protected ScopeType scopeType;

	protected String factoryMethodName;
	
	protected String initMethodName;
	
	protected String destroyMethodName;
	
	protected boolean lazyInit;

	protected ItemRuleMap constructorArgumentItemRuleMap;
	
	protected ItemRuleMap propertyItemRuleMap;
	
	private Object bean;
	
	private boolean registered;
	
	private boolean override;

	private boolean overrided;

	private boolean stealthily;
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the class type.
	 *
	 * @return the class type
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Sets the class type.
	 *
	 * @param className the new class name
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * Gets the bean class.
	 *
	 * @return the bean class
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}

	/**
	 * Sets the bean class.
	 *
	 * @param beanClass the new bean class
	 */
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Gets the scope type.
	 *
	 * @return the scope type
	 */
	public ScopeType getScopeType() {
		return scopeType;
	}

	/**
	 * Sets the scope type.
	 *
	 * @param scopeType the new scope type
	 */
	public void setScopeType(ScopeType scopeType) {
		this.scopeType = scopeType;
	}

	/**
	 * Gets the factory method name.
	 *
	 * @return the factory method
	 */
	public String getFactoryMethodName() {
		return factoryMethodName;
	}

	/**
	 * Sets the factory method name.
	 *
	 * @param factoryMethodName the new factory method name
	 */
	public void setFactoryMethodName(String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}

	/**
	 * Gets the inits the method name.
	 *
	 * @return the inits the method name
	 */
	public String getInitMethodName() {
		return initMethodName;
	}

	/**
	 * Sets the inits the method name.
	 *
	 * @param initMethodName the new inits the method name
	 */
	public void setInitMethodName(String initMethodName) {
		this.initMethodName = initMethodName;
	}

	/**
	 * Gets the destroy method name.
	 *
	 * @return the destroy method name
	 */
	public String getDestroyMethodName() {
		return destroyMethodName;
	}

	/**
	 * Sets the destroy method name.
	 *
	 * @param destroyMethodName the new destroy method name
	 */
	public void setDestroyMethodName(String destroyMethodName) {
		this.destroyMethodName = destroyMethodName;
	}

	/**
	 * Checks if is lazy init.
	 *
	 * @return true, if is lazy init
	 */
	public boolean isLazyInit() {
		return lazyInit;
	}

	/**
	 * Sets the lazy init.
	 *
	 * @param lazyInit the new lazy init
	 */
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	/**
	 * Gets the constructor argument item rule map.
	 *
	 * @return the constructor argument item rule map
	 */
	public ItemRuleMap getConstructorArgumentItemRuleMap() {
		return constructorArgumentItemRuleMap;
	}

	/**
	 * Sets the constructor argument item rule map.
	 *
	 * @param constructorArgumentItemRuleMap the new constructor argument item rule map
	 */
	public void setConstructorArgumentItemRuleMap(ItemRuleMap constructorArgumentItemRuleMap) {
		this.constructorArgumentItemRuleMap = constructorArgumentItemRuleMap;
	}

	/**
	 * Gets the property item rule map.
	 *
	 * @return the property item rule map
	 */
	public ItemRuleMap getPropertyItemRuleMap() {
		return propertyItemRuleMap;
	}

	/**
	 * Sets the property item rule map.
	 *
	 * @param propertyItemRuleMap the new property item rule map
	 */
	public void setPropertyItemRuleMap(ItemRuleMap propertyItemRuleMap) {
		this.propertyItemRuleMap = propertyItemRuleMap;
	}
	
	/**
	 * Gets the bean.
	 *
	 * @return the bean
	 */
	public Object getBean() {
		return bean;
	}

	/**
	 * Sets the bean.
	 *
	 * @param bean the new bean
	 */
	public void setBean(Object bean) {
		this.bean = bean;
	}

	/**
	 * Checks if is registered.
	 *
	 * @return true, if is registered
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * Sets the registered.
	 *
	 * @param registered the new registered
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}
	
	public boolean isOverrided() {
		return overrided;
	}
	
	public void setOverrided(boolean overrided) {
		this.overrided = overrided;
	}

	public boolean isStealthily() {
		return stealthily;
	}

	public void setStealthily(boolean stealthily) {
		this.stealthily = stealthily;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{id=").append(id);
		sb.append(", class=").append(className);
		sb.append(", scope=").append(scopeType);
		sb.append(", factoryMethod=").append(factoryMethodName);
		sb.append(", initMethod=").append(initMethodName);
		sb.append(", destroyMethod=").append(destroyMethodName);
		sb.append(", lazyInit=").append(lazyInit);
		sb.append(", override=").append(override);

		if(constructorArgumentItemRuleMap != null) {
			sb.append(", constructorArguments=[");
			int sbLength = sb.length();

			for(String name : constructorArgumentItemRuleMap.keySet()) {
				if(sb.length() > sbLength)
					sb.append(", ");
				
				sb.append(name);
			}

			sb.append("]");
		}
		
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
		
		sb.append("}");
		
		return sb.toString();
	}
	
	public static BeanRule[] newInstance(ClassLoader classLoader, String id, String className, String scope, boolean singleton, String factoryMethod, String initMethodName, String destroyMethodName, boolean lazyInit, boolean override) throws ClassNotFoundException, IOException {
		if(id == null)
			throw new IllegalArgumentException("The <bean> element requires a id attribute.");

		if(className == null)
			throw new IllegalArgumentException("The <bean> element requires a class attribute.");

		ScopeType scopeType = ScopeType.valueOf(scope);
		
		if(scope != null && scopeType == null)
			throw new IllegalArgumentException("No scope-type registered for scope '" + scope + "'.");
		
		if(scopeType == null)
			scopeType = singleton ? ScopeType.SINGLETON : ScopeType.PROTOTYPE;
		
		BeanRule[] beanRules = null;
		
		if(!WildcardPattern.hasWildcards(className)) {
			Class<?> beanClass = classLoader.loadClass(className);
			
			BeanRule beanRule = new BeanRule();
			beanRule.setId(id);
			beanRule.setClassName(className);
			beanRule.setBeanClass(beanClass);
			beanRule.setScopeType(scopeType);
			beanRule.setFactoryMethodName(factoryMethod);
			beanRule.setLazyInit(lazyInit);
			beanRule.setOverride(override);
			
			updateAccessibleMethod(beanRule, beanClass, initMethodName, destroyMethodName);			
			
			beanRules = new BeanRule[] { beanRule };
		} else {
			BeanClassScanner scanner = new BeanClassScanner(id, classLoader);
			Map<String, Class<?>> beanClassMap = scanner.scanClass(className);
			
			if(beanClassMap != null && beanClassMap.size() > 0) {
				beanRules = new BeanRule[beanClassMap.size()];
	
				int i = 0;
				for(Map.Entry<String, Class<?>> entry : beanClassMap.entrySet()) {
					String beanId = entry.getKey();
					Class<?> beanClass2 = entry.getValue();
					
					BeanRule beanRule = new BeanRule();
					beanRule.setId(beanId);
					beanRule.setClassName(className);
					beanRule.setBeanClass(beanClass2);
					beanRule.setScopeType(scopeType);
					beanRule.setFactoryMethodName(factoryMethod);
					beanRule.setLazyInit(lazyInit);
					beanRule.setOverride(override);
					beanRule.setStealthily(true);
					
					updateAccessibleMethod(beanRule, beanClass2, initMethodName, destroyMethodName);			

					beanRules[i++] = beanRule;
				}
			}
		}
		
		return beanRules;
	}
	
	private static void updateAccessibleMethod(BeanRule beanRule, Class<?> beanClass, String initMethodName, String destroyMethodName) {
		if(initMethodName == null && beanClass.isAssignableFrom(InitializableBean.class)) {
			initMethodName = InitializableBean.INITIALIZE_METHOD_NAME;
		}

		if(initMethodName != null) {
			if(MethodUtils.getAccessibleMethod(beanClass, initMethodName, null) == null) {
				throw new IllegalArgumentException("No such initialization method '" + initMethodName + "() on bean class: " + beanClass);
			}
		}
		
		if(destroyMethodName == null && beanClass.isAssignableFrom(DisposableBean.class)) {
			destroyMethodName = DisposableBean.DESTROY_METHOD_NAME;
		}

		if(destroyMethodName != null) {
			if(MethodUtils.getAccessibleMethod(beanClass, destroyMethodName, null) == null) {
				throw new IllegalArgumentException("No such destroy method '" + destroyMethodName + "() on bean class: " + beanClass);
			}
		}

		beanRule.setInitMethodName(initMethodName);
		beanRule.setDestroyMethodName(destroyMethodName);
	}
	
	public static ItemRuleMap addListConstructorArgument(BeanRule beanRule, List<Parameters> itemParametersList) {
//		type = new ParameterDefine("type", ParameterValueType.STRING);
//		name = new ParameterDefine("name", ParameterValueType.STRING);
//		value = new ParameterDefine("value", ParameterValueType.VARIABLE);
//		valueType = new ParameterDefine("valueType", ParameterValueType.STRING);
//		defaultValue = new ParameterDefine("defaultValue", ParameterValueType.VARIABLE);
//		tokenize = new ParameterDefine("tokenize", ParameterValueType.BOOLEAN);
//		reference = new ParameterDefine("reference", new ReferenceParameters());

		if(itemParametersList != null && itemParametersList.size() > 0) {
			ItemRuleMap itemRuleMap = new ItemRuleMap();
			
			for(Parameters parameters : itemParametersList) {
				ItemRule itemRule = ItemRule.toItemRule(parameters);
				itemRuleMap.putItemRule(itemRule);
			}
			
			beanRule.setConstructorArgumentItemRuleMap(itemRuleMap);
			
			return itemRuleMap;
		}
		
		return null;
	}
	
}
