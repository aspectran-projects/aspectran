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
package com.aspectran.core.context.bean;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aspectran.core.context.bean.scan.BeanClassScanner;
import com.aspectran.core.context.loader.AspectranClassLoader;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.BeanRuleMap;
import com.aspectran.core.util.PrefixSuffixPattern;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.core.util.wildcard.WildcardPattern;

/**
 * The type Bean rule registry.
 *
 * @since 2.0.0
 */
public class BeanRuleRegistry {
	
	private final Log log = LogFactory.getLog(BeanRuleRegistry.class);

	private final ClassLoader classLoader;
	
	private final BeanRuleMap beanRuleMap = new BeanRuleMap();

	private final Map<Class<?>, BeanRule> classBeanRuleMap = new HashMap<Class<?>, BeanRule>();

	public BeanRuleRegistry() {
		this.classLoader = AspectranClassLoader.getDefaultClassLoader();
	}
	
	public BeanRuleRegistry(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public BeanRule getBeanRule(String id) {
		return beanRuleMap.get(id);
	}
	
	public BeanRule getBeanRule(Class<?> classType) {
		return classBeanRuleMap.get(classType);
	}
	
	public boolean contains(String id) {
		return beanRuleMap.containsKey(id);
	}
	
	public boolean contains(Class<?> classType) {
		return classBeanRuleMap.containsKey(classType);
	}

	public Collection<BeanRule> getBeanRules() {
		return beanRuleMap.values();
	}

	/**
	 * Adds the bean rule.
	 *
	 * @param beanRule the bean rule
	 * @throws CloneNotSupportedException the clone not supported exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void addBeanRule(BeanRule beanRule) throws CloneNotSupportedException, ClassNotFoundException, IOException {
		String className = beanRule.getClassName();

		PrefixSuffixPattern prefixSuffixPattern = new PrefixSuffixPattern();
		boolean patterned = prefixSuffixPattern.split(beanRule.getId());
		
		if(WildcardPattern.hasWildcards(className)) {
			BeanClassScanner scanner = new BeanClassScanner(classLoader);
			if(beanRule.getFilterParameters() != null)
				scanner.setFilterParameters(beanRule.getFilterParameters());
			if(beanRule.getMaskPattern() != null)
				scanner.setBeanIdMaskPattern(beanRule.getMaskPattern());
			
			Map<String, Class<?>> beanClassMap = scanner.scanClasses(className);
			
			if(beanClassMap != null && !beanClassMap.isEmpty()) {
				for(Map.Entry<String, Class<?>> entry : beanClassMap.entrySet()) {
					BeanRule beanRule2 = beanRule.clone();
					
					String beanId = entry.getKey();
					Class<?> beanClass = entry.getValue();
			
					if(patterned) {
						beanRule2.setId(prefixSuffixPattern.join(beanId));
					} else {
						if(beanRule.getId() != null) {
							beanRule2.setId(beanRule.getId() + beanId);
						}
					}

					beanRule2.setClassName(beanClass.getName());
					beanRule2.setBeanClass(beanClass);
					beanRule2.setScanned(true);
					BeanRule.checkFactoryBeanImplement(beanRule2);
					BeanRule.checkAccessibleMethod(beanRule2);
					beanRuleMap.putBeanRule(beanRule2);
					
					if(beanRule2.getId().equals(beanRule2.getBeanClass().getName())) {
						classBeanRuleMap.put(beanRule2.getBeanClass(), beanRule2);
					}
					
					if(log.isTraceEnabled())
						log.trace("add BeanRule " + beanRule2);
				}
			}
			
			if(log.isDebugEnabled())
				log.debug("scanned class files: " + (beanClassMap == null ? 0 : beanClassMap.size()));
		} else {
			if(patterned) {
				beanRule.setId(prefixSuffixPattern.join(className));
			}
			
			Class<?> beanClass = classLoader.loadClass(className);
			beanRule.setBeanClass(beanClass);
			BeanRule.checkFactoryBeanImplement(beanRule);
			BeanRule.checkAccessibleMethod(beanRule);
			beanRuleMap.putBeanRule(beanRule);

			if(beanRule.getId().equals(beanRule.getBeanClass().getName())) {
				classBeanRuleMap.put(beanRule.getBeanClass(), beanRule);
			}
			
			if(log.isTraceEnabled())
				log.trace("add BeanRule " + beanRule);
		}
	}

	public BeanRuleMap getBeanRuleMap() {
		return beanRuleMap;
	}

	public void clear() {
		beanRuleMap.clear();
		classBeanRuleMap.clear();
	}
	
}
