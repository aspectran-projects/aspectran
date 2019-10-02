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

import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.RequestAdapter;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.component.bean.ablility.DisposableBean;
import com.aspectran.core.component.bean.scope.RequestScope;
import com.aspectran.core.component.bean.scope.Scope;
import com.aspectran.core.component.bean.scope.SessionScope;
import com.aspectran.core.component.bean.scope.SingletonScope;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.type.BeanProxifierType;
import com.aspectran.core.context.rule.type.ScopeType;
import com.aspectran.core.util.MethodUtils;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * The Class AbstractBeanRegistry.
 * 
 * <p>Created: 2009. 03. 09 PM 23:48:09</p>
 */
abstract class AbstractBeanRegistry extends AbstractBeanFactory implements BeanRegistry {

    private static final Log log = LogFactory.getLog(AbstractBeanRegistry.class);

    private final SingletonScope singletonScope = new SingletonScope();

    private final BeanRuleRegistry beanRuleRegistry;

    AbstractBeanRegistry(ActivityContext context, BeanRuleRegistry beanRuleRegistry,
                         BeanProxifierType beanProxifierType) {
        super(context, beanProxifierType);
        this.beanRuleRegistry = beanRuleRegistry;
    }

    protected BeanRuleRegistry getBeanRuleRegistry() {
        return beanRuleRegistry;
    }

    @SuppressWarnings("unchecked")
    protected <T> T getBean(BeanRule beanRule) {
        if (beanRule.getScopeType() == ScopeType.SINGLETON) {
            return (T)getSingletonScopeBean(beanRule);
        } else if (beanRule.getScopeType() == ScopeType.PROTOTYPE) {
            // Does not manage the complete lifecycle of a prototype bean.
            // In particular, Aspectran does not manage destruction phase of prototype-scoped beans.
            return (T)getPrototypeScopeBean(beanRule);
        } else if (beanRule.getScopeType() == ScopeType.REQUEST) {
            return (T)getRequestScopeBean(beanRule);
        } else if (beanRule.getScopeType() == ScopeType.SESSION) {
            return (T)getSessionScopeBean(beanRule);
        }
        throw new BeanCreationException(beanRule);
    }

    private Object getSingletonScopeBean(BeanRule beanRule) {
        if (beanRule == null) {
            throw new IllegalArgumentException("beanRule must not be null");
        }
        return getScopedBean(singletonScope, beanRule);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getPrototypeScopeBean(BeanRule beanRule) {
        if (beanRule == null) {
            throw new IllegalArgumentException("beanRule must not be null");
        }
        Object bean = createBean(beanRule);
        if (bean != null && beanRule.isFactoryProductionRequired()) {
            bean = getFactoryProducedObject(beanRule, bean);
        }
        return (T)bean;
    }

    private Object getRequestScopeBean(BeanRule beanRule) {
        if (beanRule == null) {
            throw new IllegalArgumentException("beanRule must not be null");
        }
        Scope scope = getRequestScope();
        if (scope == null) {
            throw new UnsupportedBeanScopeException(ScopeType.REQUEST, beanRule);
        }
        return getScopedBean(scope, beanRule);
    }

    private Object getSessionScopeBean(BeanRule beanRule) {
        if (beanRule == null) {
            throw new IllegalArgumentException("beanRule must not be null");
        }
        Scope scope = getSessionScope();
        if (scope == null) {
            throw new UnsupportedBeanScopeException(ScopeType.SESSION, beanRule);
        }
        return getScopedBean(scope, beanRule);
    }

    private Object getScopedBean(Scope scope, BeanRule beanRule) {
        ReadWriteLock scopeLock = scope.getScopeLock();
        boolean readLocked = true;
        scopeLock.readLock().lock();
        Object bean;
        try {
            BeanInstance instance = scope.getBeanInstance(beanRule);
            if (instance == null) {
                readLocked = false;
                scopeLock.readLock().unlock();
                scopeLock.writeLock().lock();
                try {
                    instance = scope.getBeanInstance(beanRule);
                    if (instance == null) {
                        bean = createBean(beanRule, scope);
                    } else {
                        bean = instance.getBean();
                    }
                    if (bean != null && beanRule.isFactoryProductionRequired()) {
                        bean = getFactoryProducedObject(beanRule, bean);
                    }
                } finally {
                    scopeLock.writeLock().unlock();
                }
            } else {
                bean = instance.getBean();
                if (bean != null && beanRule.isFactoryProductionRequired()) {
                    readLocked = false;
                    scopeLock.readLock().unlock();
                    scopeLock.writeLock().lock();
                    try {
                        bean = getFactoryProducedObject(beanRule, bean);
                    } finally {
                        scopeLock.writeLock().unlock();
                    }
                }
            }
        } finally {
            if (readLocked) {
                scopeLock.readLock().unlock();
            }
        }
        return bean;
    }

    private RequestScope getRequestScope() {
        Activity activity = getActivityContext().getCurrentActivity();
        if (activity != null) {
            RequestAdapter requestAdapter = activity.getRequestAdapter();
            if (requestAdapter != null) {
                return requestAdapter.getRequestScope(true);
            }
        }
        return null;
    }

    private SessionScope getSessionScope() {
        Activity activity = getActivityContext().getCurrentActivity();
        if (activity != null) {
            SessionAdapter sessionAdapter = activity.getSessionAdapter();
            if (sessionAdapter != null) {
                return sessionAdapter.getSessionScope();
            }
        }
        return null;
    }

    /**
     * Instantiate all singletons(non-lazy-init).
     */
    private void instantiateSingletons() {
        if (log.isDebugEnabled()) {
            log.debug("Initializing singletons in " + this);
        }

        Activity activity = getActivityContext().getDefaultActivity();
        for (BeanRule beanRule : beanRuleRegistry.getIdBasedBeanRules()) {
            instantiateSingleton(beanRule, activity);
        }
        for (Set<BeanRule> beanRuleSet : beanRuleRegistry.getTypeBasedBeanRules()) {
            for (BeanRule beanRule : beanRuleSet) {
                instantiateSingleton(beanRule, activity);
            }
        }
        for (BeanRule beanRule : beanRuleRegistry.getConfigurableBeanRules()) {
            instantiateSingleton(beanRule, activity);
        }
    }

    private void instantiateSingleton(BeanRule beanRule, Activity activity) {
        if (beanRule.isSingleton() && !beanRule.isLazyInit()
                && !singletonScope.containsBeanRule(beanRule)) {
            createBean(beanRule, singletonScope, activity);
        }
    }

    /**
     * Destroy all cached singletons.
     */
    private void destroySingletons() {
        if (log.isDebugEnabled()) {
            log.debug("Destroying singletons in " + this);
        }

        singletonScope.destroy();
    }

    @Override
    public void destroySingleton(Object bean) throws Exception {
        ReadWriteLock scopeLock = singletonScope.getScopeLock();
        boolean readLocked = true;
        scopeLock.readLock().lock();
        try {
            BeanRule beanRule = singletonScope.getBeanRule(bean);
            if (beanRule != null) {
                readLocked = false;
                scopeLock.readLock().unlock();
                scopeLock.writeLock().lock();
                try {
                    singletonScope.destroy(bean);
                } finally {
                    scopeLock.writeLock().unlock();
                }
            }
        } finally {
            if (readLocked) {
                scopeLock.readLock().unlock();
            }
        }
    }

    @Override
    protected void doInitialize() throws Exception {
        instantiateSingletons();
    }

    @Override
    protected void doDestroy() throws Exception {
        destroySingletons();
    }

}
