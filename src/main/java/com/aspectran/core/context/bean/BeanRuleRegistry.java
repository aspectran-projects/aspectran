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
package com.aspectran.core.context.bean;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.aspectran.core.context.bean.ablility.DisposableBean;
import com.aspectran.core.context.bean.ablility.FactoryBean;
import com.aspectran.core.context.bean.ablility.InitializableBean;
import com.aspectran.core.context.bean.ablility.InitializableTransletBean;
import com.aspectran.core.context.bean.annotation.Configuration;
import com.aspectran.core.context.bean.scan.BeanClassScanFailedException;
import com.aspectran.core.context.bean.scan.BeanClassScanner;
import com.aspectran.core.context.builder.assistant.ContextBuilderAssistant;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.translet.TransletRuleRegistry;
import com.aspectran.core.util.PrefixSuffixPattern;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class BeanRuleRegistry.
 *
 * @since 2.0.0
 */
public class BeanRuleRegistry {
	
	private final Log log = LogFactory.getLog(BeanRuleRegistry.class);

	private final ClassLoader classLoader;
	
	private final Map<String, BeanRule> idBasedBeanRuleMap = new LinkedHashMap<>();

	private final Map<Class<?>, Set<BeanRule>> typeBasedBeanRuleMap = new LinkedHashMap<>();

	private final Map<Class<?>, BeanRule> configBeanRuleMap = new LinkedHashMap<>();

	private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();

	private final Set<BeanRule> postProcessBeanRuleMap = new HashSet<>();

	private TransletRuleRegistry transletRuleRegistry;
	
	private Set<String> importantBeanIdSet = new HashSet<>();

	private Set<Class<?>> importantBeanTypeSet = new HashSet<>();

	public BeanRuleRegistry(ClassLoader classLoader) {
		this.classLoader = classLoader;

		ignoreDependencyInterface(DisposableBean.class);
		ignoreDependencyInterface(FactoryBean.class);
		ignoreDependencyInterface(InitializableBean.class);
		ignoreDependencyInterface(InitializableTransletBean.class);
	}

	public void setTransletRuleRegistry(TransletRuleRegistry transletRuleRegistry) {
		this.transletRuleRegistry = transletRuleRegistry;
	}
	
	public BeanRule getBeanRule(Object idOrRequiredType) {
		if(idOrRequiredType == null) {
			throw new IllegalArgumentException("'idOrRequiredType' must not be null.");
		}
		if(idOrRequiredType instanceof Class<?>) {
			BeanRule[] beanRules = getBeanRules((Class<?>)idOrRequiredType);
			if(beanRules == null) {
				return null;
			}
			if(beanRules.length > 1) {
				throw new NoUniqueBeanException((Class<?>)idOrRequiredType, beanRules);
			}
			return beanRules[0];
		} else {
			return getBeanRule(idOrRequiredType.toString());
		}
	}
	
	public BeanRule getBeanRule(String id) {
		return idBasedBeanRuleMap.get(id);
	}
	
	public BeanRule[] getBeanRules(Class<?> requiredType) {
		Set<BeanRule> list = typeBasedBeanRuleMap.get(requiredType);
		if(list == null || list.isEmpty()) {
			return null;
		}
		return list.toArray(new BeanRule[list.size()]);
	}

	public BeanRule getConfigBeanRule(Class<?> requiredType) {
		return configBeanRuleMap.get(requiredType);
	}

	public boolean containsBeanRule(Object idOrRequiredType) {
		if(idOrRequiredType == null) {
			throw new IllegalArgumentException("'idOrRequiredType' must not be null.");
		}
		if(idOrRequiredType instanceof Class<?>) {
			return containsBeanRule((Class<?>)idOrRequiredType);
		} else {
			return containsBeanRule(idOrRequiredType.toString());
		}
	}

	public boolean containsBeanRule(String id) {
		return idBasedBeanRuleMap.containsKey(id);
	}
	
	public boolean containsBeanRule(Class<?> requiredType) {
		return typeBasedBeanRuleMap.containsKey(requiredType);
	}
	
