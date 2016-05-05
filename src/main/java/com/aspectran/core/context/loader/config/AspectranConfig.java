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
package com.aspectran.core.context.loader.config;

import com.aspectran.core.util.apon.AbstractParameters;
import com.aspectran.core.util.apon.ParameterDefine;

public class AspectranConfig extends AbstractParameters {

	public static final ParameterDefine context;
	public static final ParameterDefine scheduler;
	public static final ParameterDefine web;

	private static final ParameterDefine[] parameterDefines;
	
	static {
		context = new ParameterDefine("context", AspectranContextConfig.class);
		scheduler = new ParameterDefine("scheduler", AspectranSchedulerConfig.class);
		web = new ParameterDefine("web", AspectranWebConfig.class);

		parameterDefines = new ParameterDefine[] {
				context,
				scheduler,
				web
		};
	}
	
	public AspectranConfig() {
		super(parameterDefines);
	}
	
	public AspectranConfig(String text) {
		super(parameterDefines, text);
	}
	
}
