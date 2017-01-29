/**
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
package com.aspectran.core.context.builder;

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.builder.importer.ImportHandler;
import com.aspectran.core.context.builder.importer.Importer;
import com.aspectran.core.context.builder.importer.XmlImportHandler;
import com.aspectran.core.context.rule.type.ImportFileType;

/**
 * The Class XmlActivityContextBuilder.
 * 
 * <p>Created: 2008. 06. 14 PM 8:53:29</p>
 */
public class XmlActivityContextBuilder extends AbstractActivityContextBuilder {
	
	public XmlActivityContextBuilder(ApplicationAdapter applicationAdapter) {
		super(applicationAdapter);
	}

	@Override
	public ActivityContext build(String rootContext) throws ActivityContextBuilderException {
		try {
			if (rootContext == null) {
				throw new IllegalArgumentException("The rootContext argument must not be null.");
			}

			ImportHandler importHandler = new XmlImportHandler(this);
			getContextBuilderAssistant().setImportHandler(importHandler);
			
			Importer importer = resolveImporter(rootContext, ImportFileType.XML);
			importHandler.handle(importer);

			return createActivityContext();
		} catch (Exception e) {
			throw new ActivityContextBuilderException("Failed to build a XML Activity Context: " + rootContext, e);
		}
	}
	
}