	public Map<String, BeanRule> getIdBasedBeanRuleMap() {
		return idBasedBeanRuleMap;
	}

	public Map<Class<?>, Set<BeanRule>> getTypeBasedBeanRuleMap() {
		return typeBasedBeanRuleMap;
	}

	public Map<Class<?>, BeanRule> getConfigBeanRuleMap() {
		return configBeanRuleMap;
	}

	/**
	 * Adds bean rule.
	 *
	 * @param beanRule the bean rule
	 * @throws ClassNotFoundException thrown when the bean class is not found.
	 */
	public void addBeanRule(final BeanRule beanRule) throws ClassNotFoundException {
		final PrefixSuffixPattern prefixSuffixPattern = PrefixSuffixPattern.parse(beanRule.getId());
		String scanPattern = beanRule.getScanPattern();

		if(scanPattern != null) {
			BeanClassScanner scanner = new BeanClassScanner(classLoader);
			if(beanRule.getFilterParameters() != null)
				scanner.setFilterParameters(beanRule.getFilterParameters());
			if(beanRule.getMaskPattern() != null)
				scanner.setBeanIdMaskPattern(beanRule.getMaskPattern());

			try {
				scanner.scan(scanPattern, (resourceName, scannedClass) -> {
					BeanRule beanRule2 = beanRule.replicate();
					if(prefixSuffixPattern != null) {
						beanRule2.setId(prefixSuffixPattern.join(resourceName));
					} else {
						if(beanRule.getId() != null) {
							beanRule2.setId(beanRule.getId() + resourceName);
						}
					}
					beanRule2.setBeanClass(scannedClass);
					dissectBeanRule(beanRule2);
				});
			} catch(IOException e) {
				throw new BeanClassScanFailedException("Failed to scan bean class. scanPattern: " + scanPattern, e);
			}
		} else {
			if(!beanRule.isOffered()) {
				String className = beanRule.getClassName();
				if(prefixSuffixPattern != null) {
					beanRule.setId(prefixSuffixPattern.join(className));
				}
				Class<?> beanClass = classLoader.loadClass(className);
				beanRule.setBeanClass(beanClass);
			}
			dissectBeanRule(beanRule);
		}
	}

	private void dissectBeanRule(BeanRule beanRule) {
		Class<?> targetBeanClass = BeanRuleAnalyzer.determineBeanClass(beanRule);

		if(targetBeanClass == null) {
			postProcessBeanRuleMap.add(beanRule);
		} else {
			if(beanRule.getId() != null)
				saveBeanRule(beanRule.getId(), beanRule);

			if(!beanRule.isOffered()) {
				if(targetBeanClass.isAnnotationPresent(Configuration.class)) {
					// bean rule for configuration
					saveConfigBeanRule(beanRule);
				} else {
					saveBeanRule(targetBeanClass, beanRule);
					for(Class<?> ifc : targetBeanClass.getInterfaces()) {
						if(!ignoredDependencyInterfaces.contains(ifc)) {
							saveBeanRule(ifc, beanRule);
						}
					}
				}
			}

			if(log.isTraceEnabled()) {
				log.trace("add BeanRule " + beanRule);
			}
		}
	}
	
	private void saveBeanRule(String beanId, BeanRule beanRule) {
		if(importantBeanIdSet.contains(beanId)) {
			throw new BeanRuleException("Already exists the id based named bean", beanRule);
		}
		if(beanRule.isImportant()) {
			importantBeanIdSet.add(beanRule.getId());
		}
		idBasedBeanRuleMap.put(beanId, beanRule);
	}
	
	private void saveBeanRule(Class<?> beanClass, BeanRule beanRule) {
		if(importantBeanTypeSet.contains(beanClass)) {
			throw new BeanRuleException("Already exists the type based named bean", beanRule);
		}
		if(beanRule.isImportant()) {
			importantBeanTypeSet.add(beanClass);
		}
		Set<BeanRule> list = typeBasedBeanRuleMap.get(beanClass);
		if(list == null) {
			list = new HashSet<BeanRule>();
			typeBasedBeanRuleMap.put(beanClass, list);
		}
		list.add(beanRule);
	}

