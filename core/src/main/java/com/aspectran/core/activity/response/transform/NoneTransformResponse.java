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
package com.aspectran.core.activity.response.transform;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.response.Response;
import com.aspectran.core.activity.response.ResponseException;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.util.logging.Logger;
import com.aspectran.core.util.logging.LoggerFactory;

/**
 * The Class NoneTransformResponse.
 * 
 * <p>Created: 2018. 09. 11 PM 10:54:58</p>
 */
public class NoneTransformResponse extends TransformResponse {

    private static final Logger logger = LoggerFactory.getLogger(NoneTransformResponse.class);

    /**
     * Instantiates a new NoneTransformResponse.
     *
     * @param transformRule the transform rule
     */
    public NoneTransformResponse(TransformRule transformRule) {
        super(transformRule);
    }

    @Override
    public void commit(Activity activity) throws ResponseException {
        ResponseAdapter responseAdapter = activity.getResponseAdapter();
        if (responseAdapter == null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Response " + getTransformRule());
        }

        if (getContentType() != null) {
            responseAdapter.setContentType(getContentType());
        }
    }

    @Override
    public Response replicate() {
        return new NoneTransformResponse(getTransformRule().replicate());
    }

}
