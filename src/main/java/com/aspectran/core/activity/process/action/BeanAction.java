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
package com.aspectran.core.activity.process.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.context.expr.ItemExpression;
import com.aspectran.core.context.expr.ItemExpressor;
import com.aspectran.core.context.rule.AspectAdviceRule;
import com.aspectran.core.context.rule.BeanActionRule;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.type.ActionType;
import com.aspectran.core.util.BeanUtils;
import com.aspectran.core.util.MethodUtils;
import com.aspectran.core.util.ToStringBuilder;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class BeanAction.
 * 
 * <p>Created: 2008. 03. 22 PM 5:50:35</p>
 */
public class BeanAction extends AbstractAction {

	private static final Log log = LogFactory.getLog(BeanAction.class);

	private final BeanActionRule beanActionRule;
	
	private final String beanId;

	private final Class<?> beanClass;

	private final ItemRuleMap propertyItemRuleMap;
	
	private final ItemRuleMap argumentItemRuleMap;
	
	private final AspectAdviceRule aspectAdviceRule;
	
	/**
	 * Instantiates a new BeanAction.
	 *
	 * @param beanActionRule the bean action rule
	 * @param parent the parent
	 */
	public BeanAction(BeanActionRule beanActionRule, ActionList parent) {
		super(parent);

		this.beanActionRule = beanActionRule;
		this.beanId = beanActionRule.getBeanId();
		this.beanClass = beanActionRule.getBeanClass();

		if(beanActionRule.getPropertyItemRuleMap() != null && !beanActionRule.getPropertyItemRuleMap().isEmpty())
			this.propertyItemRuleMap = beanActionRule.getPropertyItemRuleMap();
		else
			this.propertyItemRuleMap = null;

		if(beanActionRule.getArgumentItemRuleMap() != null && !beanActionRule.getArgumentItemRuleMap().isEmpty())
			this.argumentItemRuleMap = beanActionRule.getArgumentItemRuleMap();
		else
			this.argumentItemRuleMap = null;

		if(beanActionRule.getAspectAdviceRule() != null)
			this.aspectAdviceRule = beanActionRule.getAspectAdviceRule();
		else
			this.aspectAdviceRule = null;
	}

	@Override
	public Object execute(Activity activity) throws Exception {
		try {
			Object bean = null;
			if(beanClass != null)
				bean = activity.getBean(beanClass);
			else if(beanId != null)
				bean = activity.getBean(beanId);
			else if(aspectAdviceRule != null)
				bean = activity.getAspectAdviceBean(aspectAdviceRule.getAspectId());

			ItemExpressor expressor = null;
			
			if(propertyItemRuleMap != null || argumentItemRuleMap != null)
				expressor = new ItemExpression(activity);
			
			if(propertyItemRuleMap != null) {
				Map<String, Object> valueMap = expressor.express(propertyItemRuleMap);
				
				// set properties for ActionBean
				for(Map.Entry<String, Object> entry : valueMap.entrySet()) {
					BeanUtils.setObject(bean, entry.getKey(), entry.getValue());
				}
			}

			return invokeMethod(activity, bean, beanActionRule.getMethod(), argumentItemRuleMap, expressor, beanActionRule.isRequiresTranslet());
		} catch(Exception e) {
			log.error("Action execution error: beanActionRule " + beanActionRule + " Cause: " + e.toString());
			throw e;
		}
	}

	public static Object invokeMethod(Activity activity, Object bean, Method method, ItemRuleMap argumentItemRuleMap, ItemExpressor expressor, boolean requiresTranslet)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Object[] argsObjects = null;

		if(argumentItemRuleMap != null) {
			Map<String, Object> valueMap = expressor.express(argumentItemRuleMap);

			int argSize = argumentItemRuleMap.size();
			int argIndex;

			if(requiresTranslet) {
				argIndex = 1;
				argsObjects = new Object[argSize + argIndex];
				argsObjects[0] = activity.getTranslet();
			} else {
				argIndex = 0;
				argsObjects = new Object[argSize];
			}

			for(ItemRule ir : argumentItemRuleMap) {
				Object o = valueMap.get(ir.getName());
				argsObjects[argIndex] = o;
				argIndex++;
			}
		} else if(requiresTranslet) {
			argsObjects = new Object[] { activity.getTranslet() };
		}

		return method.invoke(bean, argsObjects);
	}

	public static Object invokeMethod(Activity activity, Object bean, String methodName, ItemRuleMap argumentItemRuleMap, ItemExpressor expressor, boolean requiresTranslet)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Class<?>[] argsTypes = null;
		Object[] argsObjects = null;
		
		if(argumentItemRuleMap != null) {
			Map<String, Object> valueMap = expressor.express(argumentItemRuleMap);

			int argSize = argumentItemRuleMap.size();
			int argIndex;
			
			if(requiresTranslet) {
				argIndex = 1;
				argsTypes = new Class<?>[argSize + argIndex];
				argsObjects = new Object[argsTypes.length];
				argsTypes[0] = activity.getTransletInterfaceClass();
				argsObjects[0] = activity.getTranslet();
			} else {
				argIndex = 0;
				argsTypes = new Class<?>[argSize];
				argsObjects = new Object[argsTypes.length];
			}

			for(ItemRule ir : argumentItemRuleMap) {
				Object o = valueMap.get(ir.getName());

				argsTypes[argIndex] = ItemRule.getClassOfValue(ir, o);
				argsObjects[argIndex] = o;

				argIndex++;
			}
		} else if(requiresTranslet) {
			argsTypes = new Class<?>[] { activity.getTransletInterfaceClass() };
			argsObjects = new Object[] { activity.getTranslet() };
		}
		
		return MethodUtils.invokeMethod(bean, methodName, argsObjects, argsTypes);
	}
	
	/**
	 * Gets the bean action rule.
	 * 
	 * @return the bean action rule
	 */
	public BeanActionRule getBeanActionRule() {
		return beanActionRule;
	}

	@Override
	public ActionList getParent() {
		return parent;
	}

	@Override
	public String getActionId() {
		return beanActionRule.getActionId();
	}

	@Override
	public boolean isHidden() {
		return beanActionRule.isHidden();
	}

	@Override
	public ActionType getActionType() {
		return ActionType.BEAN;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getActionRule() {
		return (T)beanActionRule;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder();
		tsb.append("actionType", getActionType());
		tsb.append("beanActionRule", beanActionRule);
		return tsb.toString();
	}
	
}
