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
package com.aspectran.shell.command.builtins;

import com.aspectran.core.component.schedule.ScheduleRuleRegistry;
import com.aspectran.core.context.rule.ScheduleRule;
import com.aspectran.core.context.rule.ScheduledJobRule;
import com.aspectran.core.context.rule.converter.RuleToParamsConverter;
import com.aspectran.core.context.rule.params.ScheduleParameters;
import com.aspectran.core.context.rule.params.SchedulerParameters;
import com.aspectran.core.context.rule.params.TriggerParameters;
import com.aspectran.core.util.apon.AponWriter;
import com.aspectran.core.util.apon.Parameters;
import com.aspectran.shell.command.AbstractCommand;
import com.aspectran.shell.command.CommandRegistry;
import com.aspectran.shell.command.option.Option;
import com.aspectran.shell.command.option.ParsedOptions;
import com.aspectran.shell.console.Console;
import com.aspectran.shell.service.ShellService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class JobCommand extends AbstractCommand {

    private static final String NAMESPACE = "builtins";

    private static final String COMMAND_NAME = "job";

    private final CommandDescriptor descriptor = new CommandDescriptor();

    public JobCommand(CommandRegistry registry) {
        super(registry);

        addOption(Option.builder("l")
                .longName("list")
                .hasValues()
                .optionalValue()
                .valueName("keywords")
                .desc("Print list of all scheduled jobs or those filtered by given keywords")
                .build());
        addOption(Option.builder("d")
                .longName("detail")
                .hasValues()
                .optionalValue()
                .valueName("translet_name")
                .desc("Print detailed information for the scheduled job")
                .build());
        addOption(Option.builder("enable")
                .hasValues()
                .valueName("translet_name")
                .desc("Enable a scheduled job with a given name")
                .build());
        addOption(Option.builder("disable")
                .hasValues()
                .valueName("translet_name")
                .desc("Disable a scheduled job with a given name")
                .build());
        addOption(Option.builder("h")
                .longName("help")
                .desc("Display help for this command")
                .build());
    }

    @Override
    public void execute(ParsedOptions options, Console console) throws Exception {
        ShellService service = getService();
        if (options.hasOption("help")) {
            printHelp(console);
        } else if (options.hasOption("list")) {
            String[] keywords = options.getValues("list");
            listScheduledJobs(service, console, keywords);
        } else if (options.hasOption("detail")) {
            String[] transletNames = options.getValues("detail");
            detailScheduledJobRule(service, console, transletNames);
        } else if (options.hasOption("enable")) {
            String[] transletNames = options.getValues("enable");
            changeJobActiveState(service, console, transletNames, false);
        } else if (options.hasOption("disable")) {
            String[] transletNames = options.getValues("disable");
            changeJobActiveState(service, console, transletNames, true);
        } else {
            printQuickHelp(console);
        }
    }

    private void listScheduledJobs(ShellService service, Console console, String[] keywords) {
        ScheduleRuleRegistry scheduleRuleRegistry = service.getActivityContext().getScheduleRuleRegistry();
        console.writeLine("-%4s-+-%-20s-+-%-33s-+-%-8s-", "----", "--------------------",
                "---------------------------------", "--------");
        console.writeLine(" %4s | %-20s | %-33s | %-8s ", "No.", "Schedule ID", "Job Name", "Enabled");
        console.writeLine("-%4s-+-%-20s-+-%-33s-+-%-8s-", "----", "--------------------",
                "---------------------------------", "--------");
        int num = 0;
        for (ScheduleRule scheduleRule : scheduleRuleRegistry.getScheduleRules()) {
            for (ScheduledJobRule jobRule : scheduleRule.getScheduledJobRuleList()) {
                if (keywords != null) {
                    boolean exists = false;
                    for (String keyw : keywords) {
                        if (jobRule.getTransletName().toLowerCase().contains(keyw.toLowerCase())) {
                            exists = true;
                        }
                    }
                    if (!exists) {
                        continue;
                    }
                }
                console.write("%5d | %-20s | %-33s |", ++num, scheduleRule.getId(), jobRule.getTransletName());
                if (jobRule.isDisabled()) {
                    console.setStyle("RED");
                } else {
                    console.setStyle("BLUE");
                }
                console.writeLine(" %-8s ", !jobRule.isDisabled());
                console.styleOff();
            }
        }
        if (num == 0) {
            console.writeLine("%33s %s", " ", "No Data");
        }
        console.writeLine("-%4s-+-%-20s-+-%-33s-+-%-8s-", "----", "--------------------",
                "---------------------------------", "--------");
    }

    private void detailScheduledJobRule(ShellService service, Console console, String[] transletNames)
            throws IOException {
        ScheduleRuleRegistry scheduleRuleRegistry = service.getActivityContext().getScheduleRuleRegistry();
        if (transletNames != null && transletNames.length > 0) {
            Set<ScheduledJobRule> scheduledJobRules = scheduleRuleRegistry.getScheduledJobRules(transletNames);
            if (scheduledJobRules.isEmpty()) {
                console.writeError("Unknown scheduled job " + Arrays.toString(transletNames));
                return;
            }
            int count = 0;
            for (ScheduledJobRule jobRule : scheduledJobRules) {
                ScheduleRule scheduleRule = jobRule.getScheduleRule();
                ScheduleParameters scheduleParameters = new ScheduleParameters();
                scheduleParameters.putValueNonNull(ScheduleParameters.description, scheduleRule.getDescription());
                scheduleParameters.putValueNonNull(ScheduleParameters.id, scheduleRule.getId());
                SchedulerParameters schedulerParameters = scheduleParameters.newParameters(ScheduleParameters.scheduler);
                schedulerParameters.putValueNonNull(SchedulerParameters.bean, scheduleRule.getSchedulerBeanId());
                TriggerParameters triggerParameters = scheduleRule.getTriggerParameters();
                if (triggerParameters != null && scheduleRule.getTriggerType() != null) {
                    triggerParameters.putValueNonNull(TriggerParameters.type, scheduleRule.getTriggerType().toString());
                    schedulerParameters.putValue(SchedulerParameters.trigger, scheduleRule.getTriggerParameters());
                }
                scheduleParameters.putValue(ScheduleParameters.job, RuleToParamsConverter.toScheduledJobParameters(jobRule));

                if (count == 0) {
                    console.writeLine("----------------------------------------------------------------------------");
                }
                AponWriter aponWriter = new AponWriter(console.getWriter(), false);
                aponWriter.write(scheduleParameters);
                console.writeLine("----------------------------------------------------------------------------");
                count++;
            }
            if (count == 0) {
                console.writeError("Unknown scheduled job " + Arrays.toString(transletNames));
            }
        } else {
            int count = 0;
            for (ScheduleRule scheduleRule : scheduleRuleRegistry.getScheduleRules()) {
                Parameters scheduleParameters = RuleToParamsConverter.toScheduleParameters(scheduleRule);

                if (count == 0) {
                    console.writeLine("----------------------------------------------------------------------------");
                }
                AponWriter aponWriter = new AponWriter(console.getWriter(), false);
                aponWriter.write(scheduleParameters);
                console.writeLine("----------------------------------------------------------------------------");
                count++;
            }
        }
    }

    private void changeJobActiveState(ShellService service, Console console, String[] transletNames, boolean disabled) {
        ScheduleRuleRegistry scheduleRuleRegistry = service.getActivityContext().getScheduleRuleRegistry();
        Set<ScheduledJobRule> scheduledJobRules = scheduleRuleRegistry.getScheduledJobRules(transletNames);
        if (scheduledJobRules.isEmpty()) {
            console.writeError("Unknown scheduled job " + Arrays.toString(transletNames));
            return;
        }
        for (ScheduledJobRule jobRule : scheduledJobRules) {
            if (disabled) {
                if (jobRule.isDisabled()) {
                    console.writeLine("Scheduled job '%s' on schedule '%s' is already inactive.",
                            jobRule.getTransletName(), jobRule.getScheduleRule().getId());
                } else {
                    jobRule.setDisabled(true);
                    console.writeLine("Scheduled job '%s' on schedule '%s' is now inactive.",
                            jobRule.getTransletName(), jobRule.getScheduleRule().getId());
                }
            } else {
                if (!jobRule.isDisabled()) {
                    console.writeLine("Scheduled job '%s' on schedule '%s' is already active.",
                            jobRule.getTransletName(), jobRule.getScheduleRule().getId());
                } else {
                    jobRule.setDisabled(false);
                    console.writeLine("Scheduled job '%s' on schedule '%s' is now active.",
                            jobRule.getTransletName(), jobRule.getScheduleRule().getId());
                }
            }
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
            return "Show scheduled jobs, or disable or enable them";
        }

        @Override
        public String getUsage() {
            return null;
        }

    }

}
