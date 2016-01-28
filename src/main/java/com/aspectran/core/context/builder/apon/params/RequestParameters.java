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
import com.aspectran.core.util.apon.Parameters;

public class RequestParameters extends AbstractParameters implements Parameters {

	public static final ParameterDefine requestMethod;
	public static final ParameterDefine characterEncoding;
	public static final ParameterDefine attributes;
	
	private static final ParameterDefine[] parameterDefines;

	static {
		requestMethod = new ParameterDefine("method", ParameterValueType.STRING);
		characterEncoding = new ParameterDefine("characterEncoding", ParameterValueType.STRING);
		attributes = new ParameterDefine("attribute", ItemHolderParameters.class);
		
		parameterDefines = new ParameterDefine[] {
				requestMethod,
				characterEncoding,
				attributes
		};
	}
	
	public RequestParameters() {
		super(parameterDefines);
	}
	
	public RequestParameters(String text) {
		super(parameterDefines, text);
	}
	
}