	private void saveConfigBeanRule(BeanRule beanRule) {
		configBeanRuleMap.put(beanRule.getBeanClass(), beanRule);
	}

	public void postProcess(ContextBuilderAssistant assistant) {
		if(!postProcessBeanRuleMap.isEmpty()) {
			for(BeanRule beanRule : postProcessBeanRuleMap) {
				if(beanRule.getId() != null)
					saveBeanRule(beanRule.getId(), beanRule);

				if(beanRule.isOffered()) {
					Class<?> offerBeanClass = resolveOfferBeanClass(beanRule);
					Class<?> targetBeanClass = BeanRuleAnalyzer.determineOfferMethodTargetBeanClass(offerBeanClass, beanRule);

					if(beanRule.getInitMethodName() != null) {
						BeanRuleAnalyzer.checkInitMethod(targetBeanClass, beanRule);
					}

					if(beanRule.getDestroyMethodName() != null) {
						BeanRuleAnalyzer.checkDestroyMethod(targetBeanClass, beanRule);
					}

					if(beanRule.getFactoryMethodName() != null) {
						targetBeanClass = BeanRuleAnalyzer.determineFactoryMethodTargetBeanClass(targetBeanClass, beanRule);
					}

					saveBeanRule(targetBeanClass, beanRule);

					for(Class<?> ifc : targetBeanClass.getInterfaces()) {
						if(!ignoredDependencyInterfaces.contains(ifc)) {
							saveBeanRule(ifc, beanRule);
						}
					}
				}
			}

			postProcessBeanRuleMap.clear();
		}

		importantBeanIdSet.clear();
		importantBeanTypeSet.clear();

		parseAnnotatedConfig(assistant);
	}

	private void parseAnnotatedConfig(ContextBuilderAssistant assistant) {
		AnnotatedConfigRelater relater = new AnnotatedConfigRelater() {
			@Override
			public void relay(Class<?> targetBeanClass, BeanRule beanRule) {
				if(beanRule.getId() != null) {
					saveBeanRule(beanRule.getId(), beanRule);
				}
				saveBeanRule(targetBeanClass, beanRule);
				for(Class<?> ifc : targetBeanClass.getInterfaces()) {
					if(!ignoredDependencyInterfaces.contains(ifc)) {
						saveBeanRule(ifc, beanRule);
					}
				}
			}

			@Override
			public void relay(TransletRule transletRule) {
				if(transletRuleRegistry != null) {
					transletRuleRegistry.addTransletRule(transletRule);
				}
			}
		};

		AnnotatedConfigParser parser = new AnnotatedConfigParser(assistant, relater);
		parser.parse();
	}

	private Class<?> resolveOfferBeanClass(BeanRule beanRule) {
		BeanRule offerBeanRule;

		if(beanRule.getOfferBeanClass() == null) {
			offerBeanRule = getBeanRule(beanRule.getOfferBeanId());

			if(offerBeanRule == null)
				throw new BeanNotFoundException(beanRule.getOfferBeanId());
		} else {
			BeanRule[] beanRules = getBeanRules(beanRule.getOfferBeanClass());

			if(beanRules == null || beanRules.length == 0)
				throw new RequiredTypeBeanNotFoundException(beanRule.getOfferBeanClass());

			if(beanRules.length > 1)
				throw new NoUniqueBeanException(beanRule.getOfferBeanClass(), beanRules);

			offerBeanRule = beanRules[0];
		}

		if(offerBeanRule.isOffered())
			throw new BeanRuleException("Invalid BeanRule: An Offer Bean can not call another Offer Bean. caller:", beanRule);

		return offerBeanRule.getTargetBeanClass();
	}

	public void ignoreDependencyInterface(Class<?> ifc) {
		this.ignoredDependencyInterfaces.add(ifc);
	}

	public void clear() {
		idBasedBeanRuleMap.clear();
		typeBasedBeanRuleMap.clear();
		configBeanRuleMap.clear();
	}

}
