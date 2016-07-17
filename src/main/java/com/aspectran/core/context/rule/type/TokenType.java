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
 * The enum TokenType.
 * 
 * <p>Created: 2008. 03. 29 AM 1:21:48</p>
 */
public enum TokenType {

	TEXT("text"),
	PARAMETER("parameter"),
	ATTRIBUTE("attribute"),
	BEAN("bean"),
	PROPERTY("property");

	private final String alias;

	TokenType(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return this.alias;
	}

	/**
	 * Returns a <code>TokenType</code> with a value represented by the specified String.
	 *
	 * @param alias the token type as a String
	 * @return the token type
	 */
	public static TokenType resolve(String alias) {
		for(TokenType type : values()) {
			if(type.alias.equals(alias))
				return type;
		}
		return null;
	}

}
