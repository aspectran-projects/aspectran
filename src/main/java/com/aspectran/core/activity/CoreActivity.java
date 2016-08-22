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
package com.aspectran.core.activity;

import java.util.List;

import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.process.ContentList;
import com.aspectran.core.activity.process.ProcessException;
import com.aspectran.core.activity.process.action.ActionExecutionException;
import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.activity.process.result.ActionResult;
import com.aspectran.core.activity.process.result.ContentResult;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.request.RequestException;
import com.aspectran.core.activity.request.RequestMethodNotAllowedException;
import com.aspectran.core.activity.response.ForwardResponse;
import com.aspectran.core.activity.response.Response;
import com.aspectran.core.activity.response.ResponseException;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.aspect.AspectAdviceRulePostRegister;
import com.aspectran.core.context.aspect.AspectAdviceRuleRegister;
import com.aspectran.core.context.aspect.AspectAdviceRuleRegistry;
import com.aspectran.core.context.aspect.pointcut.Pointcut;
import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpressionParser;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.AspectRule;
import com.aspectran.core.context.rule.ExceptionRule;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.RequestRule;
import com.aspectran.core.context.rule.ResponseByContentTypeRule;
import com.aspectran.core.context.rule.ResponseRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.type.ActionType;
import com.aspectran.core.context.rule.type.AspectAdviceType;
import com.aspectran.core.context.rule.type.AspectTargetType;
import com.aspectran.core.context.rule.type.JoinpointScopeType;
import com.aspectran.core.context.rule.type.MethodType;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.context.translet.TransletNotFoundException;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class CoreActivity.
 * 
 * <p>Created: 2008. 03. 22 PM 5:48:09</p>
 */
public class CoreActivity extends AbstractActivity {

	private static final Log log = LogFactory.getLog(CoreActivity.class);
	
	private static final boolean debugEnabled = log.isDebugEnabled();

	private static final boolean traceEnabled = log.isTraceEnabled();

	private TransletRule transletRule;
	
	private RequestRule requestRule;
	
	private ResponseRule responseRule;
	
	private String transletName;
	
	private String forwardTransletName;

	private boolean withoutResponse;

	private MethodType requestMethod;
	
	private Translet translet;
	
	private Activity outerActivity;

	private JoinpointScopeType currentJoinpointScope;
	
	/** Whether the current activity is completed or interrupted. */
	private boolean activityEnded;

	private Throwable raisedException;

	private AspectAdviceRuleRegistry transletAspectAdviceRuleRegistry;
	
	private AspectAdviceRuleRegistry requestAspectAdviceRuleRegistry;
	
	private AspectAdviceRuleRegistry responseAspectAdviceRuleRegistry;
	
	private AspectAdviceRuleRegistry contentAspectAdviceRuleRegistry;
	
	/**
	 * Instantiates a new CoreActivity.
	 *
	 * @param context the current ActivityContext
	 */
	public CoreActivity(ActivityContext context) {
		super(context);
	}
	
	@Override
	public void prepare(String transletName) {
		this.transletName = transletName;
		this.requestMethod = null;

		TransletRule transletRule = getTransletRuleRegistry().getTransletRule(transletName);

		if(transletRule == null) {
			throw new TransletNotFoundException(transletName);
		}

		prepare(transletRule, null);
	}

	@Override
	public void prepare(String transletName, String requestMethod) {
		prepare(transletName, MethodType.resolve(requestMethod));
	}

	@Override
	public void prepare(String transletName, MethodType requestMethod) {
		prepare(transletName, requestMethod, null);
	}
	
	private void prepare(String transletName, MethodType requestMethod, ProcessResult processResult) {
		this.transletName = transletName;
		this.requestMethod = requestMethod;

		TransletRule transletRule = getTransletRuleRegistry().getTransletRule(transletName);

		// for RESTful
		if(transletRule == null && requestMethod != null) {
			transletRule = getTransletRuleRegistry().getRestfulTransletRule(transletName, requestMethod);
		}

		if(transletRule == null) {
			throw new TransletNotFoundException(transletName);
		}

		// for RESTful
		PathVariableMap pathVariableMap = getTransletRuleRegistry().getPathVariableMap(transletRule, transletName);

		prepare(transletRule, processResult);

		if(pathVariableMap != null) {
			pathVariableMap.apply(translet);
		}
	}

