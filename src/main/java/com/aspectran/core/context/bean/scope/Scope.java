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
package com.aspectran.core.context.bean.scope;

import java.util.concurrent.locks.ReadWriteLock;

import com.aspectran.core.context.rule.BeanRule;

/**
 * The Interface Scope.
 *
 * @author Juho Jeong
 * @since 2011. 3. 12.
 */
public interface Scope {

	ReadWriteLock getScopeLock();

	Object getBean(BeanRule beanRule);

	void putBean(BeanRule beanRule, Object bean);

	/**
	 * Destroy all scoped beans in this scope.
	 */
	void destroy();
	
}
