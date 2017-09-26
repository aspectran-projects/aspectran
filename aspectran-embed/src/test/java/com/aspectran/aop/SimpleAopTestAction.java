/*
 * Copyright 2008-2017 Juho Jeong
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
package com.aspectran.aop;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.component.bean.annotation.Configuration;
import com.aspectran.core.component.bean.annotation.Request;

/**
 * <p>Created: 2016. 11. 5.</p>
 */
@Configuration
public class SimpleAopTestAction {

    @Request (translet = "/aop/test/target1")
    public void target1(Translet translet) {
        System.out.println("[SimpleAopTest] Target-1");
    }

    @Request (translet = "/aop/test/target2")
    public void target2(Translet translet) {
        System.out.println("[SimpleAopTest] Target-2");
        throw new SimpleAopTestException();
    }

}