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

import com.aspectran.core.util.ClassUtils;
import com.aspectran.shell.command.option.OptionParser;
import com.aspectran.shell.command.option.SimpleOptionParser;
import com.aspectran.shell.service.ShellAspectranService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Created: 2017. 10. 25.</p>
 */
public class CommandRegistry {

    private Map<String, Command> commands = new LinkedHashMap<>();

    private OptionParser parser = new SimpleOptionParser();

    private ShellAspectranService service;

    public CommandRegistry(ShellAspectranService service) {
        this.service = service;
    }

    public ShellAspectranService getService() {
        return service;
    }

    public OptionParser getParser() {
        return parser;
    }

    public Command getCommand(String commandName) {
        return commands.get(commandName);
    }

    public void addCommand(String... classNames) {
        if (classNames != null) {
            for (String className : classNames) {
                try {
                    ClassLoader classLoader = service.getAspectranClassLoader();
                    @SuppressWarnings("unchecked")
                    Class<? extends Command> commandClass = (Class<? extends Command>)classLoader.loadClass(className);
                    Command command = ClassUtils.createInstance(commandClass, this);
                    assert command != null;
                    commands.put(command.getDescriptor().getName(), command);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Unable to load Command class: " + className, e);
                }
            }
        }
    }

}
