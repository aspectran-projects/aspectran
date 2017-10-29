/*
 * Copyright (c) 2008-2017 The Aspectran Project
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

import com.aspectran.shell.command.option.HelpFormatter;
import com.aspectran.shell.command.option.Option;
import com.aspectran.shell.command.option.OptionParseException;
import com.aspectran.shell.command.option.Options;
import com.aspectran.shell.command.option.ParsedOptions;
import com.aspectran.shell.console.Console;
import com.aspectran.shell.service.ShellService;

public abstract class AbstractCommand implements Command {

    private final CommandRegistry registry;

    protected final Options options = new Options();

    public AbstractCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    public ShellService getService() {
        return registry.getService();
    }

    public Console getConsole() {
        return registry.getService().getConsole();
    }

    protected void addOption(Option option) {
        options.addOption(option);
    }

    protected ParsedOptions parse(String[] args) throws OptionParseException {
        return registry.getParser().parse(options, args);
    }

    protected void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( getDescriptor().getName(), options);
    }

}
