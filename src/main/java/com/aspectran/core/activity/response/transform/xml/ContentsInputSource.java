/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.activity.response.transform.xml;

import org.xml.sax.InputSource;

import com.aspectran.core.activity.process.result.ProcessResult;

/**
 * The Class ContentsInputSource.
 * 
 * <p>Created: 2008. 05. 26 오후 2:03:25</p>
 */
public class ContentsInputSource extends InputSource {

	private ProcessResult processResult;
	
	/**
	 * Instantiates a new contents input source.
	 * 
	 * @param processResult the result of processing
	 */
	public ContentsInputSource(ProcessResult processResult) {
		this.processResult = processResult;
	}
	
	/**
	 * Gets the the result of processing.
	 * 
	 * @return the result of processing
	 */
	public ProcessResult getProcessResult() {
		return processResult;
	}

}
