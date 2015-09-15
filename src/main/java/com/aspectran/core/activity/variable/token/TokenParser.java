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
package com.aspectran.core.activity.variable.token;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aspectran.core.context.rule.type.TokenType;

public class TokenParser {

	public static Token[] parse(String value) {
		return parse(value, false);
	}
	
	public static Token[] parse(String value, boolean trimStringToken) {
		if(value == null)
			return null;
		
		List<Token> tokenList = Tokenizer.tokenize(value, trimStringToken);

		Token[] tokens = null;

		if(tokenList.size() > 0) {
			tokens = tokenList.toArray(new Token[tokenList.size()]);
			
			if(!trimStringToken) {
				// 문자열 타입 토큰을 trim하지 않았으면,
				// 처음과 끝에 위치한 토큰 문자열을 각각 앞trim, 뒷trim을 한다.
				tokens = Tokenizer.optimize(tokens);
			}
		} else {
			tokens = null;
		}
		
		return tokens;
	}
	
	public static List<Token[]> parseAsList(String value) {
		if(value == null)
			return null;
		
		List<Token> tokenList = Tokenizer.tokenize(value, true);
		
		List<Token[]> tokensList = null;
		
		for(int i = tokenList.size() - 1; i >= 0; i--) {
			Token t = tokenList.get(i);

			if(t.getType() == TokenType.TEXT && t.getDefaultText() != null) {
				// remove empty token
				if(t.getDefaultText().trim().length() == 0)
					tokenList.remove(i);
			}
		}
		
		if(tokenList.size() > 0) {
			tokensList = new ArrayList<Token[]>();
			
			for(int i = 0; i < tokensList.size(); i++) {
				Token[] ts = new Token[1];
				ts[0] = tokenList.get(i);
				tokensList.add(ts);
			}
		}
		
		return tokensList;
	}
	
	public static Map<String, Token[]> parseAsMap(String value) {
		if(value == null)
			return null;
		
		List<Token> tokenList = Tokenizer.tokenize(value, true);
		
		Map<String, Token[]> tokensMap = null;
		
		for(int i = tokenList.size() - 1; i >= 0; i--) {
			Token t = tokenList.get(i);

			if(t.getType() == TokenType.TEXT ||
					t.getName() == null || t.getName().length() == 0 ||
					t.getDefaultText() == null || t.getDefaultText().length() == 0) {
				tokenList.remove(i);
			}
		}

		if(tokenList.size() > 0) {
			tokensMap = new LinkedHashMap<String, Token[]>();
			
			for(Token t : tokenList) {
				Token[] ts = new Token[1];
				ts[0] = t;
				tokensMap.put(t.getName(), ts);
			}
		}
		
		return tokensMap;
	}
	
//	public static Object parsebyUnityType(ItemUnityType unityType, String value) {
//		if(value == null)
//			return null;
//
//		// When the parameter type is only and each token trim
//		boolean trimStringToken = (unityType != ItemUnityType.SINGLE);
//
//		Object result = null;
//		
//		if(unityType == ItemUnityType.SINGLE)
//			result = parseForSingleItem(value, trimStringToken);
//		else if(unityType == ItemUnityType.LIST)
//			result = parseToList(value, trimStringToken);
//		else if(unityType == ItemUnityType.MAP)
//			result = parseToMap(value, trimStringToken);
//		
//		return result;
//	}
}
