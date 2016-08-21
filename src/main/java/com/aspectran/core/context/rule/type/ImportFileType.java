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
package com.aspectran.core.context.rule.type;

/**
 * The enum ImportFileType.
 * 
 * <p>Created: 2015. 02. 22 AM 4:52:38</p>
 */
public enum ImportFileType {

	XML("xml"),
	APON("apon");

	private final String alias;

	ImportFileType(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return this.alias;
	}

	/**
	 * Returns an {@code ImportFileType} with a value represented by the specified {@code String}.
	 *
	 * @param alias the import file type as a {@code String}
	 * @return an {@code ImportFileType}, may be {@code null}
	 */
	public static ImportFileType resolve(String alias) {
		for(ImportFileType type : values()) {
			if(type.alias.equals(alias))
				return type;
		}
		return null;
	}

}
