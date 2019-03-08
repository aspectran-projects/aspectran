/*
 * Copyright (c) 2008-2019 The Aspectran Project
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
package com.aspectran.core.component.bean;

import com.aspectran.core.component.bean.ablility.DisposableBean;
import com.aspectran.core.component.bean.ablility.FactoryBean;
import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.core.component.bean.ablility.InitializableTransletBean;
import com.aspectran.core.component.bean.annotation.Component;
import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.component.bean.aware.ApplicationAdapterAware;
import com.aspectran.core.component.bean.aware.ClassLoaderAware;
import com.aspectran.core.component.bean.aware.CurrentActivityAware;
import com.aspectran.core.component.bean.aware.EnvironmentAware;
import com.aspectran.core.component.bean.scan.BeanClassScanFailedException;
import com.aspectran.core.component.bean.scan.BeanClassScanner;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.AutowireRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.IllegalRuleException;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.assistant.ContextRuleAssistant;
import com.aspectran.core.context.rule.type.ScopeType;
import com.aspectran.core.util.PrefixSuffixPattern;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Class BeanRuleRegistry.
 *
 * @since 2.0.0
 */
public class BeanRuleRegistry {

    private static final Log log = LogFactory.getLog(BeanRuleRegistry.class);

    private final Map<String, BeanRule> idBasedBeanRuleMap = new LinkedHashMap<>(256);

    private final Map<Class<?>, Set<BeanRule>> typeBasedBeanRuleMap = new LinkedHashMap<>(256);

    private final Map<Class<?>, BeanRule> configurableBeanRuleMap = new LinkedHashMap<>(256);

    private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();

    private final Set<BeanRule> postProcessBeanRuleMap = new HashSet<>();

    private final Set<String> importantBeanIdSet = new HashSet<>();

    private final Set<Class<?>> importantBeanTypeSet = new HashSet<>();

    private final ClassLoader classLoader;

    public BeanRuleRegistry(ClassLoader classLoader) {
        this.classLoader = classLoader;

        ignoreDependencyInterface(DisposableBean.class);
        ignoreDependencyInterface(FactoryBean.class);
        ignoreDependencyInterface(InitializableBean.class);
        ignoreDependencyInterface(InitializableTransletBean.class);
        ignoreDependencyInterface(ActivityContextAware.class);
        ignoreDependencyInterface(ApplicationAdapterAware.class);
        ignoreDependencyInterface(ClassLoaderAware.class);
        ignoreDependencyInterface(CurrentActivityAware.class);
        ignoreDependencyInterface(EnvironmentAware.class);
        ignoreDependencyInterface(java.io.Serializable.class);
        ignoreDependencyInterface(java.lang.Comparable.class);
        ignoreDependencyInterface(java.lang.CharSequence.class);
    }

