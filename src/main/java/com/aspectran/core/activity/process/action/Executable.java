/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.aspectran.core.activity.process.action;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.context.rule.type.ActionType;

/**
 * The Interface Executable.
 * 
 * <p>Created: 2008. 03. 23 오전 10:38:29</p>
 */
public interface Executable {

	/**
	 * Gets the action id.
	 * 
	 * @return the id
	 */
	public String getActionId();

	/**
	 * Gets the qualified action id.
	 *
	 * @return the qualified action id
	 */
	public String getQualifiedActionId();

	/**
	 * Execute this action.
	 *
	 * @param activity the activity
	 * @return the result of action execution
	 * @throws Exception the exception
	 * @throws ActionExecutionException the action execution exception
	 */
	public Object execute(Activity activity) throws Exception;
	
	/**
	 * Checks if is hidden action.
	 * 
	 * @return true, if is hidden action
	 */
	public boolean isHidden();
	
	/**
	 * Gets the action list.
	 * 
	 * @return the action list
	 */
	public ActionList getParent();
	
	/**
	 * Gets the Action Type.
	 *
	 * @return the Action Type
	 */
	public ActionType getActionType();
	
	/**
	 * Gets the action rule.
	 *
	 * @param <T> the generic type
	 * @return the action rule
	 */
	public <T> T getActionRule();
	
}
