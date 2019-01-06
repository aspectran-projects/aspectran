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
package com.aspectran.daemon.command.builtins;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.activity.request.parameter.ParameterMap;
import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpression;
import com.aspectran.core.context.rule.IllegalRuleException;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.daemon.command.AbstractCommand;
import com.aspectran.daemon.command.CommandRegistry;
import com.aspectran.daemon.command.polling.CommandParameters;

import java.util.Map;

public class TransletCommand extends AbstractCommand {

    private static final String NAMESPACE = "builtins";

    private static final String COMMAND_NAME = "translet";

    private final CommandDescriptor descriptor = new CommandDescriptor();

    public TransletCommand(CommandRegistry registry) {
        super(registry);
    }

    @Override
    public String execute(CommandParameters parameters) throws Exception {
        String transletName = parameters.getTransletName();
        if (transletName == null) {
            throw new IllegalRuleException("'translet' parameter is not specified");
        }

        ItemRuleMap parameterItemRuleMap = parameters.getParameterItemRuleMap();
        ItemRuleMap attributeItemRuleMap = parameters.getAttributeItemRuleMap();

        ParameterMap parameterMap = null;
        Map<String, Object> attributeMap = null;
        if (parameterItemRuleMap != null || attributeItemRuleMap != null) {
            ItemEvaluator evaluator = new ItemExpression(getService().getActivityContext());
            if (parameterItemRuleMap != null) {
                parameterMap = evaluator.evaluateAsParameterMap(parameterItemRuleMap);
            }
            if (attributeItemRuleMap != null) {
                attributeMap = evaluator.evaluate(attributeItemRuleMap);
            }
        }

        Translet translet = getService().translate(transletName, parameterMap, attributeMap);
        return translet.getResponseAdapter().getWriter().toString();
    }

    @Override
    public Descriptor getDescriptor() {
        return descriptor;
    }

    private class CommandDescriptor implements Descriptor {

        @Override
        public String getNamespace() {
            return NAMESPACE;
        }

        @Override
        public String getName() {
            return COMMAND_NAME;
        }

        @Override
        public String getDescription() {
            return "Executes a translet";
        }

    }

}