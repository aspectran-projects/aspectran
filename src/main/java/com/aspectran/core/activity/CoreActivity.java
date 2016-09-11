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

import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.process.ContentList;
import com.aspectran.core.activity.process.action.ActionExecutionException;
import com.aspectran.core.activity.process.action.Executable;
import com.aspectran.core.activity.process.result.ActionResult;
import com.aspectran.core.activity.process.result.ContentResult;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.request.MissingMandatoryParametersException;
import com.aspectran.core.activity.response.ForwardResponse;
import com.aspectran.core.activity.response.Response;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.bean.scope.Scope;
import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpressionParser;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.i18n.LocaleResolver;
import com.aspectran.core.context.rule.ExceptionCatchRule;
import com.aspectran.core.context.rule.ExceptionRule;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleList;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.RequestRule;
import com.aspectran.core.context.rule.ResponseRule;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.context.rule.type.MethodType;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.context.translet.TransletNotFoundException;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class CoreActivity.
 *
 * <p>This class is generally not thread-safe. It is primarily designed for use in a single thread only.
 *
 * <p>Created: 2008. 03. 22 PM 5:48:09</p>
 */
public class CoreActivity extends BasicActivity {

	private static final Log log = LogFactory.getLog(CoreActivity.class);
	
	private MethodType requestMethod;

	private String transletName;
	
	private String forwardTransletName;

	private boolean withoutResponse;

	private Translet translet;

	private Response reservedResponse;

	/**
	 * Instantiates a new CoreActivity.
	 *
	 * @param context the activity context
	 */
	public CoreActivity(ActivityContext context) {
		super(context);
	}
	