	private void prepare(TransletRule transletRule, ProcessResult processResult) {
		try {
			if(debugEnabled) {
				log.debug("translet " + transletRule);
			}

			if(transletRule.getTransletInterfaceClass() != null)
				setTransletInterfaceClass(transletRule.getTransletInterfaceClass());

			if(transletRule.getTransletImplementationClass() != null)
				setTransletImplementationClass(transletRule.getTransletImplementationClass());

			newTranslet();

			if(processResult != null) {
				translet.setProcessResult(processResult);
			}

			prepareRule(transletRule);

			if(forwardTransletName == null) {
				outerActivity = getCurrentActivity();
				setCurrentActivity(this);
				adapt();
			} else {
				forwardTransletName = null;
			}
		} catch(Exception e) {
			throw new ActivityException("Failed to prepare the Activity.", e);
		}
	}

	protected void adapt() throws AdapterException {
	}

	@Override
	public void perform() {
		performTranslet();
	}

	@Override
	public void performWithoutResponse() {
		withoutResponse = true;
		performTranslet();
	}

	@Override
	public void finish() {
		if(outerActivity != null) {
			setCurrentActivity(outerActivity);
		} else {
			removeCurrentActivity();
		}
	}
	
	/**
	 * Perform activity for the translet.
	 */
	private void performTranslet() {
		try {
			try {
				currentJoinpointScope = JoinpointScopeType.TRANSLET;

				// execute Before Advice Action for Translet Joinpoint
				if(transletAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> beforeAdviceRuleList = transletAspectAdviceRuleRegistry.getBeforeAdviceRuleList();
					if(beforeAdviceRuleList != null) {
						execute(beforeAdviceRuleList);
					}
				}
				
				if(!activityEnded) {
					currentJoinpointScope = JoinpointScopeType.REQUEST;
					performRequest();

					if(!activityEnded) {
						if(transletRule.getContentList() != null) {
							currentJoinpointScope = JoinpointScopeType.CONTENT;
							performContent();
						}

						if(!activityEnded && !withoutResponse) {
							currentJoinpointScope = JoinpointScopeType.RESPONSE;
							performResponse();
						}
					}
				}

				// execute After Advice Action for Translet Joinpoint
				if(transletAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> afterAdviceRuleList = transletAspectAdviceRuleRegistry.getAfterAdviceRuleList();
					if(afterAdviceRuleList != null) {
						execute(afterAdviceRuleList);
					}
				}
			} finally {
				if(transletAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> finallyAdviceRuleList = transletAspectAdviceRuleRegistry.getFinallyAdviceRuleList();
					if(finallyAdviceRuleList != null) {
						forceExecute(finallyAdviceRuleList);
					}
				}

				if(getRequestScope() != null) {
					getRequestScope().destroy();
				}
			}
		} catch(RequestMethodNotAllowedException e) {
			throw e;
		} catch(Exception e) {
			setRaisedException(e);
			
			ExceptionRule exceptionRule = transletRule.getExceptionRule();
			if(exceptionRule != null) {
				responseByContentType(exceptionRule);
				if(activityEnded) {
					return;
				}
			}
			
			if(transletAspectAdviceRuleRegistry != null) {
				List<ExceptionRule> exceptionRuleList = transletAspectAdviceRuleRegistry.getExceptionRuleList();
				if(exceptionRuleList != null) {
					responseByContentType(exceptionRuleList);
					if(activityEnded) {
						return;
					}
				}
			}
			
			throw new ActivityException("Failed to perform the activity int the translet scope.", e);
		} finally {
			if(getRequestScope() != null) {
				getRequestScope().destroy();
			}
		}
	}

