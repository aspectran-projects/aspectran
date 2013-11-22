/*
 *  Copyright (c) 2008 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.var.rule;

import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.var.rule.ability.ActionPossessable;
import com.aspectran.core.var.type.ResponseType;

/**
 * <p>
 * Created: 2008. 03. 22 오후 5:51:58
 * </p>
 */
public class DispatchResponseRule extends ActionPossessSupport implements ActionPossessable {
	
	public static final ResponseType RESPONSE_TYPE = ResponseType.DISPATCH;
	
	private String contentType;

	protected String characterEncoding;

	private String templateFile;
	
	private String templateEncoding;

	private String templateContent;
	
	private Boolean templateNoCache;
	
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

	public String getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(String templateFile) {
		this.templateFile = templateFile;
	}

	public String getTemplateEncoding() {
		return templateEncoding;
	}

	public void setTemplateEncoding(String templateEncoding) {
		this.templateEncoding = templateEncoding;
	}

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

	public Boolean getTemplateNoCache() {
		return templateNoCache;
	}

	public void setTemplateNoCache(Boolean templateNoCache) {
		this.templateNoCache = templateNoCache;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{contentType=").append(contentType);
		sb.append(", characterEncoding=").append(characterEncoding);
		
		if(actionList != null) {
			sb.append(", actionList=");
			sb.append('[');

			for(int i = 0; i < actionList.size(); i++) {
				Executable action = actionList.get(i);

				if(i > 0)
					sb.append(", ");

				sb.append(action.getActionId());
			}

			sb.append(']');
		}

		if(templateFile != null) {
			sb.append(", templateFile=").append(templateFile);
			sb.append(", templateEncoding=").append(templateEncoding);
			sb.append(", templateNoCache=").append(templateNoCache);
		}
		
		sb.append("}");
		
		return sb.toString();
	}
}
