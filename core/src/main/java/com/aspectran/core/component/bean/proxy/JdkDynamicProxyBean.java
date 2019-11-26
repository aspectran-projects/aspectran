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
package com.aspectran.core.component.bean.proxy;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.component.aspect.AspectAdviceRuleRegistry;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.context.rule.ExceptionRule;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Create an instance of the dynamic proxy bean using JDK.
 */
public class JdkDynamicProxyBean extends AbstractDynamicProxyBean implements InvocationHandler {

    private static final Log log = LogFactory.getLog(JdkDynamicProxyBean.class);

    private final ActivityContext context;

    private final BeanRule beanRule;

    private final Object bean;

    private JdkDynamicProxyBean(ActivityContext context, BeanRule beanRule, Object bean) {
        super(context.getAspectRuleRegistry());

        this.context = context;
        this.beanRule = beanRule;
        this.bean = bean;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isAvoidAdvice(method)) {
            return method.invoke(bean, args);
        }

        Activity activity = context.getCurrentActivity();
        String transletName = (activity.getTranslet() != null ? activity.getTranslet().getRequestName() : null);
        String beanId = beanRule.getId();
        String className = beanRule.getClassName();
        String methodName = method.getName();

        AspectAdviceRuleRegistry aarr = getAspectAdviceRuleRegistry(activity, transletName, beanId, className, methodName);
        if (aarr == null) {
            return method.invoke(bean, args);
        }

        try {
            try {
                if (aarr.getBeforeAdviceRuleList() != null) {
                    for (AspectAdviceRule aspectAdviceRule : aarr.getBeforeAdviceRuleList()) {
                        if (!isSameBean(beanRule, aspectAdviceRule)) {
                            activity.executeAdvice(aspectAdviceRule, true);
                        }
                    }
                }

                if (log.isDebugEnabled()) {
                    log.debug("Invoke a proxy method " + methodName + "() on the bean " + beanRule);
                }

                Object result = method.invoke(bean, args);

                if (aarr.getAfterAdviceRuleList() != null) {
                    for (AspectAdviceRule aspectAdviceRule : aarr.getAfterAdviceRuleList()) {
                        if (!isSameBean(beanRule, aspectAdviceRule)) {
                            activity.executeAdvice(aspectAdviceRule, true);
                        }
                    }
                }

                return result;
            } finally {
                if (aarr.getFinallyAdviceRuleList() != null) {
                    for (AspectAdviceRule aspectAdviceRule : aarr.getFinallyAdviceRuleList()) {
                        if (!isSameBean(beanRule, aspectAdviceRule)) {
                            activity.executeAdvice(aspectAdviceRule, false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            activity.setRaisedException(e);

            List<ExceptionRule> exceptionRuleList = aarr.getExceptionRuleList();
            if (exceptionRuleList != null) {
                activity.handleException(exceptionRuleList);
                if (activity.isResponseReserved()) {
                    return null;
                }
            }

            throw e;
        }
    }

    public static Object newInstance(ActivityContext context, BeanRule beanRule, Object bean) {
        JdkDynamicProxyBean proxy = new JdkDynamicProxyBean(context, beanRule, bean);
        return Proxy.newProxyInstance(beanRule.getBeanClass().getClassLoader(), beanRule.getBeanClass().getInterfaces(), proxy);
    }

}