/*
 * Copyright 2008-2017 Juho Jeong
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
package com.aspectran.embedded.adapter;

import com.aspectran.core.adapter.BasicApplicationAdapter;
import com.aspectran.core.util.SystemUtils;

/**
 * The Class EmbeddedApplicationAdapter.
 */
public class EmbeddedApplicationAdapter extends BasicApplicationAdapter {
	
	private static final String WORKING_DIR_PROPERTY_NAME = "com.aspectran.embedded.workingDir";
	
	/**
	 * Instantiates a new EmbeddedApplicationAdapter.
	 */
	public EmbeddedApplicationAdapter() {
		super(null);
		
		String basePath = SystemUtils.getProperty(WORKING_DIR_PROPERTY_NAME);
		if(basePath != null) {
			setBasePath(basePath);
		}
	}

}
