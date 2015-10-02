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
package com.aspectran.core.activity;

import java.util.List;

import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.response.Response;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.adapter.RequestAdapter;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.bean.BeanRegistry;
import com.aspectran.core.context.bean.scope.Scope;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.ExceptionHandlingRule;
import com.aspectran.core.context.rule.type.JoinpointScopeType;
import com.aspectran.core.context.rule.type.RequestMethodType;

/**
 * Action Translator.
 * processes the active request and response.
 * 
 * <p>Created: 2008. 03. 22 오후 5:48:09</p>
 */
public interface Activity {

	public Class<? extends Translet> getTransletInterfaceClass();
	
	public Class<? extends CoreTranslet> getTransletImplementClass();

	public void ready(String transletName);
	
	public void ready(String transletName, String restVerb);
	
	public void perform();
	
	public void performWithoutResponse();
	
	public void execute(List<AspectAdviceRule> aspectAdviceRuleList);
	
	public void forceExecute(List<AspectAdviceRule> aspectAdviceRuleList);
	
	public ProcessResult getProcessResult();
	
	public String getForwardTransletName();
	
	public void activityEnd();
	
	public boolean isActivityEnded();
	
	public void response(Response res);
	
	public void responseByContentType(List<ExceptionHandlingRule> exceptionHandlingRuleList);

	public Response getResponse();

	public boolean isExceptionRaised();

	public Exception getRaisedException();

	public void setRaisedException(Exception raisedException);

	public ActivityContext getActivityContext();

	public <T extends Activity> T newActivity();

	public Translet getTranslet();
	
	public String getTransletName();

	public RequestMethodType getRestVerb();
	
	public ApplicationAdapter getApplicationAdapter();

	public SessionAdapter getSessionAdapter();
	
	public RequestAdapter getRequestAdapter();
	
	public ResponseAdapter getResponseAdapter();

	public BeanRegistry getBeanRegistry();
	
	public <T> T getBean(String id);

	public <T> T getTransletSetting(String settingName);
	
	public <T> T getRequestSetting(String settingName);
	
	public <T> T getResponseSetting(String settingName);
	
	public void registerAspectRule(AspectRule aspectRule);
	
	public <T> T  getAspectAdviceBean(String aspectId);
	
	public Scope getRequestScope();

	public void setRequestScope(Scope requestScope);

	public JoinpointScopeType getCurrentJoinpointScope();

	public void finish();

}
