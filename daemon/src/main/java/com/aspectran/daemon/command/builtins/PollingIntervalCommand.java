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

import com.aspectran.core.context.expr.ItemEvaluator;
import com.aspectran.core.context.expr.ItemExpression;
import com.aspectran.core.context.rule.ItemRuleList;
import com.aspectran.daemon.command.AbstractCommand;
import com.aspectran.daemon.command.DaemonCommandRegistry;
import com.aspectran.daemon.command.CommandResult;
import com.aspectran.daemon.command.polling.CommandParameters;

public class PollingIntervalCommand extends AbstractCommand {

    private static final String NAMESPACE = "builtins";

    private static final String COMMAND_NAME = "pollingInterval";

    private final CommandDescriptor descriptor = new CommandDescriptor();

    public PollingIntervalCommand(DaemonCommandRegistry registry) {
        super(registry);
    }

    @Override
    public CommandResult execute(CommandParameters parameters) {
        try {
            long oldPollingInterval = getCommandRegistry().getDaemon().getCommandPoller().getPollingInterval();
            long pollingInterval = 0L;

            ItemRuleList itemRuleList = parameters.getArgumentItemRuleList();
            if (!itemRuleList.isEmpty()) {
                ItemEvaluator evaluator = new ItemExpression(getService().getActivityContext());
                pollingInterval = evaluator.evaluate(itemRuleList.get(0));
            }

            if (pollingInterval > 0L) {
                getCommandRegistry().getDaemon().getCommandPoller().setPollingInterval(pollingInterval);
                return success(info("The polling interval is changed from " + oldPollingInterval + "ms to " + pollingInterval + "ms"));
            } else if (pollingInterval < 0L) {
                return failed(error("The polling interval can not be negative: " + pollingInterval));
            } else {
                return failed(warn("The polling interval is not changed"));
            }
        } catch (Exception e) {
            return failed(e);
        }
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
            return "Specifies in seconds how often the daemon polls for new commands";
        }

    }

}
