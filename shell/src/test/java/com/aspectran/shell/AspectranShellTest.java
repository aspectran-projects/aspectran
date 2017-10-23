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
package com.aspectran.shell;

import com.aspectran.shell.service.ShellAspectranService;

/**
 * Testcase for Aspectran Shell.
 *
 * <p>Created: 2017. 3. 26.</p>
 */
public class AspectranShellTest {

    public static void main(String[] args) {
        String aspectranConfigFile = "classpath:config/shell/aspectran-config.apon";

        int exitStatus = 0;

        try {
            ShellAspectranService service = ShellAspectranService.create(aspectranConfigFile);
            service.start();
            ConsoleCommand command = new ConsoleCommand(service);
            command.perform();
        } catch (Exception e) {
            e.printStackTrace();
            exitStatus = 1;
        }

        System.exit(exitStatus);
    }

}