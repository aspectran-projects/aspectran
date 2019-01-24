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
package com.aspectran.shell.command;

import com.aspectran.shell.command.option.Arguments;
import com.aspectran.shell.command.option.HelpFormatter;
import com.aspectran.shell.command.option.Option;
import com.aspectran.shell.command.option.Options;
import com.aspectran.shell.console.Console;
import com.aspectran.shell.service.ShellService;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand implements Command {

    private final CommandRegistry registry;

    private final Options options = new Options();

    private final List<Arguments> argumentsList = new ArrayList<>();

    public AbstractCommand(CommandRegistry registry) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        this.registry = registry;
    }

    public CommandRegistry getCommandRegistry() {
        return registry;
    }

    public CommandInterpreter getInterpreter() {
        return registry.getInterpreter();
    }

    public Console getConsole() {
        Console console = (getInterpreter() != null ? getInterpreter().getConsole() : null);
        if (console == null) {
            throw new IllegalStateException("CONSOLE NOT AVAILABLE");
        }
        return console;
    }

    public ShellService getService() {
        ShellService service = (getInterpreter() != null ? getInterpreter().getService() : null);
        if (service == null || !service.getServiceController().isActive()) {
            throw new IllegalStateException("SERVICE NOT AVAILABLE");
        }
        return service;
    }

    public boolean isServiceAvailable() {
        return (getInterpreter() != null && getInterpreter().getService() != null);
    }

    protected void addOption(Option option) {
        options.addOption(option);
    }

    protected void addArguments(Arguments arguments) {
        argumentsList.add(arguments);
    }

    protected Arguments touchArguments() {
        Arguments arguments = new Arguments();
        addArguments(arguments);
        return arguments;
    }

    protected void skipParsingAtNonOption() {
        options.setSkipParsingAtNonOption(true);
    }

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public List<Arguments> getArgumentsList() {
        return argumentsList;
    }

    @Override
    public void writeLine(String string) {
        getConsole().writeLine(string);
    }

    @Override
    public void writeLine(String format, Object... args) {
        getConsole().writeLine(format, args);
    }

    @Override
    public void writeLine() {
        getConsole().writeLine();
    }

    @Override
    public void writeError(String string) {
        getConsole().writeError(string);
    }

    @Override
    public void writeError(String format, Object... args) {
        getConsole().writeError(format, args);
    }

    @Override
    public void setStyle(String... styles) {
        getConsole().setStyle(styles);
    }

    @Override
    public void offStyle() {
        getConsole().offStyle();
    }

    @Override
    public void printUsage() {
        printUsage(getConsole());
    }

    @Override
    public void printUsage(Console console) {
        HelpFormatter formatter = new HelpFormatter(console);
        if (getDescriptor().getUsage() != null && getDescriptor().getUsage().length() > 0) {
            formatter.printHelp(getDescriptor().getUsage(), this, false);
        } else {
            formatter.printHelp(getDescriptor().getName(), this, true);
        }
    }

}
