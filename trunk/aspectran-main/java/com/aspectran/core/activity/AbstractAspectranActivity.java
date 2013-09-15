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

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aspectran.core.activity.aspect.result.AspectAdviceResult;
import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.process.ContentList;
import com.aspectran.core.activity.process.ProcessException;
import com.aspectran.core.activity.process.action.ActionExecutionException;
import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.activity.process.result.ActionResult;
import com.aspectran.core.activity.process.result.ContentResult;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.request.RequestException;
import com.aspectran.core.activity.response.ForwardResponse;
import com.aspectran.core.activity.response.ForwardingFailedException;
import com.aspectran.core.activity.response.ResponseException;
import com.aspectran.core.activity.response.Responsible;
import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.adapter.RequestAdapter;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.adapter.SessionAdapter;
import com.aspectran.core.context.AspectranContext;
import com.aspectran.core.context.aspect.AspectAdviceRuleRegistry;
import com.aspectran.core.context.bean.BeanRegistry;
import com.aspectran.core.context.bean.scope.RequestScope;
import com.aspectran.core.context.translet.TransletInstantiationException;
import com.aspectran.core.context.translet.TransletRuleRegistry;
import com.aspectran.core.rule.AspectAdviceRule;
import com.aspectran.core.rule.RequestRule;
import com.aspectran.core.rule.ResponseByContentTypeRule;
import com.aspectran.core.rule.ResponseByContentTypeRuleMap;
import com.aspectran.core.rule.ResponseRule;
import com.aspectran.core.rule.TransletRule;
import com.aspectran.core.type.ResponseType;

/**
 * Action Translator.
 * processes the active request and response.
 * 
 * <p>Created: 2008. 03. 22 오후 5:48:09</p>
 */
public abstract class AbstractAspectranActivity implements AspectranActivity {

	/** The log. */
	private final Log log = LogFactory.getLog(AbstractAspectranActivity.class);
	
	/** The debug enabled. */
	private final boolean debugEnabled = log.isDebugEnabled();

	/** The context. */
	protected final AspectranContext context;
	
	/** The request adapter. */
	private RequestAdapter requestAdapter;

	/** The response adapter. */
	private ResponseAdapter responseAdapter;

	/** The session adapter. */
	private SessionAdapter sessionAdapter;

	/** The translet interface class. */
	private Class<? extends SuperTranslet> transletInterfaceClass;
	
	/** The translet instance class. */
	private Class<? extends AbstractSuperTranslet> transletInstanceClass;

	/** The translet rule. */
	private TransletRule transletRule;
	
	/** The request rule. */
	private RequestRule requestRule;
	
	/** The response rule. */
	private ResponseRule responseRule;
	
	/** The translet. */
	private SuperTranslet translet;
	
	/** The request scope. */
	private RequestScope requestScope;
	
	/** Whether the response is ended. */
	private boolean isResponseEnd;

	/** Whether the response rule has been replaced. */
	private boolean isResponseRuleReplaced;
	
	/** The forward translet name. */
	private String forwardTransletName;
	
	/** The enforceable response id. */
	private String multipleTransletResponseId;
	
	private Exception raisedException;

	/** The translet name. */
	private String transletName;
	
	//private AspectAdviceResult aspectAdviceResult;
	