	@Override
	public void prepare(String transletName) {
		this.transletName = transletName;
		this.requestMethod = MethodType.GET;

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
		this.requestMethod = (requestMethod == null) ? MethodType.GET : requestMethod;

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
			if(log.isDebugEnabled()) {
				log.debug("translet " + transletRule);
			}

			if(transletRule.getTransletInterfaceClass() != null) {
				setTransletInterfaceClass(transletRule.getTransletInterfaceClass());
			}
			if(transletRule.getTransletImplementationClass() != null) {
				setTransletImplementationClass(transletRule.getTransletImplementationClass());
			}

			translet = newTranslet(this, transletRule);

			if(processResult != null) {
				translet.setProcessResult(processResult);
			}

			if(forwardTransletName == null) {
				if(isIncluded()) {
					backupCurrentActivity();
				} else {
					setCurrentActivity(this);
				}
				adapt();
			}
			
			prepareAspectAdviceRule(transletRule);
			parseRequest();
			
			if(forwardTransletName == null) {
				resolveLocale();
			}
		} catch(Exception e) {
			throw new ActivityException("Failed to prepare activity.", e);
		}
	}

	protected void adapt() throws AdapterException {
	}
	
	/**
	 * Resolve the current locale.
	 * 
	 * @return the current locale
	 */
	protected LocaleResolver resolveLocale() {
		LocaleResolver localeResolver = null;
		String localeResolverBeanId = getSetting(RequestRule.LOCALE_RESOLVER_SETTING_NAME);
		if(localeResolverBeanId != null) {
			localeResolver = getBean(localeResolverBeanId, LocaleResolver.class);
			localeResolver.resolveLocale(getTranslet());
			localeResolver.resolveTimeZone(getTranslet());
		}
		return localeResolver;
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
		removeCurrentActivity();
	}

	/**
	 * Parses the declared parameters and attributes.
	 */
	protected void parseRequest() {
		parseDeclaredParameters();
		parseDeclaredAttributes();
	}

	/**
	 * Parses the declared parameters.
	 */
	protected void parseDeclaredParameters() {
		ItemRuleMap parameterItemRuleMap = getRequestRule().getParameterItemRuleMap();
		if(parameterItemRuleMap != null) {
			ItemEvaluator evaluator = null;
			ItemRuleList missingItemRules = null;
			for(ItemRule itemRule : parameterItemRuleMap.values()) {
				Token[] tokens = itemRule.getTokens();
				if(tokens != null) {
					if(evaluator == null) {
						evaluator = new ItemExpressionParser(this);
					}
					String[] values = evaluator.evaluateAsStringArray(itemRule);
					String[] oldValues = getRequestAdapter().getParameterValues(itemRule.getName());
					if(values != oldValues) {
						getRequestAdapter().setParameter(itemRule.getName(), values);
					}
				}

				if(itemRule.isMandatory()) {
					String[] values = getRequestAdapter().getParameterValues(itemRule.getName());
					if(values == null) {
						if(missingItemRules == null) {
							missingItemRules = new ItemRuleList();
						}
						missingItemRules.add(itemRule);
					}
				}
			}
			if(missingItemRules != null) {
				throw new MissingMandatoryParametersException(missingItemRules);
			}
		}
	}

	/**
	 * Parses the declared attributes.
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
	
	/**
	 * Performs an activity.
	 */
	private void performTranslet() {
		try {
			try {
				// execute the Before Advice Action for Translet Joinpoint
				if(getBeforeAdviceRuleList() != null) {
					execute(getBeforeAdviceRuleList());
				}

				if(!isResponseReserved()) {
					if(getTransletRule().getContentList() != null) {
						produce();
					}
				}

				// execute the After Advice Action for Translet Joinpoint
				if(getAfterAdviceRuleList() != null) {
					execute(getAfterAdviceRuleList());
				}
			} catch(Exception e) {
				setRaisedException(e);
			} finally {
				if(getFinallyAdviceRuleList() != null) {
					executeWithoutThrow(getFinallyAdviceRuleList());
				}
			}

			if(isExceptionRaised()) {
				reserveResponse(null);

				if(getTransletRule().getExceptionRule() != null) {
					exceptionHandling(getTransletRule().getExceptionRule());
				}
				if(!isResponseReserved() && getExceptionRuleList() != null) {
					exceptionHandling(getExceptionRuleList());
				}
			}

			if(!withoutResponse) {
				response();
			}
		} catch(Exception e) {
			throw new ActivityException("Failed to perform an activity.", e);
		} finally {
			Scope requestScope = getRequestAdapter().getRequestScope(false);
			if(requestScope != null) {
				requestScope.destroy();
			}
		}
	}

	/**
	 * Produces content.
	 */
	private void produce() {
		ContentList contentList = getTransletRule().getContentList();

		if(contentList != null) {
			ProcessResult processResult = translet.touchProcessResult(contentList.getName(), contentList.size());

			if(getTransletRule().isExplicitContent()) {
				processResult.setOmittable(contentList.isOmittable());
			} else {
				if(contentList.getVisibleCount() < 2) {
					processResult.setOmittable(true);
				}
			}

			for(ActionList actionList : contentList) {
				execute(actionList);
				if(isResponseReserved())
					break;
			}
		}
	}

	protected Response getDeclaredResponse() {
		return (getResponseRule() != null) ? getResponseRule().getResponse() : null;
	}
	
	private void response() {
		Response res = (this.reservedResponse != null) ? this.reservedResponse : getDeclaredResponse();
		
		if(res != null) {
			if(res.getResponseType() != ResponseType.FORWARD) {
				getResponseAdapter().flush();
			}

			res.response(this);

			if(res.getResponseType() == ResponseType.FORWARD) {
				ForwardResponse forwardResponse = (ForwardResponse)res;
				this.forwardTransletName = forwardResponse.getForwardResponseRule().getTransletName();
			} else {
				this.forwardTransletName = null;
			}
			
			if(forwardTransletName != null) {
				forward();
			}
		}
	}

	/**
	 * Responds immediately, and the remaining jobs will be canceled.
	 *
	 * @param response the response
	 */
	protected void reserveResponse(Response response) {
		this.reservedResponse = response;
	}

	protected void reserveResponse() {
		if(this.reservedResponse != null) {
			this.reservedResponse = getDeclaredResponse();
		}
	}

	@Override
	public boolean isResponseReserved() {
		return (this.reservedResponse != null);
	}
	
	/**
	 * Forwarding from current translet to other translet.
	 */
	private void forward() {
		if(log.isDebugEnabled()) {
			log.debug("Forwarding from [" + transletName + "] to [" + forwardTransletName + "]");
		}

		reserveResponse(null);
		
		prepare(forwardTransletName, requestMethod, translet.getProcessResult());
		perform();
	}

	@Override
	public void exceptionHandling(ExceptionRule exceptionRule) {
		super.exceptionHandling(exceptionRule);
		if(!isResponseReserved() && translet != null) {
			ExceptionCatchRule exceptionCatchRule = exceptionRule.getExceptionCatchRule(getRaisedException());
			if(exceptionCatchRule != null) {
				responseByContentType(exceptionCatchRule);
			}
		}
	}

	private void responseByContentType(ExceptionCatchRule exceptionCatchRule) {
		Response response = getDeclaredResponse();
		Response targetResponse;

		if(response != null && response.getContentType() != null)
			targetResponse = exceptionCatchRule.getResponse(response.getContentType());
		else
			targetResponse = exceptionCatchRule.getDefaultResponse();

		if(targetResponse != null) {
			ResponseRule responseRule = new ResponseRule();
			responseRule.setResponse(targetResponse);
			if(getResponseRule() != null) {
				responseRule.setCharacterEncoding(getResponseRule().getCharacterEncoding());
			}

			setResponseRule(responseRule);

			if(log.isDebugEnabled()) {
				log.debug("Response by Content Type " + responseRule);
			}

			// Clear produced results. No reflection to ProcessResult.
			translet.setProcessResult(null);
			translet.touchProcessResult(null, 0).setOmittable(true);
			
			ActionList actionList = targetResponse.getActionList();
			if(actionList != null) {
				execute(actionList);
			}

			reserveResponse(targetResponse);
		}
	}

	/**
	 * Execute actions.
	 *
	 * @param actionList the action list
	 */
	protected void execute(ActionList actionList) {
		ContentResult contentResult = null;

		if(translet.getProcessResult() != null) {
			contentResult = new ContentResult(translet.getProcessResult(), actionList.size());
			contentResult.setName(actionList.getName());
			if(getTransletRule().isExplicitContent()) {
				contentResult.setOmittable(actionList.isOmittable());
			} else if(actionList.getName() == null && actionList.getVisibleCount() < 2) {
				contentResult.setOmittable(true);
			}
		}

		for(Executable action : actionList) {
			execute(action, contentResult);
			if(isResponseReserved())
				break;
		}
	}

	/**
	 * Execute action.
	 *
	 * @param action the executable action
	 * @param contentResult the content result
	 */
	private void execute(Executable action, ContentResult contentResult) {
		if(log.isDebugEnabled())
			log.debug("action " + action);
		
		try {
			Object resultValue = action.execute(this);
		
			if(contentResult != null && resultValue != ActionResult.NO_RESULT) {
				ActionResult actionResult = new ActionResult(contentResult);
				actionResult.setActionId(action.getActionId());
				actionResult.setResultValue(resultValue);
				actionResult.setHidden(action.isHidden());
			}
			
			if(log.isTraceEnabled()) {
				log.trace("actionResult " + resultValue);
			}
		} catch(Exception e) {
			setRaisedException(e);
			throw new ActionExecutionException("Failed to execute action " + action, e);
		}
	}

	@Override
	public String getTransletName() {
		return transletName;
	}

	@Override
	public MethodType getRequestMethod() {
		return requestMethod;
	}

	@Override
	public Translet getTranslet() {
		return translet;
	}

	@Override
	public ProcessResult getProcessResult() {
		return translet.getProcessResult();
	}

	@Override
	public Object getProcessResult(String actionId) {
		return translet.getProcessResult().getResultValue(actionId);
	}

}
