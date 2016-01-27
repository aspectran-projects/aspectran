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
package com.aspectran.web.activity.response.view;

import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.result.ActionResult;
import com.aspectran.core.activity.process.result.ContentResult;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.response.dispatch.ViewDispatchException;
import com.aspectran.core.activity.response.dispatch.ViewDispatcher;
import com.aspectran.core.adapter.RequestAdapter;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.context.rule.DispatchResponseRule;
import com.aspectran.core.context.rule.ResponseRule;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * JSP or other web resource integration.
 * 
 * @since 2008. 03. 22 오후 5:51:58
 */
public class JspViewDispatcher implements ViewDispatcher {

	private static final Log log = LogFactory.getLog(JspViewDispatcher.class);

	private static final boolean debugEnabled = log.isDebugEnabled();
	
	private static final boolean traceEnabled = log.isTraceEnabled();
	
	private String templateNamePrefix;

	private String templateNameSuffix;
	
	/**
	 * Sets the template name prefix.
	 *
	 * @param templateNamePrefix the new template name prefix
	 */
	public void setTemplateNamePrefix(String templateNamePrefix) {
		this.templateNamePrefix = templateNamePrefix;
	}

	/**
	 * Sets the template name suffix.
	 *
	 * @param templateNameSuffix the new template name suffix
	 */
	public void setTemplateNameSuffix(String templateNameSuffix) {
		this.templateNameSuffix = templateNameSuffix;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.response.dispatch.ViewDispatcher#dispatch(com.aspectran.core.activity.AspectranActivity, com.aspectran.base.rule.DispatchResponseRule)
	 */
	public void dispatch(Activity activity, DispatchResponseRule dispatchResponseRule) throws ViewDispatchException {
		try {
			String dispatchName = dispatchResponseRule.getDispatchName();
			if(dispatchName == null) {
				log.warn("No specified dispatch name " + dispatchResponseRule);
				return;
			}
			
			if(templateNamePrefix != null && templateNameSuffix != null) {
				dispatchName = templateNamePrefix + dispatchName + templateNameSuffix;
			} else if(templateNamePrefix != null) {
				dispatchName = templateNamePrefix + dispatchName;
			} else if(templateNameSuffix != null) {
				dispatchName = dispatchName + templateNameSuffix;
			}
			
			RequestAdapter requestAdapter = activity.getRequestAdapter();
			ResponseAdapter responseAdapter = activity.getResponseAdapter();

			String contentType = dispatchResponseRule.getContentType();
			String outputEncoding = dispatchResponseRule.getCharacterEncoding();

			if(contentType != null)
				responseAdapter.setContentType(contentType);

			if(outputEncoding != null)
				responseAdapter.setCharacterEncoding(outputEncoding);
			else {
				String characterEncoding = activity.getResponseSetting(ResponseRule.CHARACTER_ENCODING_SETTING_NAME);
				
				if(characterEncoding != null)
					responseAdapter.setCharacterEncoding(characterEncoding);
			}
			
			ProcessResult processResult = activity.getProcessResult();

			if(processResult != null)
				setAttribute(requestAdapter, processResult);

			HttpServletRequest request = requestAdapter.getAdaptee();
			HttpServletResponse response = responseAdapter.getAdaptee();
			
			RequestDispatcher requestDispatcher = request.getRequestDispatcher(dispatchName);
			requestDispatcher.forward(request, response);

			if(traceEnabled) {
				Enumeration<String> attrNames = requestAdapter.getAttributeNames();

				if(attrNames.hasMoreElements()) {
					StringBuilder sb2 = new StringBuilder(256);
					sb2.append("request atttibute names [");
					String name = null;

					while(attrNames.hasMoreElements()) {
						if(name != null)
							sb2.append(", ");

						name = attrNames.nextElement();
						sb2.append(name);
					}

					sb2.append("]");
					log.trace(sb2.toString());
				}
			}

			if(debugEnabled)
				log.debug("dispatch to a JSP {templateFile: " + dispatchName + "}");

		} catch(Exception e) {
			throw new ViewDispatchException("JSP View Dispatch Error: " + dispatchResponseRule, e);
		}
	}

	/**
	 * Stores an attribute in request.
	 *
	 * @param requestAdapter the request adapter
	 * @param processResult the process result
	 */
	private void setAttribute(RequestAdapter requestAdapter, ProcessResult processResult) {
		for(ContentResult contentResult : processResult) {
			for(ActionResult actionResult : contentResult) {
				Object actionResultValue = actionResult.getResultValue();

				if(actionResultValue instanceof ProcessResult) {
					setAttribute(requestAdapter, (ProcessResult)actionResultValue);
				} else {
					String actionId = actionResult.getActionId();
					if(actionId != null)
						requestAdapter.setAttribute(actionId, actionResultValue);
				}
			}
		}
	}

}
