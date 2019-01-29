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

import com.aspectran.core.activity.request.ParameterMap;
import com.aspectran.core.component.bean.BeanRegistry;
import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpression;
import com.aspectran.core.context.rule.ItemRuleList;
import com.aspectran.core.context.rule.ItemRuleMap;
import com.aspectran.core.util.StringUtils;
import com.aspectran.daemon.command.AbstractCommand;
import com.aspectran.daemon.command.CommandRegistry;
import com.aspectran.daemon.command.CommandResult;
import com.aspectran.daemon.command.polling.CommandParameters;
import com.aspectran.with.jetty.JettyServer;

import java.net.BindException;

/**
 * Use the command 'jetty' to control the Jetty Server.
 */
public class JettyCommand extends AbstractCommand {

    private static final String NAMESPACE = "builtins";

    private static final String COMMAND_NAME = "jetty";

    private final CommandDescriptor descriptor = new CommandDescriptor();

    public JettyCommand(CommandRegistry registry) {
        super(registry);
    }

    @Override
    public CommandResult execute(CommandParameters parameters) {
        try {
            ClassLoader classLoader = getService().getActivityContext().getEnvironment().getClassLoader();
            classLoader.loadClass("com.aspectran.with.jetty.JettyServer");
        } catch (ClassNotFoundException e) {
            return failed("Unable to load class com.aspectran.with.jetty.JettyServer due to missing dependency 'aspectran-with-jetty'", e);
        }

        try {
            String serverName = null;

            ItemRuleMap parameterItemRuleMap = parameters.getParameterItemRuleMap();
            if ((parameterItemRuleMap != null && !parameterItemRuleMap.isEmpty())) {
                ItemEvaluator evaluator = new ItemExpression(getService().getActivityContext());
                ParameterMap parameterMap = evaluator.evaluateAsParameterMap(parameterItemRuleMap);
                serverName = parameterMap.getParameter("server");
            }

            if (!StringUtils.hasLength(serverName)) {
                serverName = "jetty.server";
            }

            BeanRegistry beanRegistry = getService().getActivityContext().getBeanRegistry();
            JettyServer jettyServer;
            try {
                jettyServer = beanRegistry.getBean(com.aspectran.with.jetty.JettyServer.class, serverName);
            } catch (Exception e) {
                return failed("Jetty server is not available", e);
            }

            String arg = null;
            ItemRuleList itemRuleList = parameters.getArgumentItemRuleList();
            if (!itemRuleList.isEmpty()) {
                ItemEvaluator evaluator = new ItemExpression(getService().getActivityContext());
                arg = evaluator.evaluate(itemRuleList.get(0));
            }

            if (arg == null) {
                return failed("Command for Jetty control not specified");
            }

            switch (arg) {
                case "start":
                    if (jettyServer.isRunning()) {
                        return failed(warn("Jetty server is already running"));
                    }
                    try {
                        jettyServer.start();
                        return success(info(getStatus(jettyServer)));
                    } catch (BindException e) {
                        return failed("Jetty Server Error - Port already in use", e);
                    }
                case "restart":
                    try {
                        if (jettyServer.isRunning()) {
                            jettyServer.stop();
                        }
                        jettyServer.start();
                        return success(info(getStatus(jettyServer)));
                    } catch (BindException e) {
                        return failed("Jetty Server Error - Port already in use");
                    }
                case "stop":
                    if (!jettyServer.isRunning()) {
                        return failed(warn("Jetty Server is not running"));
                    }
                    try {
                        jettyServer.stop();
                        return success(info(getStatus(jettyServer)));
                    } catch (Exception e) {
                        return failed("Jetty Server Stop Failed", e);
                    }
                case "status":
                    return success(getStatus(jettyServer));
                default:
                    return failed(error("Unknown command '" + arg + "'"));
            }
        } catch (Exception e) {
            return failed(e);
        }
    }

    private String getStatus(JettyServer jettyServer) {
        return jettyServer.getState() + " - " + "Jetty " + JettyServer.getVersion();
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
            return "Use the command 'jetty' to control the Jetty server";
        }

    }

}
