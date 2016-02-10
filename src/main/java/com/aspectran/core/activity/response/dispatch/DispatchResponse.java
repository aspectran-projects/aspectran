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
package com.aspectran.core.activity.response.dispatch;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.response.Response;
import com.aspectran.core.context.rule.DispatchResponseRule;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * JSP or other web resource integration.
 * 
 * <p> Created: 2008. 03. 22 PM 5:51:58</p>
 */
public class DispatchResponse implements Response {

	private final Log log = LogFactory.getLog(DispatchResponse.class);

	private final boolean debugEnabled = log.isDebugEnabled();

	private final DispatchResponseRule dispatchResponseRule;
	
	private ViewDispatcher viewDispatcher;

	/**
	 * Instantiates a new DispatchResponse with specified DispatchResponseRule.
	 * 
	 * @param dispatchResponseRule the dispatch response rule
	 */
	public DispatchResponse(DispatchResponseRule dispatchResponseRule) {
		this.dispatchResponseRule = dispatchResponseRule;
	}

	@Override
	public void response(Activity activity) {
		try {
			if(debugEnabled) {
				log.debug("response " + dispatchResponseRule);
			}

			determineViewDispatcher(activity);
			
			if(viewDispatcher != null) {
				viewDispatcher.dispatch(activity, dispatchResponseRule);
			}
		} catch(Exception e) {
			throw new DispatchResponseException(dispatchResponseRule, e);
		}
	}

	/**
	 * Gets the dispatch response rule.
	 * 
	 * @return the dispatch response rule
	 */
	public DispatchResponseRule getDispatchResponseRule() {
		return dispatchResponseRule;
	}

	@Override
	public String getContentType() {
		return dispatchResponseRule.getContentType();
	}

	@Override
	public ResponseType getResponseType() {
		return DispatchResponseRule.RESPONSE_TYPE;
	}

	@Override
	public ActionList getActionList() {
		return dispatchResponseRule.getActionList();
	}

	@Override
	public Response replicate() {
		DispatchResponseRule drr = dispatchResponseRule.replicate();
		Response response = new DispatchResponse(drr);
		return response;
	}

	/**
	 * Determine the view dispatcher.
	 *
	 * @param activity the current Activity
	 */
	private void determineViewDispatcher(Activity activity) {
		if(viewDispatcher == null) {
			synchronized(this) {
				if(viewDispatcher == null) {
					String viewDispatcherName = activity.getResponseSetting(ViewDispatcher.VIEW_DISPATCHER_SETTING_NAME);
					if(viewDispatcherName == null)
						throw new DispatchResponseException("The settings name '" + ViewDispatcher.VIEW_DISPATCHER_SETTING_NAME + "' has not been specified in the default response rule.");
					
					viewDispatcher = activity.getBean(viewDispatcherName);
					
					if(viewDispatcher == null)
						throw new DispatchResponseException("No bean named '" + viewDispatcherName + "' is defined.");
				}
			}
		}
	}

	@Override
	public String toString() {
		return dispatchResponseRule.toString();
	}

}
