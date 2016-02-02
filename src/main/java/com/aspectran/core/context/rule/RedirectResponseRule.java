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
package com.aspectran.core.context.rule;

import java.util.List;

import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.expr.token.Tokenizer;
import com.aspectran.core.context.rule.ability.ActionPossessable;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.util.BooleanUtils;

/**
 * The Class RedirectResponseRule.
 * 
 * <p>Created: 2008. 03. 22 PM 5:51:58</p>
 */
public class RedirectResponseRule extends ActionPossessSupport implements ActionPossessable, Replicable<RedirectResponseRule> {
	
	public static final ResponseType RESPONSE_TYPE = ResponseType.REDIRECT;

	private String contentType;
	
	private String target;
	
	private Token[] targetTokens;
	
	private Boolean excludeNullParameter;

	private String characterEncoding;
	
	private ItemRuleMap parameterItemRuleMap;

	private Boolean defaultResponse;

	/**
	 * Gets the content type.
	 * 
	 * @return the content type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content type.
	 * 
	 * @param contentType the new content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Gets the target name.
	 * 
	 * @return the target name
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Sets the target name.
	 * 
	 * @param target the new target name
	 */
	public void setTarget(String target) {
		this.target = target;
		this.targetTokens = null;
		
		List<Token> tokens = Tokenizer.tokenize(target, true);
		
		int tokenCount = 0;
		
		for(Token t : tokens) {
			if(t.getType() != TokenType.TEXT)
				tokenCount++;
		}
		
		if(tokenCount > 0)
			this.targetTokens = tokens.toArray(new Token[tokens.size()]);
	}
	
	public void setTarget(String target, Token[] targetTokens) {
		this.target = target;
		this.targetTokens = targetTokens;
	}

	/**
	 * Gets the url tokens.
	 * 
	 * @return the url tokens
	 */
	public Token[] getTargetTokens() {
		return targetTokens;
	}

	/**
	 * Gets the exclude null parameters.
	 * 
	 * @return the exclude null parameters
	 */
	public Boolean getExcludeNullParameter() {
		return excludeNullParameter;
	}

	public boolean isExcludeNullParameter() {
		return BooleanUtils.toBoolean(excludeNullParameter);
	}

	/**
	 * Sets the exclude null parameters.
	 * 
	 * @param excludeNullParameter the new exclude null parameters
	 */
	public void setExcludeNullParameter(Boolean excludeNullParameter) {
		this.excludeNullParameter = excludeNullParameter;
	}

	/**
	 * Gets the character encoding.
	 * 
	 * @return the character encoding
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/**
	 * Sets the character encoding.
	 * 
	 * @param characterEncoding the new character encoding
	 */
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	/**
	 * Gets the parameter rule map.
	 * 
	 * @return the parameter rule map
	 */
	public ItemRuleMap getParameterItemRuleMap() {
		return parameterItemRuleMap;
	}

	/**
	 * Sets the parameter rules.
	 * 
	 * @param parameterItemRuleMap the new parameter rules
	 */
	public void setParameterItemRuleMap(ItemRuleMap parameterItemRuleMap) {
		this.parameterItemRuleMap = parameterItemRuleMap;
	}
	
	/**
	 * Adds the parameter rule.
	 * 
	 * @param parameterItemRule the parameter rule
	 */
	public void addParameterItemRule(ItemRule parameterItemRule) {
		if(parameterItemRuleMap == null) 
			parameterItemRuleMap = new ItemRuleMap();
		
		parameterItemRuleMap.putItemRule(parameterItemRule);
	}
	
	/**
	 * Returns whether the default response.
	 *
	 * @return whether the default response
	 */
	public Boolean getDefaultResponse() {
		return defaultResponse;
	}

	/**
	 * Returns whether the default response.
	 *
	 * @return true, if is default response
	 */
	public boolean isDefaultResponse() {
		return BooleanUtils.toBoolean(defaultResponse);
	}

	/**
	 * Sets whether the default response.
	 *
	 * @param defaultResponse whether the default response
	 */
	public void setDefaultResponse(Boolean defaultResponse) {
		this.defaultResponse = defaultResponse;
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.context.rule.Replicable#replicate()
	 */
	public RedirectResponseRule replicate() {
		return replicate(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{contentType=").append(contentType);
		sb.append(", target=").append(target);
		sb.append(", excludeNullParameters=").append(excludeNullParameter);
		if(defaultResponse != null)
			sb.append(", defaultResponse=").append(defaultResponse);
		sb.append("}");
		
		return sb.toString();
	}
	
	public static RedirectResponseRule newInstance(String contentType, String target, Boolean excludeNullParameters, Boolean defaultResponse) {
		RedirectResponseRule rrr = new RedirectResponseRule();
		rrr.setContentType(contentType);
		
		if(target != null && target.length() > 0)
			rrr.setTarget(target);
		
		rrr.setExcludeNullParameter(excludeNullParameters);
		rrr.setDefaultResponse(defaultResponse);

		return rrr;
	}
	
	public static RedirectResponseRule newInstance(String redirectName) {
		if(redirectName == null)
			throw new IllegalArgumentException("redirectName must not be null.");
		
		RedirectResponseRule rrr = new RedirectResponseRule();
		rrr.setTarget(redirectName);

		return rrr;
	}
	
	public static RedirectResponseRule replicate(RedirectResponseRule redirectResponseRule) {
		RedirectResponseRule rrr = new RedirectResponseRule();
		rrr.setContentType(redirectResponseRule.getContentType());
		rrr.setTarget(redirectResponseRule.getTarget(), redirectResponseRule.getTargetTokens());
		rrr.setExcludeNullParameter(redirectResponseRule.getExcludeNullParameter());
		rrr.setCharacterEncoding(redirectResponseRule.getCharacterEncoding());
		rrr.setParameterItemRuleMap(redirectResponseRule.getParameterItemRuleMap());
		rrr.setDefaultResponse(redirectResponseRule.getDefaultResponse());
		rrr.setActionList(redirectResponseRule.getActionList());

		return rrr;
	}
	
}
