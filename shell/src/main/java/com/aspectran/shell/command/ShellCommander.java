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

import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.shell.console.Console;
import com.aspectran.shell.service.ShellService;

import java.util.Arrays;

/**
 * The Shell Command Handler.
 *
 * <p>Created: 2017. 6. 3.</p>
 */
public class ShellCommander {

    private static final Log log = LogFactory.getLog(ShellCommander.class);

    private final ShellService service;

    private final Console console;

    private final CommandRegistry commandRegistry;

    public ShellCommander(ShellService service) {
        this.service = service;
        this.console = service.getConsole();
        this.commandRegistry = service.getCommandRegistry();
    }

    public void perform() {
        try {
            for (;;) {
                String commandLine = console.readCommandLine();
                if (commandLine == null) {
                    continue;
                }
                commandLine = commandLine.trim();
                if (commandLine.isEmpty()) {
                    continue;
                }

                CommandLineParser commandLineParser = CommandLineParser.parseCommandLine(commandLine);
                String[] arr = CommandLineParser.splitCommandLine(commandLineParser.getCommand());

                String commandName = arr[0];
                String[] args = Arrays.copyOfRange(arr, 1, arr.length);

                Command command = commandRegistry.getCommand(commandName);
                if (command != null) {
                    String result = command.execute(args);
                    if (result != null) {
                        console.writeLine(result);
                    }
                } else {
                    service.serve(commandLine);
                    console.writeLine();
                }
            }
        } catch (ConsoleTerminatedException e) {
            // Will be shutdown
        } catch (Exception e) {
            log.error("An error occurred while executing the command", e);
        } finally {
            if (service.isActive()) {
                log.info("Do not terminate this application while releasing all resources");
            }
        }
    }

}
