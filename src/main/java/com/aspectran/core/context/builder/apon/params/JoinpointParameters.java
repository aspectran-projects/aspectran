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
package com.aspectran.core.context.builder.apon.params;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefine;
import com.aspectran.core.util.apon.ParameterValueType;

public class JoinpointParameters extends AbstractParameters {

	public static final ParameterDefine scope;
	public static final ParameterDefine method;
	public static final ParameterDefine pointcut;
	
	private static final ParameterDefine[] parameterDefines;
	
	static {
		scope = new ParameterDefine("scope", ParameterValueType.STRING);
		method = new ParameterDefine("method", ParameterValueType.STRING);
		pointcut = new ParameterDefine("pointcut", PointcutParameters.class);
		
		parameterDefines = new ParameterDefine[] {
				scope,
				method,
				pointcut
		};
	}
	
	public JoinpointParameters() {
		super(parameterDefines);
	}
	
	public JoinpointParameters(String text) {
		super(parameterDefines, text);
	}
	
}
