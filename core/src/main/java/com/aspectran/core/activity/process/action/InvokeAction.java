/*
 * Copyright (c) 2008-2019 The Aspectran Project
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

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpression;
import com.aspectran.core.context.rule.InvokeActionRule;
import com.aspectran.core.context.rule.ItemRule;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.context.rule.ItemRuleUtils;
import com.aspectran.core.context.rule.type.ActionType;
import com.aspectran.core.util.BeanUtils;
import com.aspectran.core.util.MethodUtils;
import com.aspectran.core.util.ToStringBuilder;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * {@code InvokeAction} that invokes a method of the bean instance.
 *
 * <p>Created: 2008. 03. 22 PM 5:50:35</p>
 */
public class InvokeAction implements Executable {

    private static final Log log = LogFactory.getLog(InvokeAction.class);

    private final InvokeActionRule invokeActionRule;

    private Boolean requiresTranslet;

    /**
     * Instantiates a new InvokeAction.
     *
     * @param invokeActionRule the invoke action rule
     */
    public InvokeAction(InvokeActionRule invokeActionRule) {
        this.invokeActionRule = invokeActionRule;
    }

    @Override
    public Object execute(Activity activity) throws Exception {
        Object bean = null;
        if (invokeActionRule.getBeanClass() != null) {
            bean = activity.getBean(invokeActionRule.getBeanClass());
        } else if (invokeActionRule.getBeanId() != null) {
            bean = activity.getBean(invokeActionRule.getBeanId());
        }
        if (bean == null) {
            throw new ActionExecutionException("No bean found for " + invokeActionRule);
        }
        return execute(activity, bean);
    }

    protected Object execute(Activity activity, Object bean) throws Exception {
        try {
            ItemRuleMap propertyItemRuleMap = invokeActionRule.getPropertyItemRuleMap();
            ItemRuleMap argumentItemRuleMap = invokeActionRule.getArgumentItemRuleMap();
            ItemEvaluator evaluator = null;
            if ((propertyItemRuleMap != null && !propertyItemRuleMap.isEmpty()) ||
                    (argumentItemRuleMap != null && !argumentItemRuleMap.isEmpty())) {
                evaluator = new ItemExpression(activity);
            }
            if (propertyItemRuleMap != null && !propertyItemRuleMap.isEmpty()) {
                Map<String, Object> valueMap = evaluator.evaluate(propertyItemRuleMap);
                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                    BeanUtils.setProperty(bean, entry.getKey(), entry.getValue());
                }
            }

            Method method = invokeActionRule.getMethod();
            if (method != null) {
                if (argumentItemRuleMap != null && !argumentItemRuleMap.isEmpty()) {
                    Object[] args = createArguments(activity, argumentItemRuleMap, evaluator, invokeActionRule.isRequiresTranslet());
                    return invokeMethod(bean, method, args);
                } else {
                    return invokeMethod(activity, bean, method, invokeActionRule.isRequiresTranslet());
                }
            } else {
                String methodName = invokeActionRule.getMethodName();
                Object result;
                if (activity.getTranslet() != null) {
                    if (requiresTranslet == null) {
                        try {
                            result = invokeMethod(activity, bean, methodName, argumentItemRuleMap, evaluator, true);
                            requiresTranslet = Boolean.TRUE;
                        } catch (NoSuchMethodException e) {
                            if (log.isTraceEnabled()) {
                                log.trace("No have a Translet argument on bean method " + invokeActionRule);
                            }
                            requiresTranslet = Boolean.FALSE;
                            result = invokeMethod(activity, bean, methodName, argumentItemRuleMap, evaluator, false);
                        }
                    } else {
                        result = invokeMethod(activity, bean, methodName, argumentItemRuleMap, evaluator, requiresTranslet);
                    }
                } else {
                    result = invokeMethod(activity, bean, methodName, argumentItemRuleMap, evaluator, false);
                }
                return result;
            }
        } catch (ActionExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new ActionExecutionException("Failed to execute action " + this, e);
        }
    }

    /**
     * Returns the invoke action rule.
     *
     * @return the invoke action rule
     */
    public InvokeActionRule getInvokeActionRule() {
        return invokeActionRule;
    }

    @Override
    public String getActionId() {
        return invokeActionRule.getActionId();
    }

    @Override
    public boolean isHidden() {
        return invokeActionRule.isHidden();
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ACTION;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getActionRule() {
        return (T)invokeActionRule;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("invoke", invokeActionRule);
        return tsb.toString();
    }

    private static Object invokeMethod(Activity activity, Object bean, Method method, boolean requiresTranslet)
            throws Exception {
        Object[] args;
        if (requiresTranslet) {
            args = new Object[] { activity.getTranslet() };
        } else {
            args = MethodUtils.EMPTY_OBJECT_ARRAY;
        }
        return invokeMethod(bean, method, args);
    }

    private static Object invokeMethod(Object bean, Method method, Object[] args) throws Exception {
        try {
            return method.invoke(bean, args);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw (Exception)e.getCause();
            } else {
                throw e;
            }
        }
    }

    private static Object invokeMethod(Activity activity, Object bean, String methodName,
                                       ItemRuleMap argumentItemRuleMap, ItemEvaluator evaluator,
                                       boolean requiresTranslet)
            throws Exception {
        Class<?>[] argsTypes = null;
        Object[] argsObjects = null;

        if (argumentItemRuleMap != null && !argumentItemRuleMap.isEmpty()) {
            if (evaluator == null) {
                evaluator = new ItemExpression(activity);
            }

            Map<String, Object> valueMap = evaluator.evaluate(argumentItemRuleMap);
            int argSize = argumentItemRuleMap.size();
            int argIndex;

            if (requiresTranslet) {
                argIndex = 1;
                argsTypes = new Class<?>[argSize + argIndex];
                argsObjects = new Object[argsTypes.length];
                argsTypes[0] = Translet.class;
                argsObjects[0] = activity.getTranslet();
            } else {
                argIndex = 0;
                argsTypes = new Class<?>[argSize];
                argsObjects = new Object[argsTypes.length];
            }

            for (ItemRule ir : argumentItemRuleMap.values()) {
                Object o = valueMap.get(ir.getName());
                argsTypes[argIndex] = ItemRuleUtils.getPrototypeClass(ir, o);
                argsObjects[argIndex] = o;
                argIndex++;
            }
        } else if (requiresTranslet) {
            argsTypes = new Class<?>[] { Translet.class };
            argsObjects = new Object[] { activity.getTranslet() };
        }

        try {
            return MethodUtils.invokeMethod(bean, methodName, argsObjects, argsTypes);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw (Exception)e.getCause();
            } else {
                throw e;
            }
        }
    }

    private static Object[] createArguments(Activity activity, ItemRuleMap argumentItemRuleMap,
                                            ItemEvaluator evaluator, boolean requiresTranslet) {
        if (evaluator == null) {
            evaluator = new ItemExpression(activity);
        }

        Object[] args;
        int size = argumentItemRuleMap.size();
        int index;
        if (requiresTranslet) {
            index = 1;
            args = new Object[size + index];
            args[0] = activity.getTranslet();
        } else {
            index = 0;
            args = new Object[size];
        }

        Map<String, Object> valueMap = evaluator.evaluate(argumentItemRuleMap);
        for (String name : argumentItemRuleMap.keySet()) {
            Object o = valueMap.get(name);
            args[index] = o;
            index++;
        }

        return args;
    }

}
