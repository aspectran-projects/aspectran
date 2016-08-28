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
import com.aspectran.core.util.apon.ParameterDefinition;
import com.aspectran.core.util.apon.ParameterValueType;

public class JoinpointParameters extends AbstractParameters {

	public static final ParameterDefinition type;
	public static final ParameterDefinition methods;
	public static final ParameterDefinition headers;
	public static final ParameterDefinition pluses;
	public static final ParameterDefinition minuses;
	public static final ParameterDefinition includes;
	public static final ParameterDefinition execludes;
	
	private static final ParameterDefinition[] parameterDefinitions;
	
	static {
		type = new ParameterDefinition("scope", ParameterValueType.STRING);
		methods = new ParameterDefinition("methods", ParameterValueType.STRING, true);
		headers = new ParameterDefinition("headers", ParameterValueType.STRING, true);
		pluses = new ParameterDefinition("+", ParameterValueType.STRING, true, true);
		minuses = new ParameterDefinition("-", ParameterValueType.STRING, true, true);
		includes = new ParameterDefinition("include", PointcutParameters.class, true, true);
		execludes = new ParameterDefinition("execlude", PointcutParameters.class, true, true);
	
		parameterDefinitions = new ParameterDefinition[] {
			type,
			methods,
			headers,
			pluses,
			minuses,
			includes,
			execludes
		};
	}
	
	public JoinpointParameters() {
		super(parameterDefinitions);
	}
	
	public JoinpointParameters(String text) {
		super(parameterDefinitions, text);
	}
	
}
