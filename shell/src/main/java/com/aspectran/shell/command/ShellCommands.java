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

import com.aspectran.shell.console.Console;
import com.aspectran.shell.service.ShellAspectranService;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Console Command Handler.
 *
 * <p>Created: 2017. 6. 3.</p>
 */
public class ShellCommands {

    private static final Log log = LogFactory.getLog(ShellCommands.class);

    private final ShellAspectranService service;

    private final Console console;

    public ShellCommands(ShellAspectranService service) {
        this.service = service;
        this.console = service.getConsole();
    }

    public void perform() {
        try {
            loop:
            while (true) {
                String command = console.readCommand();
                if (command == null) {
                    continue;
                }
                command = command.trim();
                if (command.isEmpty()) {
                    continue;
                }

                switch (command) {
                    case "restart":
                        service.restart();
                        break;
                    case "pause":
                        service.pause();
                        break;
                    case "resume":
                        service.resume();
                        break;
                    case "desc on":
                        log.info("Description On");
                        service.setDescriptable(true);
                        break;
                    case "desc off":
                        log.info("Description Off");
                        service.setDescriptable(false);
                        break;
                    case "help":
                        service.printUsage();
                        break ;
                    case "clear":
                        console.clearScreen();
                        break ;
                    case "mem":
                        mem(false);
                        break;
                    case "gc":
                        mem(true);
                        break;
                    case "quit":
                        break loop;
                    default:
                        service.serve(command);
                        console.writeLine();
                }
            }
        } catch (ConsoleTerminatedException e) {
            // Will be shutdown
        } catch (Exception e) {
            log.error("An error occurred while processing the command", e);
        } finally {
            if (service.isActive()) {
                log.info("Do not terminate this application while destroying all scoped beans");
            }
        }
    }

    /**
     * Displays memory usage.
     *
     * @param gc if true, perform a garbage collection
     */
    private void mem(boolean gc) throws Exception {
        long total = Runtime.getRuntime().totalMemory();
        long before = Runtime.getRuntime().freeMemory();

        console.setStyle("yellow");
        console.write("   Total memory: ");
        console.setStyle("fg:off");
        console.writeLine(StringUtils.convertToHumanFriendlyByteSize(total));
        console.setStyle("yellow");
        console.write("   Used memory: ");
        console.setStyle("fg:off");
        console.writeLine(StringUtils.convertToHumanFriendlyByteSize(total - before));
        if (gc) {
            // Let the finilizer finish its work and remove objects from its queue
            System.gc(); // asyncronous garbage collector might already run
            System.gc(); // to make sure it does a full gc call it twice
            System.runFinalization();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // do nothing
            }

            long after = Runtime.getRuntime().freeMemory();

            console.setStyle("yellow");
            console.write("   Free memory before GC: ");
            console.setStyle("fg:off");
            console.writeLine(StringUtils.convertToHumanFriendlyByteSize(before));
            console.setStyle("yellow");
            console.write("   Free memory after GC: ");
            console.setStyle("fg:off");
            console.writeLine(StringUtils.convertToHumanFriendlyByteSize(after));
            console.setStyle("yellow");
            console.write("   Memory gained with GC: ");
            console.setStyle("fg:off");
            console.writeLine(StringUtils.convertToHumanFriendlyByteSize(after - before));
        } else {
            console.setStyle("yellow");
            console.write("   Free memory: ");
            console.setStyle("fg:off");
            console.writeLine(StringUtils.convertToHumanFriendlyByteSize(before));
        }
        console.writeLine();
        console.offStyle();
    }

}