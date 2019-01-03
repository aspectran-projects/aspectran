/*
 * Copyright (c) 2008-2019 The Aspectran Project
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
package com.aspectran.core.activity.response.transform.apon;

import com.aspectran.core.activity.process.result.ActionResult;
import com.aspectran.core.activity.process.result.ContentResult;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.util.apon.Parameters;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class ContentsAponAssemblerTest {

    @Test
    void testAssemble() throws InvocationTargetException {
        ProcessResult processResult = new ProcessResult();
        ContentResult contentResult = new ContentResult(processResult, 10);

        ActionResult actionResult0 = new ActionResult();
        actionResult0.setResultValue("action0", "value0");
        contentResult.addActionResult(actionResult0);

        ActionResult actionResult1 = new ActionResult();
        actionResult1.setResultValue("action1.result1", "value1");
        contentResult.addActionResult(actionResult1);

        ActionResult actionResult2 = new ActionResult();
        actionResult2.setResultValue("action1.result2", "value2");
        contentResult.addActionResult(actionResult2);

        ActionResult actionResult3 = new ActionResult();
        actionResult3.setResultValue("action1", "value3");
        contentResult.addActionResult(actionResult3);

        //actionResult0.setHidden(true);
        //actionResult1.setHidden(true);
        actionResult2.setHidden(true);
        actionResult3.setHidden(true);

        Parameters parameters = ContentsAponAssembler.assemble(processResult);
        System.out.println(parameters.toString());
    }

}