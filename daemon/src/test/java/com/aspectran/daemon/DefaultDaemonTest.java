/*
 * Copyright (c) 2008-2020 The Aspectran Project
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
package com.aspectran.daemon;

import com.aspectran.core.context.env.Environment;
import com.aspectran.core.util.ResourceUtils;

import java.io.File;

/**
 * <p>Created: 2017. 12. 12.</p>
 */
public class DefaultDaemonTest {

    public static void main(String[] args) {
        try {
            System.setProperty(Environment.ACTIVE_PROFILES_PROPERTY_NAME, "daemon");
            File current = ResourceUtils.getResourceAsFile("com/aspectran/daemon");
            File root = new File(current, "../../../../../../demo/app");
            String[] args2 = { root.getCanonicalPath(), "config/aspectran-config.apon" };
            DefaultDaemon.main(args2);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

}