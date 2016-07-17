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
package com.aspectran.core.context.rule.type;

/**
 * Types of advice include "around," "before" and "after" advice.
 * <pre>
 * Before advice: Advice that executes before a join point.
 * After advice: Advice to be executed after a join point completes normally.
 * Finally advice: Advice to be executed regardless of the means by which a join point exits (normal or exceptional return).
 * Around advice: Before advice + After advice
 * Job advice: Only used for Scheduler.
 * </pre>
 * 
 * @author Juho Jeong
 */
public enum AspectAdviceType {

	SETTINGS("settings"),
	BEFORE("before"),
	AFTER("after"),
	AROUND("around"),
	FINALLY("finally"),
	JOB("job");

	private final String alias;

	AspectAdviceType(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return this.alias;
	}

	/**
	 * Returns a <code>AspectAdviceType</code> with a value represented by the specified String.
	 *
	 * @param alias the aspect adive type as a String
	 * @return the aspect advice type
	 */
	public static AspectAdviceType resolve(String alias) {
		for(AspectAdviceType type : values()) {
			if(type.alias.equals(alias))
				return type;
		}
		return null;
	}

}
