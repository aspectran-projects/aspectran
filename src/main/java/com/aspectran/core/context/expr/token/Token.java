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
package com.aspectran.core.context.expr.token;

import com.aspectran.core.context.rule.ability.BeanReferenceInspectable;
import com.aspectran.core.context.rule.type.BeanReferrerType;
import com.aspectran.core.context.rule.type.TokenDirectiveType;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.util.ToStringBuilder;

/**
 * The Class Token.
 *
 *<ul>
 *  <li>${parameterName}
 *  <li>${parameterName:defaultValue}
 *  <li>{@literal @}{attributeName}
 *  <li>{@literal @}{attributeName:defaultValue}
 *  <li>{@literal @}{attributeName^getterName:defaultValue}
 *  <li>#{beanId}
 *  <li>#{beanId^getterName}
 *  <li>#{class:className}
 *  <li>#{class:className^getterName}
 *  <li>%{environmentPropertyName}
 *  <li>%{classpath:properties^getterName}
 *  <li>%{classpath:properties^getterName:defaultValue}
 *</ul>
 *
 * <p>Created: 2008. 03. 27 PM 10:20:06</p>
 */
public class Token implements BeanReferenceInspectable {

	private static final BeanReferrerType BEAN_REFERRER_TYPE = BeanReferrerType.TOKEN;

	static final char BEAN_SYMBOL = '#';

	static final char TEMPLATE_SYMBOL = '~';

	static final char PARAMETER_SYMBOL = '$';

	static final char ATTRIBUTE_SYMBOL = '@';

	static final char PROPERTY_SYMBOL = '%';

	static final char START_BRACKET = '{';

	static final char END_BRACKET = '}';

	static final char VALUE_SEPARATOR = ':';
	
	static final char GETTER_SEPARATOR = '^';
	
	private final TokenType type;

	private TokenDirectiveType directiveType;

	private final String name;
	
	private String value;

	private Object alternativeValue;
	
	private String getterName;
	
	/**
	 * Instantiates a new Token.
	 *
	 * @param type the token type
	 * @param nameOrValue token's name or value of this token.
	 * 		If token type is TEXT then will be a value of this token.
	 */
	public Token(TokenType type, String nameOrValue) {
		this.type = type;

		if (type == TokenType.TEXT) {
			this.name = null;
			this.value = nameOrValue;
		} else {
			if (nameOrValue == null) {
				throw new IllegalArgumentException("The nameOrValue argument must not be null.");
			}
			this.name = nameOrValue;
		}
	}
	
	/**
	 * Gets the token type.
	 * 
	 * @return the token type
	 */
	public TokenType getType() {
		return type;
	}

	/**
	 * Gets the token directive type.
	 *
	 * @return the token directive type
	 */
	public TokenDirectiveType getDirectiveType() {
		return directiveType;
	}

	/**
	 * Sets the token directive type.
	 *
	 * @param directiveType the token directive type
	 */
	public void setDirectiveType(TokenDirectiveType directiveType) {
		this.directiveType = directiveType;
	}

	/**
	 * Gets the token name.
	 * 
	 * @return the token name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the token's default value or bean's class name.
	 * If the token's type is Bean and token's name is "class" then token's
	 * value is class name of the Bean. Others that is default value.
	 * 
	 * @return the default value or bean's class name
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the token's default value or bean's class name.
	 *
	 * @param value the default value or bean's class name
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Gets the name of the property whose value is to be retrieved.
	 * 
	 * @return the name of the property whose value is to be retrieved
	 */
	public String getGetterName() {
		return getterName;
	}

	/**
	 * Gets the alternative value.
	 *
	 * @return the alternative value
	 */
	public Object getAlternativeValue() {
		return alternativeValue;
	}

	/**
	 * Sets the alternative value.
	 *
	 * @param value the new alternative value
	 */
	public void setAlternativeValue(Object value) {
		this.alternativeValue = value;
	}

	/**
	 * Sets the name of the property whose value is to be retrieved.
	 * 
	 * @param getterName the name of the property whose value is to be retrieved
	 */
	public void setGetterName(String getterName) {
		this.getterName = getterName;
	}

	@Override
	public BeanReferrerType getBeanReferrerType() {
		return BEAN_REFERRER_TYPE;
	}

