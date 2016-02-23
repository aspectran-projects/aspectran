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

import java.lang.reflect.Method;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.context.bean.ablility.FactoryBean;
import com.aspectran.core.context.rule.BeanActionRule;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.util.MethodUtils;

public class BeanRuleAnalyzer {

	public static final Class<?>[] TRANSLET_ACTION_PARAMETER_TYPES = { Translet.class };
	
	public static Class<?> determineBeanClass(BeanRule beanRule) {
		Class<?> targetBeanClass;

		if(beanRule.isOffered()) {
			targetBeanClass = beanRule.getOfferBeanClass();
			if(targetBeanClass == null) {
				// (will be post processing)
				return null;
			}
			targetBeanClass = determineOfferMethodTargetBeanClass(targetBeanClass, beanRule);
		} else {
			targetBeanClass = beanRule.getBeanClass();
		}

		if(targetBeanClass == null)
			throw new BeanRuleException("Invalid BeanRule", beanRule);

		if(beanRule.getInitMethodName() != null) {
			checkInitMethod(targetBeanClass, beanRule);
		}

		if(beanRule.getDestroyMethodName() != null) {
			checkDestroyMethod(targetBeanClass, beanRule);
		}

		if(beanRule.isFactoryBean()) {
			targetBeanClass = determineTargetBeanClassForFactoryBean(targetBeanClass, beanRule);
		} else if(beanRule.getFactoryMethodName() != null) {
			targetBeanClass = determineFactoryMethodTargetBeanClass(targetBeanClass, beanRule);
		}

		return targetBeanClass;
	}

	public static Class<?> determineOfferMethodTargetBeanClass(Class<?> beanClass, BeanRule beanRule) {
		String offerMethodName = beanRule.getOfferMethodName();

		Method m1 = MethodUtils.getAccessibleMethod(beanClass, offerMethodName);
		Method m2 = MethodUtils.getAccessibleMethod(beanClass, offerMethodName, TRANSLET_ACTION_PARAMETER_TYPES);

		if(m1 == null && m2 == null)
			throw new IllegalArgumentException("No such offer method " + offerMethodName + "() on bean class: " + beanClass);

		Class<?> targetBeanClass;
		
		if(m2 != null) {
			beanRule.setOfferMethod(m2);
			beanRule.setOfferMethodRequiresTranslet(true);
			targetBeanClass = m2.getReturnType();
		} else {
			beanRule.setOfferMethod(m1);
			targetBeanClass = m1.getReturnType();
		}

		beanRule.setTargetBeanClass(targetBeanClass);

		return targetBeanClass;
	}

	public static Class<?> determineTargetBeanClassForFactoryBean(Class<?> beanClass, BeanRule beanRule) {
		try {
			Method m = MethodUtils.getAccessibleMethod(beanClass, FactoryBean.FACTORY_METHOD_NAME);
			Class<?> targetBeanClass = m.getReturnType();
			beanRule.setTargetBeanClass(targetBeanClass);
			return targetBeanClass;
		} catch(Exception e) {
			throw new BeanRuleException("Invalid BeanRule", beanRule);
		}
	}
	
	protected static Class<?> determineFactoryMethodTargetBeanClass(Class<?> beanClass, BeanRule beanRule) {
		if(beanRule.isFactoryBean())
			throw new BeanRuleException("Bean factory method is duplicated. Already implemented the FactoryBean", beanRule);
		
		String factoryMethodName = beanRule.getFactoryMethodName();

		Method m1 = MethodUtils.getAccessibleMethod(beanClass, factoryMethodName);
		Method m2 = MethodUtils.getAccessibleMethod(beanClass, factoryMethodName, TRANSLET_ACTION_PARAMETER_TYPES);

		if(m1 == null && m2 == null)
			throw new IllegalArgumentException("No such factory method " + factoryMethodName + "() on bean class: " + beanClass);

		Class<?> targetBeanClass;
		
		if(m2 != null) {
			beanRule.setFactoryMethod(m2);
			beanRule.setFactoryMethodRequiresTranslet(true);
			targetBeanClass = m2.getReturnType();
		} else {
			beanRule.setFactoryMethod(m1);
			targetBeanClass = m1.getReturnType();
		}
		
		beanRule.setTargetBeanClass(targetBeanClass);
		
		return targetBeanClass;
	}

	public static void checkInitMethod(Class<?> beanClass, BeanRule beanRule) {
		if(beanRule.isInitializableBean())
			throw new BeanRuleException("Bean initialization method is duplicated. Already implemented the InitializableBean", beanRule);

		if(beanRule.isInitializableTransletBean())
			throw new BeanRuleException("Bean initialization method is duplicated. Already implemented the InitializableTransletBean", beanRule);

		String initMethodName = beanRule.getInitMethodName();

		Method m1 = MethodUtils.getAccessibleMethod(beanClass, initMethodName);
		Method m2 = MethodUtils.getAccessibleMethod(beanClass, initMethodName, TRANSLET_ACTION_PARAMETER_TYPES);

		if(m1 == null && m2 == null)
			throw new IllegalArgumentException("No such initialization method " + initMethodName + "() on bean class: " + beanClass);

		if(m2 != null) {
			beanRule.setInitMethod(m2);
			beanRule.setInitMethodRequiresTranslet(true);
		} else {
			beanRule.setInitMethod(m1);
		}
	}

	public static void checkDestroyMethod(Class<?> beanClass, BeanRule beanRule) {
		if(beanRule.isDisposableBean())
			throw new BeanRuleException("Bean destroy method  is duplicated. Already implemented the DisposableBean", beanRule);

		String destroyMethodName = beanRule.getDestroyMethodName();
		Method m = MethodUtils.getAccessibleMethod(beanClass, destroyMethodName);
		
		if(m == null)
			throw new IllegalArgumentException("No such destroy method " + destroyMethodName + "() on bean class: " + beanClass);
		
		beanRule.setDestroyMethod(m);
	}
	
	public static void checkTransletActionParameter(BeanActionRule beanActionRule, BeanRule beanRule) {
		Class<?> beanClass = beanRule.getTargetBeanClass();
		String methodName = beanActionRule.getMethodName();

		Method m1 = MethodUtils.getAccessibleMethod(beanClass, methodName);
		Method m2 = MethodUtils.getAccessibleMethod(beanClass, methodName, TRANSLET_ACTION_PARAMETER_TYPES);

		if(m1 == null && m2 == null)
			throw new IllegalArgumentException("No such action method " + methodName + "() on bean class: " + beanClass);
		
		if(m2 != null) {
			beanActionRule.setMethod(m2);
			beanActionRule.setRequiresTranslet(true);
		} else {
			beanActionRule.setMethod(m1);
		}
	}
	
}
