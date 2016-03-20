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
package com.aspectran.core.activity.process.result;

import com.aspectran.core.util.ToStringBuilder;

/**
 * The Class ActionResult.
 * 
 * <p>Created: 2008. 03. 23 PM 12:01:24</p>
 */
public class ActionResult {
	
	public static final Object NO_RESULT = new Object();

	private final ContentResult parent;

	private String actionId;

	private Object resultValue;

	private boolean hidden;

	public ActionResult(ContentResult parent) {
		this.parent = parent;
		
		if(parent != null) {
			this.parent.addActionResult(this);
		}
	}

	/**
	 * Gets the parent.
	 *
	 * @return the parent
	 */
	public ContentResult getParent() {
		return parent;
	}

	/**
	 * Gets the action id.
	 * 
	 * @return the action id
	 */
	public String getActionId() {
		return actionId;
	}

	/**
	 * Sets the action id.
	 * 
	 * @param actionId the new action id
	 */
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

	/**
	 * Gets the result value.
	 * 
	 * @return the result value
	 */
	public Object getResultValue() {
		return resultValue;
	}

	/**
	 * Sets the result value.
	 * 
	 * @param resultValue the new result value
	 */
	public void setResultValue(Object resultValue) {
		this.resultValue = resultValue;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder();
		tsb.append("actionId", actionId);
		tsb.append("resultValue", resultValue);
		tsb.append("hidden", hidden);
		return tsb.toString();
	}

}