	/**
	 * Perform activity for the request.
	 */
	private void performRequest() {
		try {
			try {
				// execute Before Advice Action for Request Joinpoint
				if(requestAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> beforeAdviceRuleList = requestAspectAdviceRuleRegistry.getBeforeAdviceRuleList();
					if(beforeAdviceRuleList != null) {
						execute(beforeAdviceRuleList);
					}
				}
				
				if(!activityEnded) {
					request();
				}

				// execute After Advice Action for Request Joinpoint
				if(requestAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> afterAdviceRuleList = requestAspectAdviceRuleRegistry.getAfterAdviceRuleList();
					if(afterAdviceRuleList != null) {
						execute(afterAdviceRuleList);
					}
				}
			} finally {
				if(requestAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> finallyAdviceRuleList = requestAspectAdviceRuleRegistry.getFinallyAdviceRuleList();
					if(finallyAdviceRuleList != null) {
						forceExecute(finallyAdviceRuleList);
					}
				}
			}
		} catch(RequestMethodNotAllowedException e) {
			throw e;
		} catch(Exception e) {
			setRaisedException(e);
			
			if(requestAspectAdviceRuleRegistry != null) {
				List<ExceptionRule> exceptionRuleList = requestAspectAdviceRuleRegistry.getExceptionRuleList();
				if(exceptionRuleList != null) {
					responseByContentType(exceptionRuleList);
					if(activityEnded) {
						return;
					}
				}
			}
				
			throw new RequestException("Failed to perform the activity in the request scope.", e);
		}
	}
	
	/**
	 * Perform activity for the content.
	 */
	private void performContent() {
		try {
			try {
				// execute Before Advice Action for Content Joinpoint
				if(contentAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> beforeAdviceRuleList = contentAspectAdviceRuleRegistry.getBeforeAdviceRuleList();
					if(beforeAdviceRuleList != null)
						execute(beforeAdviceRuleList);
				}
				
				if(!activityEnded) {
					process();
				}
				
				// execute After Advice Action for Content Joinpoint
				if(contentAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> afterAdviceRuleList = contentAspectAdviceRuleRegistry.getAfterAdviceRuleList();
					if(afterAdviceRuleList != null)
						execute(afterAdviceRuleList);
				}
			} finally {
				if(contentAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> finallyAdviceRuleList = contentAspectAdviceRuleRegistry.getFinallyAdviceRuleList();
					if(finallyAdviceRuleList != null)
						forceExecute(finallyAdviceRuleList);
				}
			}
		} catch(Exception e) {
			setRaisedException(e);
			
			if(contentAspectAdviceRuleRegistry != null) {
				List<ExceptionRule> exceptionRuleList = contentAspectAdviceRuleRegistry.getExceptionRuleList();
				if(exceptionRuleList != null) {
					responseByContentType(exceptionRuleList);
					if(activityEnded) {
						return;
					}
				}
			}
			
			throw new ProcessException("Failed to perform the activity in the content scope.", e);
		}
	}
	
	/**
	 * Perform activity for the response.
	 */
	private void performResponse() {
		try {
			try {
				// execute Before Advice Action for Request Joinpoint
				if(responseAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> beforeAdviceRuleList = responseAspectAdviceRuleRegistry.getBeforeAdviceRuleList();
					if(beforeAdviceRuleList != null) {
						execute(beforeAdviceRuleList);
					}
				}
				
				if(!activityEnded) {
					response();
				}
				
				// execute After Advice Action for Request Joinpoint
				if(responseAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> afterAdviceRuleList = responseAspectAdviceRuleRegistry.getAfterAdviceRuleList();
					if(afterAdviceRuleList != null) {
						execute(afterAdviceRuleList);
					}
				}
			} finally {
				if(responseAspectAdviceRuleRegistry != null) {
					List<AspectAdviceRule> finallyAdviceRuleList = responseAspectAdviceRuleRegistry.getFinallyAdviceRuleList();
					if(finallyAdviceRuleList != null) {
						forceExecute(finallyAdviceRuleList);
					}
				}
			}
		} catch(Exception e) {
			setRaisedException(e);
			
			if(responseAspectAdviceRuleRegistry != null) {
				List<ExceptionRule> exceptionRuleList = responseAspectAdviceRuleRegistry.getExceptionRuleList();
				if(exceptionRuleList != null) {
					responseByContentType(exceptionRuleList);
					if(activityEnded) {
						return;
					}
				}
			}
			
			throw new ResponseException("Failed to perform the activity in the response scope.", e);
		}
	}

