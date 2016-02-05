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
 * The enum ContentType.
 * 
 * <p>Created: 2008. 03. 26 AM 12:58:38</p>
 */
public enum ContentType {

	TEXT_PLAIN("text/plain"),
	TEXT_XML("text/xml"),
	TEXT_JSON("application/json"),
	TEXT_APON("text/apon"),
	TEXT_HTML("text/html");

	private final String alias;

	private ContentType(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return this.alias;
	}

	/**
	 * Returns a <code>ContentType</code> with a value represented by the specified String.
	 *
	 * @param alias the specified String
	 * @return the content type
	 */
	public static ContentType lookup(String alias) {
		for(ContentType type : values()) {
			if(type.alias.equals(alias))
				return type;
		}
		return null;
	}

	
}
