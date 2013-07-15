/*
 *  Copyright (c) 2010 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.rule;

/**
 * <p>Created: 2011. 03. 11 오후 5:48:09</p>
 */
public class MultiActivityTransletRule {

	private String name;

	private String responseId;
	
	private TransletRule transletRule;
	
	/**
	 * Instantiates a new multi activity translet rule.
	 */
	public MultiActivityTransletRule() {
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the path.
	 * 
	 * @param name the new path
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the response id.
	 *
	 * @return the response id
	 */
	public String getResponseId() {
		return responseId;
	}

	/**
	 * Sets the response id.
	 *
	 * @param responseId the new response id
	 */
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	/**
	 * Gets the translet rule.
	 *
	 * @return the translet rule
	 */
	public TransletRule getTransletRule() {
		return transletRule;
	}

	/**
	 * Sets the translet rule.
	 *
	 * @param transletRule the new translet rule
	 */
	public void setTransletRule(TransletRule transletRule) {
		this.transletRule = transletRule;
	}

}