    public BeanRule getBeanRule(Object idOrRequiredType) {
        if (idOrRequiredType == null) {
            throw new IllegalArgumentException("idOrRequiredType must not be null");
        }
        if (idOrRequiredType instanceof Class<?>) {
            BeanRule[] beanRules = getBeanRules((Class<?>)idOrRequiredType);
            if (beanRules == null) {
                return null;
            }
            if (beanRules.length > 1) {
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
        if (list != null && !list.isEmpty()) {
            return list.toArray(new BeanRule[0]);
        } else {
            return null;
        }
    }

    public BeanRule getBeanRuleForConfig(Class<?> requiredType) {
        return configurableBeanRuleMap.get(requiredType);
    }

    public boolean containsBeanRule(Object idOrRequiredType) {
        if (idOrRequiredType == null) {
            throw new IllegalArgumentException("idOrRequiredType must not be null");
        }
        if (idOrRequiredType instanceof Class<?>) {
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

    public Map<Class<?>, BeanRule> getConfigurableBeanRuleMap() {
        return configurableBeanRuleMap;
    }

    public Collection<BeanRule> getIdBasedBeanRules() {
        return idBasedBeanRuleMap.values();
    }

    public Collection<Set<BeanRule>> getTypeBasedBeanRules() {
        return typeBasedBeanRuleMap.values();
    }

    public Collection<BeanRule> getConfigurableBeanRules() {
        return configurableBeanRuleMap.values();
    }

    /**
     * Scans for annotated components.
     *
     * @param basePackages the base packages to scan for annotated components
     * @throws IOException if an error occurs while scanning the bean class
     */
    public void scanConfigurableBeans(String... basePackages) throws IOException {
        if (basePackages == null || basePackages.length == 0) {
            return;
        }

        log.info("Auto component scanning on packages [" + StringUtils.joinCommaDelimitedList(basePackages) + "]");

        for (String basePackage : basePackages) {
            BeanClassScanner scanner = new BeanClassScanner(classLoader);
            scanner.scan(basePackage + ".**", (resourceName, targetClass) -> {
                if (targetClass.isAnnotationPresent(Component.class)) {
                    BeanRule beanRule = new BeanRule();
                    beanRule.setBeanClass(targetClass);
                    beanRule.setScopeType(ScopeType.SINGLETON);
                    saveConfigurableBeanRule(beanRule);
                }
            });
        }
    }

    /**
     * Adds a bean rule.
     *
     * @param beanRule the bean rule to add
     * @throws IllegalRuleException if an error occurs while adding a bean rule
     */
    public void addBeanRule(final BeanRule beanRule) throws IllegalRuleException {
        try {
            final PrefixSuffixPattern prefixSuffixPattern = PrefixSuffixPattern.parse(beanRule.getId());
            String scanPattern = beanRule.getScanPattern();
            if (scanPattern != null) {
                BeanClassScanner scanner = new BeanClassScanner(classLoader);
                if (beanRule.getFilterParameters() != null) {
                    scanner.setFilterParameters(beanRule.getFilterParameters());
                }
                if (beanRule.getMaskPattern() != null) {
                    scanner.setBeanIdMaskPattern(beanRule.getMaskPattern());
                }
                try {
                    scanner.scan(scanPattern, (resourceName, targetClass) -> {
                        BeanRule beanRule2 = beanRule.replicate();
                        if (prefixSuffixPattern != null) {
                            beanRule2.setId(prefixSuffixPattern.join(resourceName));
                        } else {
                            if (beanRule.getId() != null) {
                                beanRule2.setId(beanRule.getId() + resourceName);
                            } else if (beanRule.getMaskPattern() != null) {
                                beanRule2.setId(resourceName);
                            }
                        }
                        beanRule2.setBeanClass(targetClass);
                        dissectBeanRule(beanRule2);
                    });
                } catch (IOException e) {
                    throw new BeanClassScanFailedException("Failed to scan bean classes with given pattern: " +
                            scanPattern, e);
                }
            } else {
                if (!beanRule.isFactoryOffered()) {
                    String className = beanRule.getClassName();
                    if (prefixSuffixPattern != null) {
                        beanRule.setId(prefixSuffixPattern.join(className));
                    }
                    Class<?> beanClass = classLoader.loadClass(className);
                    beanRule.setBeanClass(beanClass);
                }
                dissectBeanRule(beanRule);
            }
        } catch(Exception e) {
            throw new IllegalRuleException("Could not add bean rule " + beanRule, e);
        }
    }

    private void dissectBeanRule(BeanRule beanRule) {
        Class<?> targetBeanClass = BeanRuleAnalyzer.determineBeanClass(beanRule);
        if (targetBeanClass == null) {
            postProcessBeanRuleMap.add(beanRule);
        } else {
            if (beanRule.getId() != null) {
                saveBeanRule(beanRule.getId(), beanRule);
            }
            if (!beanRule.isFactoryOffered()) {
                if (targetBeanClass.isAnnotationPresent(Component.class)) {
                    saveConfigurableBeanRule(beanRule);
                } else {
                    saveBeanRule(targetBeanClass, beanRule);
                    saveBeanRule(targetBeanClass.getInterfaces(), beanRule);
                }
            }
            if (log.isTraceEnabled()) {
                log.trace("add BeanRule " + beanRule);
            }
        }
    }

    private void saveBeanRule(String beanId, BeanRule beanRule) {
        if (importantBeanIdSet.contains(beanId)) {
            throw new BeanRuleException("Already exists the ID-based named bean", beanRule);
        }
        if (beanRule.isImportant()) {
            importantBeanIdSet.add(beanRule.getId());
        }
        idBasedBeanRuleMap.put(beanId, beanRule);
    }

    private void saveBeanRule(Class<?> beanClass, BeanRule beanRule) {
        if (importantBeanTypeSet.contains(beanClass)) {
            throw new BeanRuleException("Already exists the type-based named bean", beanRule);
        }
        if (beanRule.isImportant()) {
            importantBeanTypeSet.add(beanClass);
        }
        Set<BeanRule> set = typeBasedBeanRuleMap.computeIfAbsent(beanClass, k -> new HashSet<>());
        set.add(beanRule);
    }

    private void saveBeanRule(Class<?>[] interfaces, BeanRule beanRule) {
        for (Class<?> ifc : interfaces) {
            if (!ignoredDependencyInterfaces.contains(ifc)) {
                saveBeanRule(ifc, beanRule);
            }
        }
    }

    private void saveConfigurableBeanRule(BeanRule beanRule) {
        if (beanRule.getBeanClass() == null) {
            throw new BeanRuleException("No specified bean class", beanRule);
        }
        configurableBeanRuleMap.put(beanRule.getBeanClass(), beanRule);
    }

    public void postProcess(ContextRuleAssistant assistant) throws IllegalRuleException {
        if (!postProcessBeanRuleMap.isEmpty()) {
            for (BeanRule beanRule : postProcessBeanRuleMap) {
                if (beanRule.getId() != null) {
                    saveBeanRule(beanRule.getId(), beanRule);
                }
                if (beanRule.isFactoryOffered()) {
                    Class<?> offeredFactoryBeanClass = resolveOfferedFactoryBeanClass(beanRule);
                    Class<?> targetBeanClass = BeanRuleAnalyzer.determineFactoryMethodTargetBeanClass(
                            offeredFactoryBeanClass, beanRule);
                    if (beanRule.getInitMethodName() != null) {
                        BeanRuleAnalyzer.checkInitMethod(targetBeanClass, beanRule);
                    }
                    if (beanRule.getDestroyMethodName() != null) {
                        BeanRuleAnalyzer.checkDestroyMethod(targetBeanClass, beanRule);
                    }
                    saveBeanRule(targetBeanClass, beanRule);
                    saveBeanRule(targetBeanClass.getInterfaces(), beanRule);
                }
            }
            postProcessBeanRuleMap.clear();
        }
        importantBeanIdSet.clear();
        importantBeanTypeSet.clear();
        parseAnnotatedConfig(assistant);
    }

    private void parseAnnotatedConfig(ContextRuleAssistant assistant) throws IllegalRuleException {
        AnnotatedConfigRelater relater = new AnnotatedConfigRelater() {
            @Override
            public void relay(Class<?> targetBeanClass, BeanRule beanRule) {
                if (beanRule.getId() != null) {
                    saveBeanRule(beanRule.getId(), beanRule);
                }
                saveBeanRule(targetBeanClass, beanRule);
                saveBeanRule(targetBeanClass.getInterfaces(), beanRule);
            }

            @Override
            public void relay(AspectRule aspectRule) throws IllegalRuleException {
                assistant.addAspectRule(aspectRule);
            }

            @Override
            public void relay(TransletRule transletRule) {
                assistant.addTransletRule(transletRule);
            }

            @Override
            public void relay(AutowireRule autowireRule) {
                assistant.resolveBeanClass(autowireRule);
            }
        };

        AnnotatedConfigParser parser = new AnnotatedConfigParser(assistant, relater);
        parser.parse();
    }

    private Class<?> resolveOfferedFactoryBeanClass(BeanRule beanRule) {
        BeanRule offeredFactoryBeanRule;
        if (beanRule.getFactoryBeanClass() == null) {
            offeredFactoryBeanRule = getBeanRule(beanRule.getFactoryBeanId());
            if (offeredFactoryBeanRule == null) {
                throw new BeanNotFoundException(beanRule.getFactoryBeanId());
            }
        } else {
            BeanRule[] beanRules = getBeanRules(beanRule.getFactoryBeanClass());
            if (beanRules == null || beanRules.length == 0) {
                throw new RequiredTypeBeanNotFoundException(beanRule.getFactoryBeanClass());
            }
            if (beanRules.length > 1) {
                throw new NoUniqueBeanException(beanRule.getFactoryBeanClass(), beanRules);
            }
            offeredFactoryBeanRule = beanRules[0];
        }
        if (offeredFactoryBeanRule.isFactoryOffered()) {
            throw new BeanRuleException("Invalid BeanRule: An offered factory bean can not call " +
                    "another offered factory bean; Caller:", beanRule);
        }
        return offeredFactoryBeanRule.getTargetBeanClass();
    }

    public void ignoreDependencyInterface(Class<?> ifc) {
        this.ignoredDependencyInterfaces.add(ifc);
    }

}
