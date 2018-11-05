/*
 * Copyright (c) 2008-2018 The Aspectran Project
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
package com.aspectran.shell.jline;

import com.aspectran.core.util.ResourceUtils;
import com.aspectran.shell.command.ShellCommander;
import com.aspectran.shell.jline.console.JLineConsole;
import com.aspectran.shell.service.ShellService;

import java.io.File;

/**
 * <p>Created: 2017. 3. 26.</p>
 */
public class JlineAspectranShellTest {

    public static void main(String[] args) {
        ShellService service = null;
        int exitStatus = 0;

        try {
            File aspectranConfigFile = ResourceUtils.getResourceAsFile("config/shell/jline/aspectran-config.apon");
            service = ShellService.run(aspectranConfigFile, new JLineConsole());
            ShellCommander commander = new ShellCommander(service);
            commander.perform();
        } catch (Exception e) {
            e.printStackTrace();
            exitStatus = 1;
        } finally {
            if (service != null) {
                service.release();
            }
        }

        System.exit(exitStatus);
    }

}