	public String stringify() {
		if (type == TokenType.TEXT) {
			return value;
		} else if (type == TokenType.BEAN) {
			StringBuilder sb = new StringBuilder();
			sb.append(BEAN_SYMBOL);
			sb.append(START_BRACKET);
			if (name != null) {
				sb.append(name);
			}
			if (value != null) {
				sb.append(VALUE_SEPARATOR);
				sb.append(value);
			}
			if (getterName != null) {
				sb.append(GETTER_SEPARATOR);
				sb.append(getterName);
			}
			sb.append(END_BRACKET);
			return sb.toString();
		} else if (type == TokenType.TEMPLATE) {
			StringBuilder sb = new StringBuilder();
			sb.append(TEMPLATE_SYMBOL);
			sb.append(START_BRACKET);
			if (name != null) {
				sb.append(name);
			}
			if (value != null) {
				sb.append(VALUE_SEPARATOR);
				sb.append(value);
			}
			if (getterName != null) {
				sb.append(GETTER_SEPARATOR);
				sb.append(getterName);
			}
			sb.append(END_BRACKET);
			return sb.toString();
		} else if (type == TokenType.PARAMETER) {
			StringBuilder sb = new StringBuilder();
			sb.append(PARAMETER_SYMBOL);
			sb.append(START_BRACKET);
			if (name != null) {
				sb.append(name);
			}
			if (value != null) {
				sb.append(VALUE_SEPARATOR);
				sb.append(value);
			}
			sb.append(END_BRACKET);
			return sb.toString();
		} else if (type == TokenType.ATTRIBUTE) {
			StringBuilder sb = new StringBuilder();
			sb.append(ATTRIBUTE_SYMBOL);
			sb.append(START_BRACKET);
			if (name != null) {
				sb.append(name);
			}
			if (getterName != null) {
				sb.append(GETTER_SEPARATOR);
				sb.append(getterName);
			}
			if (value != null) {
				sb.append(VALUE_SEPARATOR);
				sb.append(value);
			}
			sb.append(END_BRACKET);
			return sb.toString();
		} else if (type == TokenType.PROPERTY) {
			StringBuilder sb = new StringBuilder();
			sb.append(PROPERTY_SYMBOL);
			sb.append(START_BRACKET);
			if (name != null) {
				sb.append(name);
			}
			if (value != null) {
				sb.append(VALUE_SEPARATOR);
				sb.append(value);
			}
			if (getterName != null) {
				sb.append(GETTER_SEPARATOR);
				sb.append(getterName);
			}
			if (alternativeValue != null) {
				sb.append(VALUE_SEPARATOR);
				sb.append(alternativeValue);
			}
			sb.append(END_BRACKET);
			return sb.toString();
		} else {
			throw new InvalidTokenException("Unknown token type", this);
		}
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder();
		tsb.append("type", type);
		tsb.append("name", name);
		tsb.append("getterName", getterName);
		tsb.append("value", value);
		tsb.append("alternativeValue", alternativeValue);
		return tsb.toString();
	}
	
	/**
	 * Returns whether a specified character is the token symbol.
	 * 
	 * @param c a character
	 * @return true, if a specified character is one of the token symbols
	 */
	public static boolean isTokenSymbol(char c) {
		return (c == BEAN_SYMBOL
				|| c == TEMPLATE_SYMBOL
				|| c == PARAMETER_SYMBOL
				|| c == ATTRIBUTE_SYMBOL
				|| c == PROPERTY_SYMBOL);
	}
	
	/**
	 * Returns the token type for the specified character.
	 * 
	 * @param symbol the token symbol character
	 * @return the token type
	 */
	public static TokenType resolveTypeAsSymbol(char symbol) {
		TokenType type;
		if (symbol == Token.BEAN_SYMBOL) {
			type = TokenType.BEAN;
		} else if (symbol == Token.TEMPLATE_SYMBOL) {
			type = TokenType.TEMPLATE;
		} else if (symbol == Token.PARAMETER_SYMBOL) {
			type = TokenType.PARAMETER;
		} else if (symbol == Token.ATTRIBUTE_SYMBOL) {
			type = TokenType.ATTRIBUTE;
		} else if (symbol == Token.PROPERTY_SYMBOL) {
			type = TokenType.PROPERTY;
		} else {
			throw new IllegalArgumentException("Unknown token symbol");
		}
		return type;
	}

}
