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
package com.aspectran.core.activity.response.transform;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.process.result.ActionResult;
import com.aspectran.core.activity.process.result.ContentResult;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.response.Response;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.context.rule.CustomTransformRule;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.util.ListIterator;

/**
 * The Class CustomTransformResponse.
 * 
 * Created: 2019. 06. 15
 */
public class CustomTransformResponse implements Response {

    private static final Log log = LogFactory.getLog(CustomTransformResponse.class);

    private static final ResponseType RESPONSE_TYPE = ResponseType.TRANSFORM;

    private final CustomTransformRule customTransformRule;

    /**
     * Instantiates a new CustomTransformResponse.
     */
    public CustomTransformResponse() {
        this.customTransformRule = CustomTransformRule.DEFAULT;
    }

    /**
     * Instantiates a new CustomTransformResponse.
     *
     * @param transformer the custom transformer
     */
    public CustomTransformResponse(CustomTransformer transformer) {
        this.customTransformRule = CustomTransformRule.newInstance(transformer);
    }

    @Override
    public void commit(Activity activity) {
        ResponseAdapter responseAdapter = activity.getResponseAdapter();
        if (responseAdapter == null) {
            return;
        }

        CustomTransformer transformer = customTransformRule.getTransformer();
        if (transformer == null) {
            transformer = findTransformer(activity.getProcessResult());
        }
        if (transformer == null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("Response with " + transformer);
        }

        try {
            transformer.transform(activity);
        } catch (Exception e) {
            throw new CustomTransformResponseException(transformer, e);
        }
    }

    @Override
    public ResponseType getResponseType() {
        return RESPONSE_TYPE;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getContentType(Activity activity) {
        return null;
    }

    @Override
    public ActionList getActionList() {
        return null;
    }

    @Override
    public Response replicate() {
        throw new UnsupportedOperationException("No replicable");
    }

    private CustomTransformer findTransformer(ProcessResult processResult) {
        if (processResult != null) {
            for (ListIterator<ContentResult> iterator1 = processResult.listIterator(processResult.size());
                 iterator1.hasPrevious(); ) {
                ContentResult contentResult = iterator1.previous();
                for (ListIterator<ActionResult> iterator2 = contentResult.listIterator(contentResult.size());
                     iterator2.hasPrevious(); ) {
                    ActionResult actionResult = iterator2.previous();
                    if (actionResult != null && actionResult.getResultValue() instanceof CustomTransformer) {
                        return (CustomTransformer)actionResult.getResultValue();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return customTransformRule.toString();
    }

}
