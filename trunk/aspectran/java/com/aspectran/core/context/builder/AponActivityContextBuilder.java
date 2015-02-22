/*
 *  Copyright (c) 2008 Jeong Ju Ho, All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aspectran.core.context.builder;

import java.io.Reader;

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.builder.apon.AspectranAponDisassembler;
import com.aspectran.core.context.builder.apon.params.AspectranParameters;
import com.aspectran.core.context.rule.type.ImportFileType;
import com.aspectran.core.util.apon.AponReader;
import com.aspectran.core.util.apon.Parameters;

/**
 * AponAspectranContextBuilder.
 * 
 * <p>Created: 2015. 01. 27 오후 10:36:29</p>
 */
public class AponActivityContextBuilder extends AbstractActivityContextBuilder implements ActivityContextBuilder {
	
	private final ApplicationAdapter applicationAdapter;
	
	private final String encoding;
	
	public AponActivityContextBuilder(ApplicationAdapter applicationAdapter) {
		this(applicationAdapter, null, null);
	}
	
	public AponActivityContextBuilder(ApplicationAdapter applicationAdapter, String encoding) {
		this(applicationAdapter, null, encoding);
	}
	
	public AponActivityContextBuilder(ApplicationAdapter applicationAdapter, ClassLoader classLoader) {
		this(applicationAdapter, classLoader, null);
	}
	
	public AponActivityContextBuilder(ApplicationAdapter applicationAdapter, ClassLoader classLoader, String encoding) {
		super(applicationAdapter.getApplicationBasePath(), classLoader);
		this.applicationAdapter = applicationAdapter;
		this.encoding = encoding;
	}

	public ActivityContext build(String rootContext) throws ActivityContextBuilderException {
		try {
			ImportHandler importHandler = new AponImportHandler(this, encoding);
			setImportHandler(importHandler);
			
			Importable importable = makeAponImportable(rootContext);
			Reader reader = importable.getReader(encoding);

			AponReader aponReader = new AponReader();
			Parameters aspectranParameters = aponReader.read(reader, new AspectranParameters());
			
			reader.close();
			
			AspectranAponDisassembler aponDisassembler = new AspectranAponDisassembler(this);
			aponDisassembler.disassembleAspectran(aspectranParameters);
			
			ActivityContext aspectranContext = makeActivityContext(applicationAdapter);

			return aspectranContext;
		} catch(Exception e) {
			throw new ActivityContextBuilderException("AponActivityContext build failed. rootContext: " + rootContext, e);
		}
	}
	
	private Importable makeAponImportable(String rootContext) {
		return makeImportable(rootContext, ImportFileType.APON);
	}
	
}