	/**
	 * Instantiates a new action translator.
	 *
	 * @param context the translets context
	 */
	public AbstractAspectranActivity(AspectranContext context) {
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getRequestAdapter()
	 */
	public RequestAdapter getRequestAdapter() {
		return requestAdapter;
	}
	
	/**
	 * Sets the request adapter.
	 *
	 * @param requestAdapter the new request adapter
	 */
	protected void setRequestAdapter(RequestAdapter requestAdapter) {
		this.requestAdapter = requestAdapter;
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getResponseAdapter()
	 */
	public ResponseAdapter getResponseAdapter() {
		return responseAdapter;
	}

	/**
	 * Sets the response adapter.
	 *
	 * @param responseAdapter the new response adapter
	 */
	protected void setResponseAdapter(ResponseAdapter responseAdapter) {
		this.responseAdapter = responseAdapter;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getSessionAdapter()
	 */
	public SessionAdapter getSessionAdapter() {
		return sessionAdapter;
	}
	
	/**
	 * Sets the session adapter.
	 *
	 * @param sessionAdapter the new session adapter
	 */
	protected void setSessionAdapter(SessionAdapter sessionAdapter) {
		this.sessionAdapter = sessionAdapter;
	}
	
	/**
	 * Gets the translet interface class.
	 *
	 * @return the translet interface class
	 */
	public Class<? extends SuperTranslet> getTransletInterfaceClass() {
		if(transletRule != null && transletRule.getTransletInterfaceClass() != null)
			return transletRule.getTransletInterfaceClass();
		
		return transletInterfaceClass;
	}

	/**
	 * Sets the translet interface class.
	 *
	 * @param transletInterfaceClass the new translet interface class
	 */
	public void setTransletInterfaceClass(Class<? extends SuperTranslet> transletInterfaceClass) {
		this.transletInterfaceClass = transletInterfaceClass;
	}

	/**
	 * Gets the translet instance class.
	 *
	 * @return the translet instance class
	 */
	public Class<? extends AbstractSuperTranslet> getTransletInstanceClass() {
		if(transletRule != null && transletRule.getTransletInstanceClass() != null)
			return transletRule.getTransletInstanceClass();

		return transletInstanceClass;
	}

	/**
	 * Sets the translet instance class.
	 *
	 * @param transletInstanceClass the new translet instance class
	 */
	public void setTransletInstanceClass(Class<? extends AbstractSuperTranslet> transletInstanceClass) {
		this.transletInstanceClass = transletInstanceClass;
	}

	public SuperTranslet getSuperTranslet() {
		return translet;
	}
	
	public void run(String transletName) throws RequestException, ProcessException, ResponseException {
		init(transletName);
		
		AspectAdviceRuleRegistry aspectAdviceRuleRegistry = transletRule.getAspectAdviceRuleRegistry();
		List<AspectAdviceRule> finallyAdviceRuleList = null;
		
		if(aspectAdviceRuleRegistry != null) {
			finallyAdviceRuleList = aspectAdviceRuleRegistry.getFinallyAdviceRuleList();

			if(finallyAdviceRuleList != null) {
				try {
					run(aspectAdviceRuleRegistry);
				} finally {
					execute(finallyAdviceRuleList);
				}
			} else {
				run(aspectAdviceRuleRegistry);
			}
		} else {
			run();
		}
	}

	private void run(AspectAdviceRuleRegistry aspectAdviceRuleRegistry) throws RequestException, ProcessException, ResponseException {
		List<AspectAdviceRule> beforeAdviceRuleList = aspectAdviceRuleRegistry.getBeforeAdviceRuleList();
		List<AspectAdviceRule> afterAdviceRuleList = aspectAdviceRuleRegistry.getAfterAdviceRuleList();

		// execute Before Advice Action
		if(beforeAdviceRuleList != null)
			execute(beforeAdviceRuleList);
		
		run();
		
		// execute After Advice Action
		if(afterAdviceRuleList != null)
			execute(afterAdviceRuleList);
	}

	private void run() throws RequestException, ProcessException, ResponseException {
		//request
		AspectAdviceRuleRegistry aspectAdviceRuleRegistry = requestRule.getAspectAdviceRuleRegistry();
		List<AspectAdviceRule> finallyAdviceRuleList = null;

		if(aspectAdviceRuleRegistry != null) {
			finallyAdviceRuleList = aspectAdviceRuleRegistry.getFinallyAdviceRuleList();

			if(finallyAdviceRuleList != null) {
				try {
					request(aspectAdviceRuleRegistry);
				} finally {
					execute(finallyAdviceRuleList);
				}
			} else {
				request(aspectAdviceRuleRegistry);
			}
		} else {
			request();
		}
		
		//content
		ContentList contentList = transletRule.getContentList();
		
		if(contentList != null) {
			aspectAdviceRuleRegistry = contentList.getAspectAdviceRuleRegistry();
	
			if(aspectAdviceRuleRegistry != null) {
				finallyAdviceRuleList = aspectAdviceRuleRegistry.getFinallyAdviceRuleList();
	
				if(finallyAdviceRuleList != null) {
					try {
						process(aspectAdviceRuleRegistry);
					} finally {
						execute(finallyAdviceRuleList);
					}
				} else {
					process(aspectAdviceRuleRegistry);
				}
			} else {
				process();
			}
		}
		
		//response
		aspectAdviceRuleRegistry = responseRule.getAspectAdviceRuleRegistry();

		if(aspectAdviceRuleRegistry != null) {
			finallyAdviceRuleList = aspectAdviceRuleRegistry.getFinallyAdviceRuleList();

			if(finallyAdviceRuleList != null) {
				try {
					response(aspectAdviceRuleRegistry);
				} finally {
					execute(finallyAdviceRuleList);
				}
			} else {
				response(aspectAdviceRuleRegistry);
			}
		} else {
			response();
		}
	}
	
	public void init(String transletName) {
		TransletRule transletRule = context.getTransletRuleRegistry().getTransletRule(transletName);

		if(transletRule.getMultipleTransletResponseId() != null) {
			multipleTransletResponseId = transletRule.getMultipleTransletResponseId();
		}
		
		Class<? extends SuperTranslet> transletInterfaceClass = getTransletInterfaceClass();
		Class<? extends AbstractSuperTranslet> transletInstanceClass = getTransletInstanceClass();

		//create translet instance
		try {
			Constructor<?> transletInstanceConstructor = transletInstanceClass.getConstructor(AspectranActivity.class);
			Object[] args = new Object[] { this, false };
			
			if(transletRule.isAspectAdviceRuleExists())
				args[1] = true;
			
			translet = (SuperTranslet)transletInstanceConstructor.newInstance(args);
		} catch(Exception e) {
			throw new TransletInstantiationException(transletInterfaceClass, transletInstanceClass, e);
		}

		
		this.transletName = transletName;
		this.transletRule = transletRule;
		this.requestRule = transletRule.getRequestRule();
		this.responseRule = transletRule.getResponseRule();
	}
	
	private void request(AspectAdviceRuleRegistry aspectAdviceRuleRegistry) throws RequestException, ActionExecutionException {
		List<AspectAdviceRule> beforeAdviceRuleList = aspectAdviceRuleRegistry.getBeforeAdviceRuleList();
		List<AspectAdviceRule> afterAdviceRuleList = aspectAdviceRuleRegistry.getAfterAdviceRuleList();

		// execute Before Advice Action
		if(beforeAdviceRuleList != null)
			execute(beforeAdviceRuleList);
		
		if(isResponseEnd)
			return;
		
		request();
		
		if(isResponseEnd)
			return;
		
		// execute After Advice Action
		if(afterAdviceRuleList != null)
			execute(afterAdviceRuleList);
	}
	
	abstract public void request() throws RequestException;
	
	public ProcessResult process(AspectAdviceRuleRegistry aspectAdviceRuleRegistry) throws ProcessException {
		List<AspectAdviceRule> beforeAdviceRuleList = aspectAdviceRuleRegistry.getBeforeAdviceRuleList();
		List<AspectAdviceRule> afterAdviceRuleList = aspectAdviceRuleRegistry.getAfterAdviceRuleList();

		// execute Before Advice Action
		if(beforeAdviceRuleList != null)
			execute(beforeAdviceRuleList);
		
		if(isResponseEnd)
			return translet.getProcessResult();
		
		process();
		
		if(isResponseEnd)
			return translet.getProcessResult();
		
		// execute After Advice Action
		if(afterAdviceRuleList != null)
			execute(afterAdviceRuleList);
		
		return translet.getProcessResult();
	}
	
	public ProcessResult process() throws ProcessException {
		if(debugEnabled) {
			log.debug(">> Processing for path '" + transletName + "'");
		}

		try {
			// execute action on contents area
			ContentList contentList = transletRule.getContentList();
			
			if(contentList != null) {
				for(ActionList actionList : contentList) {
					execute(actionList);
					
					if(isResponseEnd)
						break;
				}
			}
			
			if(!isResponseEnd) {
				// execute action on response area
				Responsible response = getResponse();
				
				if(response != null) {
					ActionList actionList = response.getActionList();
					
					if(actionList != null)
						execute(actionList);
				}
			}
		} catch(Exception e) {
			if(debugEnabled) {
				log.error("An error occurred while executing actions. Cause: " + e, e);
			}

			setRaisedException(e);
			
			ResponseByContentTypeRuleMap responseByContentTypeRuleMap = transletRule.getExceptionHandlingRuleMap();
			
			if(responseByContentTypeRuleMap != null) {
				responseByContentType(responseByContentTypeRuleMap, e);
				
				if(isResponseRuleReplaced)
					return translet.getProcessResult();
			}

			AspectAdviceRuleRegistry aspectAdviceRuleRegistry = transletRule.getAspectAdviceRuleRegistry();
			List<AspectAdviceRule> exceptionRaizedAdviceRuleList = aspectAdviceRuleRegistry.getExceptionRaizedAdviceRuleList();
			
			if(exceptionRaizedAdviceRuleList != null) {
				responseByContentType(exceptionRaizedAdviceRuleList, e);
			}
			
			throw new ProcessException("An error occurred while processing response by content-type. Cause: " + e, e);
		} finally {
			if(requestScope != null) {
				//TODO
				requestScope.destroy();
			}
		}
		
		return translet.getProcessResult();
	}
	
	public ProcessResult getProcessResult() {
		if(translet == null)
			return null;
		
		return translet.getProcessResult();
	}
	
	private void response(AspectAdviceRuleRegistry aspectAdviceRuleRegistry) throws ResponseException, ActionExecutionException {
		List<AspectAdviceRule> beforeAdviceRuleList = aspectAdviceRuleRegistry.getBeforeAdviceRuleList();
		List<AspectAdviceRule> afterAdviceRuleList = aspectAdviceRuleRegistry.getAfterAdviceRuleList();

		// execute Before Advice Action
		if(beforeAdviceRuleList != null)
			execute(beforeAdviceRuleList);
		
		if(isResponseEnd)
			return;
		
		response();
		
		if(isResponseEnd)
			return;
		
		// execute After Advice Action
		if(afterAdviceRuleList != null)
			execute(afterAdviceRuleList);
	}
	
	protected void response() throws ResponseException {
		if(debugEnabled) {
			log.debug(">> Responsing for path '" + transletName + "'");
		}

		Responsible res = getResponse();
		
		if(res != null)
			response(res);
		
		if(forwardTransletName != null)
			forward();
	}
	
	/**
	 * Execute.
	 *
	 * @param actionList the action list
	 * @throws ActionExecutionException the action execution exception
	 */
	private void execute(ActionList actionList) throws ActionExecutionException {
		if(debugEnabled) {
			log.debug("Content " + actionList.toString());
		}
//
//		if(isResponseEnd) {
//			if(debugEnabled) {
//				log.debug("Response has already ended.");
//			}
//
//			return;
//		}
		
		if(!actionList.isHidden()) {
			ContentResult contentResult = new ContentResult();
			contentResult.setContentId(actionList.getContentId());

			translet.addContentResult(contentResult);
		}
		
		for(Executable action : actionList) {
			AspectAdviceRuleRegistry aspectAdviceRuleRegistry = action.getAspectAdviceRuleRegistry();
			List<AspectAdviceRule> finallyAdviceRuleList = null;
			
			if(aspectAdviceRuleRegistry != null) {
				finallyAdviceRuleList = aspectAdviceRuleRegistry.getFinallyAdviceRuleList();
				
				if(finallyAdviceRuleList != null) {
					try {
						execute(action, aspectAdviceRuleRegistry);
					} finally {
						execute(finallyAdviceRuleList);
					}
				} else {
					execute(action, aspectAdviceRuleRegistry);
				}
			} else {
				execute(action);
			}
			
			if(isResponseEnd)
				break;
		}
	}
	
	private void execute(Executable action, AspectAdviceRuleRegistry aspectAdviceRuleRegistry) throws ActionExecutionException {
		List<AspectAdviceRule> beforeAdviceRuleList = aspectAdviceRuleRegistry.getBeforeAdviceRuleList();
		List<AspectAdviceRule> afterAdviceRuleList = aspectAdviceRuleRegistry.getAfterAdviceRuleList();

		// execute Before Advice Action
		if(beforeAdviceRuleList != null)
			execute(beforeAdviceRuleList);
		
		if(isResponseEnd)
			return;
		
		execute(action);
		
		if(isResponseEnd)
			return;
		
		// execute After Advice Action
		if(afterAdviceRuleList != null)
			execute(afterAdviceRuleList);
	}
	
	private void execute(Executable action) throws ActionExecutionException {
		if(debugEnabled)
			log.debug("Execute " + action.toString());
		
		Object resultValue = action.execute(this);
		
		if(debugEnabled)
			log.debug("  Result " + resultValue);
		
		if(!action.isHidden() && resultValue != ActionResult.NO_RESULT) {
			translet.addActionResult(action.getActionId(), resultValue);
		}
	}
	
	private void execute(List<AspectAdviceRule> aspectAdviceRuleList) throws ActionExecutionException {
		for(AspectAdviceRule aspectAdviceRule : aspectAdviceRuleList) {
			Executable executableAction = aspectAdviceRule.getExecutableAction();
			
			Object adviceActionResult = executableAction.execute(this);
			
			if(adviceActionResult != null && adviceActionResult != ActionResult.NO_RESULT) {
				translet.putAdviceResult(aspectAdviceRule, adviceActionResult);
			}
		}
	}
	
	/**
	 * Check ticket.
	 *
	 * @param ticketCheckActionList the ticket bean action list
	 * @param checkpoint the check point
	 * @throws TicketCheckException the ticket check exception
	private void checkTicket(TicketCheckActionList ticketCheckActionList, TicketCheckpointType checkpoint) throws TicketCheckException {
		try {
			for(TicketCheckAction ticketCheckAction : ticketCheckActionList) {
				TicketCheckRule ticketCheckRule = ticketCheckAction.getTicketCheckRule();
				
				if(ticketCheckRule.getTicketCheckpoint() == checkpoint) {
					if(debugEnabled) {
						log.debug("Check ticket " + ticketCheckAction.toString());
					}
					
					Object result = ticketCheckAction.execute(this);
					
					if(result == Boolean.FALSE) {
						if(ticketCheckRule.getRejectInvalidTicket() == Boolean.TRUE) {
							if(debugEnabled) {
								log.debug("Rejected by ticket: " + ticketCheckRule);
							}
							
							ResponseByContentTypeRule responseByContentTypeRule = ticketCheckRule.getTicketCheckcaseRule().getResponseByContentTypeRule();;
							
							if(responseByContentTypeRule != null) {
								responseByContentType(responseByContentTypeRule);
								return;
							}
							
							throw new TicketCheckRejectedException(ticketCheckRule);
						}
					}
					
					if(isResponseEnd)
						break;
				}
			}
		} catch(ActionExecutionException e) {
			throw new TicketCheckException(e);
		}
	}
	*/
	
	/**
	 * Response.
	 * 
	 * @param res the responsible
	 * 
	 * @throws ResponseException the response exception
	 */
	public void response(Responsible res) throws ResponseException {
//		if(responsible == null)
//			throw new IllegalArgumentException("responsible is null.");

		res.response(this);
		
		if(res.getResponseType() == ResponseType.FORWARD) {
			ForwardResponse forwardResponse = (ForwardResponse)res;
			String forwardTransletName = forwardResponse.getForwardResponseRule().getTransletName();
			setForwardTransletName(forwardTransletName);
		}
		
		responseEnd();
	}
	
	/**
	 * Forwarding.
	 *
	 * @throws ResponseException the active response exception
	 */
	private void forward() throws ResponseException {
		if(debugEnabled) {
			log.debug("Forwarding for translet '" + forwardTransletName + "'");
		}
		
		try {
			ProcessResult processResult = translet.getProcessResult();
			init(forwardTransletName);
			translet.setProcessResult(processResult);
			request();
			process();
			response();
		} catch(Exception e) {
			throw new ForwardingFailedException("Forwarding failed for path '" + forwardTransletName + "'", e);
		}
	}
	
	private ProcessResult responseByContentType(List<AspectAdviceRule> aspectAdviceRuleList, Exception ex) throws ActionExecutionException {
		for(AspectAdviceRule aspectAdviceRule : aspectAdviceRuleList) {
			ResponseByContentTypeRuleMap responseByContentTypeRuleMap = aspectAdviceRule.getResponseByContentTypeRuleMap();
			
			if(aspectAdviceRule.getResponseByContentTypeRuleMap() != null) {
				responseByContentType(responseByContentTypeRuleMap, ex);
				
				if(isResponseRuleReplaced)
					return translet.getProcessResult();
			}
		}
		
		return null;
	}

	private void responseByContentType(ResponseByContentTypeRuleMap responseByContentTypeRuleMap, Exception ex) throws ActionExecutionException {
		ResponseByContentTypeRule rbctr = responseByContentTypeRuleMap.getResponseByContentTypeRule(ex);
		
		responseByContentType(rbctr);

		// execute action on response area
		Responsible response = getResponse();

		if(response != null) {
			ActionList actionList = response.getActionList();
			
			if(actionList != null)
				execute(actionList);
		}
	}

	/**
	 * Response by content type.
	 *
	 * @param responseByContentTypeRule the response by content type rule
	 */
	private void responseByContentType(ResponseByContentTypeRule responseByContentTypeRule) {
		Responsible response = getResponse();
		
		if(response != null && response.getContentType() != null) {
			ResponseRule newResponseRule = responseRule.newResponseRule(responseByContentTypeRule.getResponseMap());
			newResponseRule.setDefaultResponseId(response.getContentType().toString());
			responseRule = newResponseRule;
			isResponseRuleReplaced = true;
		}
		
		if(debugEnabled) {
			log.debug("Response by content type: " + responseRule);
		}

		multipleTransletResponseId = null;
		translet.setProcessResult(null);
	}
	
	/* (non-Javadoc)
	 * @see org.jhlabs.translets.activity.Activity#getForwardTransletName()
	 */
	public String getForwardTransletName() {
		return forwardTransletName;
	}

	/* (non-Javadoc)
	 * @see org.jhlabs.translets.activity.Activity#setForwardTransletName(java.lang.String)
	 */
	public void setForwardTransletName(String forwardTransletName) {
		this.forwardTransletName = forwardTransletName;
	}
	
//	/**
//	 * 응답 시작(계속).
//	 */
//	public void responseStart() {
//		isResponseEnd = false;
//		forwardingPath = null;
//	}
	
	/**
	 * 응답 종료.
	 */
	public void responseEnd() {
		if(debugEnabled) {
			log.debug("Response terminated");
		}
		
		isResponseEnd = true;
	}
	
	/**
	 * 응답 종료 여부 반환.
	 * 
	 * @return true, if checks if is response end
	 */
	public boolean isResponseEnd() {
		return isResponseEnd;
	}

	/**
	 * Checks if is exception raised.
	 *
	 * @return true, if is exception raised
	 */
	public boolean isExceptionRaised() {
		return (raisedException == null);
	}
	
	public Exception getRaisedException() {
		return raisedException;
	}

	public void setRaisedException(Exception raisedException) {
		this.raisedException = raisedException;
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getContext()
	 */
	public AspectranContext getContext() {
		return context;
	}
	
	/**
	 * The <code>response</code> will return to find.
	 *
	 * @param responseId the response id
	 * @return the response
	 */
	public Responsible getResponse(String responseId) {
		if(responseRule == null)
			return null;
		
		return responseRule.getResponseMap().get(responseId);
	}
	
	/**
	 * The <code>response</code> will return to find.
	 *
	 * @return the response
	 */
	public Responsible getResponse() {
		if(responseRule == null)
			return null;

		String responseId = null;
		
		if(multipleTransletResponseId != null && multipleTransletResponseId.length() > 0) {
			if(responseRule.getResponseMap().containsKey(multipleTransletResponseId))
				responseId = multipleTransletResponseId;
		} else {
			responseId = responseRule.getDefaultResponseId();
		}

		if(responseId == null || responseId.length() == 0) {
			if(responseRule.getResponseMap().size() == 1)
				return responseRule.getResponseMap().get(0);
		}
		
		if(responseId == null || responseId.length() == 0) {
			responseId = ResponseRule.DEFAULT_ID;
		}
		
		return responseRule.getResponseMap().get(responseId);
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getBean(java.lang.String)
	 */
	public Object getBean(String id) {
		return context.getBeanRegistry().getBean(id, this);
	}
	
	public abstract AspectranActivity newAspectranActivity();
	
	/**
	 * Gets the application adapter.
	 *
	 * @return the application adapter
	 */
	public ApplicationAdapter getApplicationAdapter() {
		return context.getApplicationAdapter();
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getTransletName()
	 */
	public String getTransletName() {
		return transletName;
	}
	
	/**
	 * Sets the translet name.
	 *
	 * @param transletName the new translet name
	 */
	protected void setTransletName(String transletName) {
		this.transletName = transletName;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getRequestScope()
	 */
	public RequestScope getRequestScope() {
		return requestScope;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#setRequestScope(com.aspectran.core.context.bean.scope.RequestScope)
	 */
	public void setRequestScope(RequestScope requestScope) {
		this.requestScope = requestScope;
	}

	/**
	 * Gets the translet rule.
	 *
	 * @return the translet rule
	 */
	public TransletRule getTransletRule() {
		return transletRule;
	}

	/**
	 * Sets the translet rule.
	 *
	 * @param transletRule the new translet rule
	 */
	public void setTransletRule(TransletRule transletRule) {
		this.transletRule = transletRule;
	}

	/**
	 * Gets the request rule.
	 *
	 * @return the request rule
	 */
	public RequestRule getRequestRule() {
		return requestRule;
	}

	/**
	 * Sets the request rule.
	 *
	 * @param requestRule the new request rule
	 */
	public void setRequestRule(RequestRule requestRule) {
		this.requestRule = requestRule;
	}

	/**
	 * Gets the response rule.
	 *
	 * @return the response rule
	 */
	public ResponseRule getResponseRule() {
		return responseRule;
	}

	/**
	 * Sets the response rule.
	 *
	 * @param responseRule the new response rule
	 */
	public void setResponseRule(ResponseRule responseRule) {
		this.responseRule = responseRule;
	}
	
	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getBeanRegistry()
	 */
	public BeanRegistry getBeanRegistry() {
		return context.getBeanRegistry();
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.AspectranActivity#getTransletRegistry()
	 */
	public TransletRuleRegistry getTransletRuleRegistry() {
		return context.getTransletRuleRegistry();
	}
	
	public Object getRequestSetting(String settingName) {
		AspectAdviceRuleRegistry aspectAdviceRuleRegistry = requestRule.getAspectAdviceRuleRegistry();
		
		if(aspectAdviceRuleRegistry != null)
			return aspectAdviceRuleRegistry.getSetting(settingName);
		
		return null;
	}
	
	public Object getResponseSetting(String settingName) {
		AspectAdviceRuleRegistry aspectAdviceRuleRegistry = responseRule.getAspectAdviceRuleRegistry();
		
		if(aspectAdviceRuleRegistry != null)
			return aspectAdviceRuleRegistry.getSetting(settingName);
		
		return null;
	}
	
	public Object getTransletSetting(String settingName) {
		AspectAdviceRuleRegistry aspectAdviceRuleRegistry = transletRule.getAspectAdviceRuleRegistry();
		
		if(aspectAdviceRuleRegistry != null)
			return aspectAdviceRuleRegistry.getSetting(settingName);
		
		return null;
	}
	
}