	@Override
	public String determineRequestCharacterEncoding() {
		String characterEncoding = requestRule.getCharacterEncoding();

		if(characterEncoding == null)
			characterEncoding = getRequestSetting(RequestRule.CHARACTER_ENCODING_SETTING_NAME);

		return characterEncoding;
	}

	@Override
	public String determineResponseCharacterEncoding() {
		String characterEncoding = responseRule.getCharacterEncoding();

		if(characterEncoding == null)
			characterEncoding = getResponseSetting(ResponseRule.CHARACTER_ENCODING_SETTING_NAME);

		if(characterEncoding == null)
			characterEncoding = determineRequestCharacterEncoding();

		return characterEncoding;
	}

	protected void request() {
		parseDeclaredParameters();
		parseDeclaredAttributes();
	}

	/**
	 * Parse the declared parameters.
	 */
	protected void parseDeclaredParameters() {
		ItemRuleMap parameterItemRuleMap = getRequestRule().getParameterItemRuleMap();
		if(parameterItemRuleMap != null) {
			ItemEvaluator evaluator = new ItemExpressionParser(this);
			for(ItemRule itemRule : parameterItemRuleMap.values()) {
				String[] values = evaluator.evaluateAsStringArray(itemRule);
				String[] oldValues = getRequestAdapter().getParameterValues(itemRule.getName());
				if(values != oldValues) {
					getRequestAdapter().setParameter(itemRule.getName(), values);
				}
			}
		}
	}

	/**
	 * Parse the declared attributes.
	 */
	protected void parseDeclaredAttributes() {
		ItemRuleMap attributeItemRuleMap = getRequestRule().getAttributeItemRuleMap();
		if(attributeItemRuleMap != null) {
			ItemEvaluator evaluator = new ItemExpressionParser(this);
			for(ItemRule itemRule : attributeItemRuleMap.values()) {
				Object value = evaluator.evaluate(itemRule);
				getRequestAdapter().setAttribute(itemRule.getName(), value);
			}
		}
	}

	private void process() {
		ContentList contentList = transletRule.getContentList();

		if(contentList != null) {
			ProcessResult processResult = translet.touchProcessResult(contentList.getName(), contentList.size());

			if(transletRule.isExplicitContent()) {
				processResult.setOmittable(contentList.isOmittable());
			} else {
				if(contentList.getVisibleCount() < 2) {
					processResult.setOmittable(true);
				}
			}

			for(ActionList actionList : contentList) {
				execute(actionList);
				if(activityEnded)
					break;
			}
		}
	}

	@Override
	public ProcessResult getProcessResult() {
		return translet.getProcessResult();
	}

	@Override
	public Object getProcessResult(String actionId) {
		return translet.getProcessResult().getResultValue(actionId);
	}

	@Override
	public Response getResponse() {
		if(responseRule == null)
			return null;

		return responseRule.getResponse();
	}
	
	private void response() {
		Response res = getResponse();
		
		if(res != null)
			response(res);
		
		if(forwardTransletName != null)
			forward();
	}

	@Override
	public void response(Response response) {
		response.response(this);
		
		if(response.getResponseType() == ResponseType.FORWARD) {
			ForwardResponse forwardResponse = (ForwardResponse)response;
			this.forwardTransletName = forwardResponse.getForwardResponseRule().getTransletName();
		}
		
		activityEnd();
	}
	
	/**
	 * Forwards to other translet.
	 */
	private void forward() {
		if(debugEnabled) {
			log.debug("Forward to translet " + forwardTransletName);
		}
		
		activityEnded = false;
		
		prepare(forwardTransletName, requestMethod, translet.getProcessResult());
			
		perform();
	}

	@Override
	public void responseByContentType(List<ExceptionRule> exceptionRuleList) {
		for(ExceptionRule exceptionRule : exceptionRuleList) {
			responseByContentType(exceptionRule);
			if(activityEnded)
				return;
		}
	}

