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
package com.aspectran.scheduler;

import com.aspectran.core.context.config.AspectranConfig;
import com.aspectran.embed.service.EmbeddedAspectran;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>Created: 2016. 9. 7.</p>
 */
public class AspectranSchedulerTest {

    private EmbeddedAspectran aspectran;

    @Before
    public void ready() {
        AspectranConfig aspectranConfig = new AspectranConfig();
        aspectranConfig.updateRootConfigFile("classpath:config/scheduler/scheduler-config.xml");
        aspectranConfig.updateSchedulerConfig(0, true, true);

        aspectran = EmbeddedAspectran.run(aspectranConfig);
    }

    @Test
    public void dummyTest() throws InterruptedException {
        Thread.sleep(3000);
    }

    @After
    public void finish() {
        if (aspectran != null) {
            aspectran.release();
        }
    }

}