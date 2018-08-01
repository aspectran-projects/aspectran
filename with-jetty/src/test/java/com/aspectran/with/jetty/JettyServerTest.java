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
package com.aspectran.with.jetty;

import com.aspectran.core.context.config.AspectranConfig;
import com.aspectran.core.util.ResourceUtils;
import com.aspectran.embed.service.EmbeddedService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import java.io.File;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyServerTest {

    private EmbeddedService service;

    @Before
    public void ready() throws Exception {
        File configFile = ResourceUtils.getResourceAsFile("config/aspectran-config.apon");
        AspectranConfig aspectranConfig = new AspectranConfig(configFile);

        service = EmbeddedService.create(aspectranConfig);
        service.start();
    }

    @After
    public void finish() {
        if (service != null) {
            service.stop();
        }
    }

}