	private void responseByContentType(ExceptionRule exceptionRule) {
		ResponseByContentTypeRule rbctr = exceptionRule.getResponseByContentTypeRule(getRaisedException());
		if(rbctr != null) {
			log.info("Raised exception: " + getRaisedException());
			responseByContentType(rbctr);
		}
	}

	private void responseByContentType(ResponseByContentTypeRule responseByContentTypeRule) {
		Response response = getResponse();
		Response response2;

		if(response != null && response.getContentType() != null)
			response2 = responseByContentTypeRule.getResponse(response.getContentType());
		else
			response2 = responseByContentTypeRule.getDefaultResponse();

		if(response2 != null) {
			responseRule = responseRule.newUrgentResponseRule(response2);
			
			log.info("Response by Content-Type " + responseRule);

			// Clear process results. No reflection to ProcessResult.
			translet.setProcessResult(null);
			translet.touchProcessResult(null, 0).setOmittable(true);
			
			if(responseRule.getResponse() != null) {
				ActionList actionList = responseRule.getResponse().getActionList();
				if(actionList != null) {
					execute(actionList);
				}
				response();
			}
		}
	}

	@Override
	public String getForwardTransletName() {
		return forwardTransletName;
	}

	protected void execute(ActionList actionList) {
		ContentResult contentResult = null;

		if(translet.getProcessResult() != null) {
			contentResult = new ContentResult(translet.getProcessResult(), actionList.size());
			contentResult.setName(actionList.getName());
			if(transletRule.isExplicitContent()) {
				contentResult.setOmittable(actionList.isOmittable());
			} else if(actionList.getName() == null && actionList.getVisibleCount() < 2) {
				contentResult.setOmittable(true);
			}
		}

		for(Executable action : actionList) {
			execute(action, contentResult);
			if(activityEnded)
				break;
		}
	}
	
	private void execute(Executable action, ContentResult contentResult) {
		if(debugEnabled)
			log.debug("action " + action);
		
		try {
			Object resultValue = action.execute(this);
		
			if(contentResult != null && resultValue != ActionResult.NO_RESULT) {
				ActionResult actionResult = new ActionResult(contentResult);
				actionResult.setActionId(action.getActionId());
				actionResult.setResultValue(resultValue);
				actionResult.setHidden(action.isHidden());
			}
			
			if(traceEnabled)
				log.trace("actionResult " + resultValue);
		} catch(Exception e) {
			setRaisedException(e);
			throw new ActionExecutionException("Failed to execute action " + action, e);
		}
	}

	@Override
	public void execute(List<AspectAdviceRule> aspectAdviceRuleList) {
		for(AspectAdviceRule aspectAdviceRule : aspectAdviceRuleList) {
			execute(aspectAdviceRule, false);
		}
	}

	@Override
	public void forceExecute(List<AspectAdviceRule> aspectAdviceRuleList) {
		for(AspectAdviceRule aspectAdviceRule : aspectAdviceRuleList) {
			execute(aspectAdviceRule, true);
		}
	}
	
	private void execute(AspectAdviceRule aspectAdviceRule, boolean force) {
		try {
			Executable action = aspectAdviceRule.getExecutableAction();
			
			if(action == null) {
				throw new IllegalArgumentException("No specified action on AspectAdviceRule " + aspectAdviceRule);
			}
			
			if(action.getActionType() == ActionType.BEAN && aspectAdviceRule.getAdviceBeanId() != null) {
				Object adviceBean = translet.getAspectAdviceBean(aspectAdviceRule.getAspectId());
				if(adviceBean == null) {
					if(aspectAdviceRule.getAdviceBeanClass() != null) {
						adviceBean = getBean(aspectAdviceRule.getAdviceBeanClass());
					} else {
						adviceBean = getBean(aspectAdviceRule.getAdviceBeanId());
					}
					translet.putAspectAdviceBean(aspectAdviceRule.getAspectId(), adviceBean);
				}
			}
			
			Object adviceActionResult = action.execute(this);
			
			if(adviceActionResult != null && adviceActionResult != ActionResult.NO_RESULT) {
				translet.putAdviceResult(aspectAdviceRule, adviceActionResult);
			}
			
			if(traceEnabled) {
				log.trace("adviceActionResult " + adviceActionResult);
			}
		} catch(Exception e) {
			setRaisedException(e);
			
			if(!force) {
				throw new ActionExecutionException("Failed to execute the advice action " + aspectAdviceRule, e);
			} else {
				log.error("Failed to execute the advice action " + aspectAdviceRule, e);
			}
		}
	}

