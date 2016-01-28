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
package com.aspectran.core.context.template;

import java.util.Locale;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.result.ActivityProcessResultMap;

/**
 * The Class TemplateDataMap.
 */
public class TemplateDataMap extends ActivityProcessResultMap {

	/** @serial */
	private static final long serialVersionUID = -1414688689441309354L;
	
	public TemplateDataMap(Activity activity) {
		super(activity);
	}

	public Locale getLocale() {
		if(requestAdapter != null)
			return requestAdapter.getLocale();

		return null;
	}

}
