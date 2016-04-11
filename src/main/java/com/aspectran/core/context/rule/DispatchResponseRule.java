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

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.response.dispatch.ViewDispatcher;
import com.aspectran.core.context.expr.TokenExpression;
import com.aspectran.core.context.expr.TokenExpressor;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.expr.token.Tokenizer;
import com.aspectran.core.context.rule.ability.ActionPossessSupport;
import com.aspectran.core.context.rule.ability.Replicable;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.ToStringBuilder;

/**
 * The Class DispatchResponseRule.
 * 
 * <p>Created: 2008. 03. 22 PM 5:51:58</p>
 */
public class DispatchResponseRule extends ActionPossessSupport implements Replicable<DispatchResponseRule> {
	
	public static final ResponseType RESPONSE_TYPE = ResponseType.DISPATCH;
	
	private String name;

	private Token[] nameTokens;
	
	private String contentType;

	private String characterEncoding;
	
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
	 * Gets the dispatch name.
	 *
	 * @return the dispatch name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the dispatch name.
	 *
	 * @param activity the activity
	 * @return the dispatch name
	 */
	public String getName(Activity activity) {
		if(nameTokens != null && nameTokens.length > 0) {
			TokenExpressor expressor = new TokenExpression(activity);
			return expressor.expressAsString(nameTokens);
		} else {
			return name;
		}
	}

	/**
	 * Sets the dispatch name.
	 *
	 * @param name the new dispatch name
	 */
	public void setName(String name) {
		this.name = name;

		List<Token> tokens = Tokenizer.tokenize(name, true);
		int tokenCount = 0;

		for(Token t : tokens) {
			if(t.getType() != TokenType.TEXT)
				tokenCount++;
		}

		if(tokenCount > 0)
			this.nameTokens = tokens.toArray(new Token[tokens.size()]);
		else
			this.nameTokens = null;
	}

	public void setName(String name, Token[] nameTokens) {
		this.name = name;
		this.nameTokens = nameTokens;
	}

	/**
	 * Gets the tokens of the dispatch name.
	 *
	 * @return the tokens of the dispatch name
	 */
	public Token[] getNameTokens() {
		return nameTokens;
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
	 * Gets the character encoding.
	 * 
	 * @return the characterEncoding
	 */
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/**
	 * Sets the character encoding.
	 * 
	 * @param characterEncoding the characterEncoding to set
	 */
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	/**
	 * Gets the default response.
	 *
	 * @return the default response
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

	@Override
	public DispatchResponseRule replicate() {
		return replicate(this);
	}
	
	@Override
	public String toString() {
		return toString(null, null);
	}

	/**
	 * Returns a string representation of <code>DispatchResponseRule</code> with used <code>Dispatcher</code>.
	 *
	 * @param viewDispatcher the view dispatcher
	 * @param dispatchName the new dispatch name
	 * @return a string representation of <code>DispatchResponseRule</code>.
	 */
	public String toString(ViewDispatcher viewDispatcher, String dispatchName) {
		ToStringBuilder tsb = new ToStringBuilder();
		tsb.appendForce("responseType", RESPONSE_TYPE);
		tsb.appendForce("name", name);
		tsb.append("dispatchName", dispatchName);
		tsb.append("contentType", contentType);
		tsb.append("characterEncoding", characterEncoding);
		tsb.append("defaultResponse", defaultResponse);
		tsb.append("viewDispatcher", viewDispatcher);
		return tsb.toString();
	}

	/**
	 * Returns a new instance of DispatchResponseRule.
	 *
	 * @param dispatchName the dispatch name
	 * @param contentType the content type
	 * @param characterEncoding the character encoding
	 * @param defaultResponse the default response
	 * @return an instance of DispatchResponseRule
	 */
	public static DispatchResponseRule newInstance(String dispatchName, String contentType, String characterEncoding, Boolean defaultResponse) {
		DispatchResponseRule drr = new DispatchResponseRule();
		drr.setName(dispatchName);
		drr.setContentType(contentType);
		drr.setCharacterEncoding(characterEncoding);
		drr.setDefaultResponse(defaultResponse);

		return drr;
	}

	/**
	 * Returns a new instance of DispatchResponseRule.
	 *
	 * @param dispatchName the dispatch name
	 * @param contentType the content type
	 * @param characterEncoding the character encoding
	 * @return the dispatch response rule
	 */
	public static DispatchResponseRule newInstance(String dispatchName, String contentType, String characterEncoding) {
		return newInstance(dispatchName, contentType, characterEncoding, null);
	}

	/**
	 * Returns a new derived instance of DispatchResponseRule.
	 *
	 * @param dispatchResponseRule an instance of DispatchResponseRulethe
	 * @return the dispatch response rule
	 */
	public static DispatchResponseRule replicate(DispatchResponseRule dispatchResponseRule) {
		DispatchResponseRule drr = new DispatchResponseRule();
		drr.setName(dispatchResponseRule.getName(), dispatchResponseRule.getNameTokens());
		drr.setContentType(dispatchResponseRule.getContentType());
		drr.setCharacterEncoding(dispatchResponseRule.getCharacterEncoding());
		drr.setDefaultResponse(dispatchResponseRule.getDefaultResponse());
		drr.setActionList(dispatchResponseRule.getActionList());
		
		return drr;
	}

}
