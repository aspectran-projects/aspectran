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
package com.aspectran.demo.examples;

import com.aspectran.core.activity.Translet;

import java.net.URL;

public class SampleResources {

    public String getSampleResourceFile(Translet translet) {
        ClassLoader classLoader = translet.getEnvironment().getClassLoader();
        URL url = classLoader.getResource("sample_resource.txt");
        String file = url.getFile();
        return file;
    }

}