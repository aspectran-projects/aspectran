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
package com.aspectran.core.component.bean;

import com.aspectran.core.service.AspectranServiceException;
import com.aspectran.embed.sample.anno.ThirdResult;
import com.aspectran.embed.service.EmbeddedAspectranService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * Test cases for Annotated Configuration.
 *
 * <p>Created: 2016. 9. 7.</p>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AnnotatedConfigurationTest {

    private EmbeddedAspectranService aspectranService;

    @Before
    public void ready() throws Exception {
        String rootConfigLocation = "classpath:config/anno/annotated-configuration-test-config.xml";
        aspectranService = EmbeddedAspectranService.create(rootConfigLocation);
        aspectranService.start();
    }

    @Test
    public void firstTest() throws AspectranServiceException, IOException {
        ThirdResult thirdResult = aspectranService.getActivityContext().getBeanRegistry().getBean("thirdResult");
        assertEquals(thirdResult.getMessage(), "This is a second bean.");
    }

    @After
    public void finish() {
        aspectranService.stop();
    }

}