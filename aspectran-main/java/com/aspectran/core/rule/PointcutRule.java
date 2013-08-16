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

import java.util.List;

import com.aspectran.core.type.PointcutType;

public class PointcutRule {

	private PointcutType pointcutType;
	
	private List<String> includePatternList;
	
	private List<String> excludePatternList;
	
	public PointcutType getPointcutType() {
		return pointcutType;
	}

	public void setPointcutType(PointcutType pointcutType) {
		this.pointcutType = pointcutType;
	}

	public List<String> getIncludePatternList() {
		return includePatternList;
	}

	public void setIncludePatternList(List<String> includePatternList) {
		this.includePatternList = includePatternList;
	}

	public List<String> getExcludePatternList() {
		return excludePatternList;
	}

	public void setExcludePatternList(List<String> excludePatternList) {
		this.excludePatternList = excludePatternList;
	}

	public void setPattern(String pattern) {
		parsePattern(pattern);
	}
	
	private void parsePattern(String pattern) {
		
	}
	
}
