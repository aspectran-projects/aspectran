/*
 *  Copyright (c) 2010 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.activity;

import java.util.List;

import com.aspectran.core.activity.process.action.ActionExecutionException;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.request.RequestException;
import com.aspectran.core.activity.response.ResponseException;
import com.aspectran.core.activity.response.Responsible;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.adapter.RequestAdapter;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.bean.BeanRegistry;
import com.aspectran.core.context.bean.scope.Scope;
import com.aspectran.core.var.rule.AspectAdviceRule;
import com.aspectran.core.var.rule.AspectRule;
import com.aspectran.core.var.type.JoinpointScopeType;

/**
 * Action Translator.
 * processes the active request and response.
 * 
 * <p>Created: 2008. 03. 22 오후 5:48:09</p>
 */
public interface CoreActivity {
	
	public RequestAdapter getRequestAdapter();
	
	public ResponseAdapter getResponseAdapter();
	
	public SessionAdapter getSessionAdapter();
	
	public Class<? extends CoreTranslet> getTransletInterfaceClass();
	
	public Class<? extends CoreTransletImpl> getTransletImplementClass();
	
	public CoreTranslet getSuperTranslet();
	
	public void init(String transletName) throws CoreActivityException;
	
	public void run() throws CoreActivityException;
	
	public void runWithoutResponse() throws CoreActivityException;
	
	public void request() throws RequestException;

	public void execute(List<AspectAdviceRule> aspectAdviceRuleList) throws ActionExecutionException;
	
	public Object execute(AspectAdviceRule aspectAdviceRule) throws ActionExecutionException;
	
	public ProcessResult process() throws CoreActivityException;
	
	public ProcessResult getProcessResult();
	
	public String getForwardTransletName();
	
	public void activityEnd();
	
	public boolean isActivityEnd();
	
	public void response(Responsible res) throws ResponseException;
	
	public void responseByContentType(List<AspectAdviceRule> aspectAdviceRuleList) throws CoreActivityException;
	
	public ActivityContext getActivityContext();
	
	public Responsible getResponse();
	
	public Object getBean(String id);
	
	public ApplicationAdapter getApplicationAdapter();
	
	public String getTransletName();
	
	public Scope getRequestScope();
	
	public void setRequestScope(Scope requestScope);
	
	public CoreActivity newCoreActivity();
	
	public BeanRegistry getBeanRegistry();
	
	public boolean isExceptionRaised();

	public Exception getRaisedException();

	public void setRaisedException(Exception raisedException);
	
	public Object getTransletSetting(String settingName);
	
	public Object getRequestSetting(String settingName);
	
	public Object getResponseSetting(String settingName);
	
	public void registerAspectRule(AspectRule aspectRule) throws ActionExecutionException;
	
	public Object getAspectAdviceBean(String aspectId);
	
	public JoinpointScopeType getJoinpointScope();

	public void close();

}
