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
package com.aspectran.shell.jline.console;

import com.aspectran.core.component.translet.TransletRuleRegistry;
import com.aspectran.core.context.rule.TransletRule;
import com.aspectran.core.util.wildcard.WildcardPattern;
import com.aspectran.shell.command.Command;
import com.aspectran.shell.command.CommandRegistry;
import com.aspectran.shell.command.option.Arguments;
import com.aspectran.shell.command.option.HelpFormatter;
import com.aspectran.shell.command.option.Option;
import com.aspectran.shell.service.ShellService;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

/**
 * Command and option name autocompleter.
 *
 * <p>Created: 17/11/2018</p>
 *
 * @since 5.8.0
 */
public class CommandCompleter implements Completer {

    private CommandRegistry commandRegistry;

    private ShellService service;

    public CommandCompleter() {
    }

    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void setService(ShellService service) {
        this.service = service;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        if (line.wordIndex() == 0) {
            makeCommandCandidates(line.word(), candidates);
            makeTransletCandidates(line.word(), candidates);
        } else if (line.wordIndex() > 0) {
            String cmd = line.words().get(0);
            makeArgumentsCandidates(cmd, line.word(), candidates);
        }
    }

    private void makeCommandCandidates(String cmd, List<Candidate> candidates) {
        if (commandRegistry != null) {
            for (Command command : commandRegistry.getAllCommands()) {
                String commandName = command.getDescriptor().getName();
                if (cmd == null || commandName.indexOf(cmd) == 0) {
                    candidates.add(new Candidate(commandName, commandName, command.getDescriptor().getNamespace(),
                            null, null, null, true));
                }
            }
        }
    }

    private void makeArgumentsCandidates(String cmd, String opt, List<Candidate> candidates) {
        if (commandRegistry != null) {
            Command command = commandRegistry.getCommand(cmd);
            if (command != null) {
                for (Option option : command.getOptions().getAllOptions()) {
                    String shortName = null;
                    if (option.getName() != null) {
                        shortName = HelpFormatter.DEFAULT_OPT_PREFIX + option.getName();
                    }
                    String longName = null;
                    if (option.getLongName() != null) {
                        longName = HelpFormatter.DEFAULT_LONG_OPT_PREFIX + option.getLongName();
                    }
                    String dispName;
                    if (shortName != null && longName != null) {
                        dispName = shortName + "," + longName;
                    } else if (shortName != null) {
                        dispName = shortName;
                    } else {
                        dispName = longName;
                    }
                    if (shortName != null && (opt == null || shortName.indexOf(opt) == 0)) {
                        candidates.add(new Candidate(shortName, dispName,
                                command.getOptions().getTitle(), null, null, longName, false));
                    } else if (longName != null && (opt == null || longName.indexOf(opt) == 0)) {
                        candidates.add(new Candidate(longName, dispName,
                                command.getOptions().getTitle(), null, null, null, false));
                    }
                }
                for (Arguments arguments : command.getArgumentsList()) {
                    for (String name : arguments.keySet()) {
                        candidates.add(new Candidate(name, name,
                                arguments.getTitle(), null, null, null, false));
                    }
                }
            }
        }
    }

    private void makeTransletCandidates(String cmd, List<Candidate> candidates) {
        if (service == null ||
                !service.getServiceController().isActive()) {
            return;
        }
        TransletRuleRegistry transletRuleRegistry = service.getActivityContext().getTransletRuleRegistry();
        for (TransletRule transletRule : transletRuleRegistry.getTransletRules()) {
            String transletName = transletRule.getName();
            String dispName = transletName;
            if (service.isExposable(transletName)) {
                if (cmd == null || transletName.indexOf(cmd) == 0) {
                    if (transletRule.hasPathVariables()) {
                        transletName = transletRule.getNamePattern().toString();
                        if (transletRule.getNameTokens().length == 1) {
                            transletName = transletName.replace(WildcardPattern.STAR_CHAR, ' ');
                            transletName = transletName.replace(WildcardPattern.PLUS_CHAR, ' ');
                            transletName = transletName.replace(WildcardPattern.QUESTION_CHAR, ' ');
                            transletName = transletName.trim();
                        }
                    }
                    if (!transletName.isEmpty()) {
                        candidates.add(new Candidate(transletName, dispName, "translets",
                                null, null, null, true));
                    }
                }
            }
        }
    }

}
