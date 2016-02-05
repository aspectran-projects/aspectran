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

import java.lang.reflect.Constructor;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.context.bean.annotation.Autowired;
import com.aspectran.core.context.rule.BeanRule;
import com.aspectran.core.util.ClassUtils;

/**
 * The Class BeanAnnotationProcessor.
 */
public abstract class BeanAnnotationProcessor {

	public static void process(BeanRule beanRule, Object bean, Activity activity) {
		Class<?> targetClass = beanRule.getBeanClass();
		Autowired autowiredAnno = targetClass.getAnnotation(Autowired.class);
		boolean required = autowiredAnno.required();

		Constructor c;

	}

	private static Constructor<?> getMatchConstructor(Class<?> clazz, Object[] args) {
		Constructor<?>[] candidates = clazz.getDeclaredConstructors();
		Constructor<?> constructorToUse = null;
		float bestMatchWeight = Float.MAX_VALUE;
		float matchWeight = Float.MAX_VALUE;

		for(Constructor<?> candidate : candidates) {
			if(candidate.isAnnotationPresent(Autowired.class)) {
				matchWeight = ClassUtils.getTypeDifferenceWeight(candidate.getParameterTypes(), args);

				if(matchWeight < bestMatchWeight) {
					constructorToUse = candidate;
					bestMatchWeight = matchWeight;
				}
			}
		}

		return constructorToUse;
	}

}
