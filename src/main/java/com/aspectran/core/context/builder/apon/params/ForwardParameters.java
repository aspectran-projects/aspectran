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

public class ForwardParameters extends AbstractParameters {

	public static final ParameterDefine actions;
	public static final ParameterDefine contentType;
	public static final ParameterDefine translet;
	public static final ParameterDefine defaultResponse;
	public static final ParameterDefine attributes;

	private static final ParameterDefine[] parameterDefines;
	
	static {
		actions = new ParameterDefine("action", ActionParameters.class, true, true);
		contentType = new ParameterDefine("contentType", ParameterValueType.STRING);
		translet = new ParameterDefine("translet", ParameterValueType.STRING);
		defaultResponse = new ParameterDefine("defaultResponse", ParameterValueType.BOOLEAN);
		attributes = new ParameterDefine("attribute", ItemHolderParameters.class);

		parameterDefines = new ParameterDefine[] {
				actions,
				contentType,
				translet,
				defaultResponse,
				attributes
		};
	}
	
	public ForwardParameters() {
		super(parameterDefines);
	}
	
	public ForwardParameters(String text) {
		super(parameterDefines, text);
	}
	
}