	private void prepareRule(TransletRule transletRule) {
		this.transletRule = transletRule;
		this.requestRule = transletRule.getRequestRule();
		this.responseRule = transletRule.getResponseRule();
		
		if(transletRule.getNameTokens() == null) {
			this.transletAspectAdviceRuleRegistry = transletRule.replicateAspectAdviceRuleRegistry();
			this.requestAspectAdviceRuleRegistry = requestRule.replicateAspectAdviceRuleRegistry();
			this.responseAspectAdviceRuleRegistry = responseRule.replicateAspectAdviceRuleRegistry();
			if(transletRule.getContentList() != null) {
				this.contentAspectAdviceRuleRegistry = transletRule.getContentList().replicateAspectAdviceRuleRegistry();
			}
		} else {
			AspectAdviceRulePostRegister transletAARPostRegister = new AspectAdviceRulePostRegister();
			AspectAdviceRulePostRegister requestAARPostRegister = new AspectAdviceRulePostRegister();
			AspectAdviceRulePostRegister contentAARPostRegister = new AspectAdviceRulePostRegister();
			AspectAdviceRulePostRegister responseAARPostRegister = new AspectAdviceRulePostRegister();

			for(AspectRule aspectRule : getAspectRuleRegistry().getAspectRuleMap().values()) {
				AspectTargetType aspectTargetType = aspectRule.getAspectTargetType();

				if(aspectTargetType == AspectTargetType.TRANSLET) {
					JoinpointScopeType joinpointScope = aspectRule.getJoinpointScope();

					if(!aspectRule.isBeanRelevanted() && joinpointScope != JoinpointScopeType.SESSION) {
						if(requestMethod == null ||
								aspectRule.getAllowedMethods() == null ||
								requestMethod.containsTo(aspectRule.getAllowedMethods())) {
							Pointcut pointcut = aspectRule.getPointcut();
							if(pointcut == null || pointcut.matches(transletRule.getName())) {
								if(debugEnabled)
									log.debug("register AspectRule " + aspectRule);
	
								if(joinpointScope == JoinpointScopeType.REQUEST) {
									requestAARPostRegister.register(aspectRule);
								} else if(joinpointScope == JoinpointScopeType.CONTENT) {
									contentAARPostRegister.register(aspectRule);
								} else if(joinpointScope == JoinpointScopeType.RESPONSE) {
									responseAARPostRegister.register(aspectRule);
								} else {
									transletAARPostRegister.register(aspectRule);
								}
							}
						}
					}
				}
			}

			this.transletAspectAdviceRuleRegistry = transletAARPostRegister.getAspectAdviceRuleRegistry();
			this.requestAspectAdviceRuleRegistry = requestAARPostRegister.getAspectAdviceRuleRegistry();
			this.contentAspectAdviceRuleRegistry = contentAARPostRegister.getAspectAdviceRuleRegistry();
			this.responseAspectAdviceRuleRegistry = responseAARPostRegister.getAspectAdviceRuleRegistry();
		}
	}

	@Override
	public void registerAspectRule(AspectRule aspectRule) {
		if(requestMethod != null &&
				aspectRule.getAllowedMethods() != null &&
				!requestMethod.containsTo(aspectRule.getAllowedMethods()))
			return;
		
		JoinpointScopeType joinpointScope = aspectRule.getJoinpointScope();

		/*
		 * before-advice is excluded because it is already processed.
		 */
		if(joinpointScope == JoinpointScopeType.TRANSLET || joinpointScope == currentJoinpointScope) {
			if(debugEnabled)
				log.debug("register AspectRule " + aspectRule);
			
			if(JoinpointScopeType.TRANSLET == joinpointScope) {
				if(transletAspectAdviceRuleRegistry == null) {
					transletAspectAdviceRuleRegistry = new AspectAdviceRuleRegistry();
				}
				AspectAdviceRuleRegister.register(transletAspectAdviceRuleRegistry, aspectRule, AspectAdviceType.BEFORE);
			} else if(JoinpointScopeType.REQUEST == joinpointScope) {
				if(requestAspectAdviceRuleRegistry == null) {
					requestAspectAdviceRuleRegistry = new AspectAdviceRuleRegistry();
				}
				AspectAdviceRuleRegister.register(requestAspectAdviceRuleRegistry, aspectRule, AspectAdviceType.BEFORE);
			} else if(JoinpointScopeType.RESPONSE == joinpointScope) {
				if(responseAspectAdviceRuleRegistry == null) {
					responseAspectAdviceRuleRegistry = new AspectAdviceRuleRegistry();
				}
				AspectAdviceRuleRegister.register(responseAspectAdviceRuleRegistry, aspectRule, AspectAdviceType.BEFORE);
			} else if(JoinpointScopeType.CONTENT == joinpointScope) {
				if(contentAspectAdviceRuleRegistry == null) {
					contentAspectAdviceRuleRegistry = new AspectAdviceRuleRegistry();
				}
				AspectAdviceRuleRegister.register(contentAspectAdviceRuleRegistry, aspectRule, AspectAdviceType.BEFORE);
			}

			List<AspectAdviceRule> aspectAdviceRuleList = aspectRule.getAspectAdviceRuleList();

			if(aspectAdviceRuleList != null) {
				for(AspectAdviceRule aspectAdviceRule : aspectAdviceRuleList) {
					if(aspectAdviceRule.getAspectAdviceType() == AspectAdviceType.BEFORE) {
						execute(aspectAdviceRule, false);
					}
				}
			}
		}
	}

	@Override
	public <T> T getAspectAdviceBean(String aspectId) {
		return translet.getAspectAdviceBean(aspectId);
	}

	@Override
	public boolean isExceptionRaised() {
		return (raisedException != null);
	}

	@Override
	public Throwable getRaisedException() {
		return raisedException;
	}

	@Override
	public Throwable getOriginRaisedException() {
		Throwable t = raisedException;
		while(t != null) {
			t = t.getCause();
		}
		return t;
	}

	@Override
	public void setRaisedException(Throwable raisedException) {
		if(this.raisedException == null) {
			if(debugEnabled) {
				log.error("Raised exception: ", raisedException);
			}
			this.raisedException = raisedException;
		}
	}

	@Override
	public void activityEnd() {
		activityEnded = true;
	}

	@Override
	public boolean isActivityEnded() {
		return activityEnded;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends Activity> T newActivity() {
		Activity activity = new CoreActivity(getActivityContext());
		return (T)activity;
	}
	
	/**
	 * Create a new {@code Translet}.
	 */
	protected void newTranslet() {
		translet = newTranslet(this);
	}

	@Override
	public Translet getTranslet() {
		return translet;
	}

	@Override
	public String getTransletName() {
		return transletName;
	}

	@Override
	public MethodType getRequestMethod() {
		return requestMethod;
	}

	protected TransletRule getTransletRule() {
		return transletRule;
	}

	protected RequestRule getRequestRule() {
		return requestRule;
	}

	protected ResponseRule getResponseRule() {
		return responseRule;
	}

	@Override
	public <T> T getTransletSetting(String settingName) {
		return getSetting(transletAspectAdviceRuleRegistry, settingName);
	}

	@Override
	public <T> T getRequestSetting(String settingName) {
		return getSetting(requestAspectAdviceRuleRegistry, settingName);
	}

	@Override
	public <T> T getResponseSetting(String settingName) {
		return getSetting(responseAspectAdviceRuleRegistry, settingName);
	}

	private <T> T getSetting(AspectAdviceRuleRegistry aspectAdviceRuleRegistry, String settingName) {
		return (aspectAdviceRuleRegistry != null) ? aspectAdviceRuleRegistry.getSetting(settingName) : null;
	}

}
