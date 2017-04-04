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
package com.aspectran.core.activity.response.transform;

import java.io.Writer;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.activity.process.result.ProcessResult;
import com.aspectran.core.activity.response.Response;
import com.aspectran.core.activity.response.transform.json.ContentsJsonWriter;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.util.json.JsonWriter;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class JsonTransformResponse.
 * 
 * Created: 2008. 03. 22 PM 5:51:58
 */
public class JsonTransformResponse extends TransformResponse {

    private static final Log log = LogFactory.getLog(JsonTransformResponse.class);

    private static final String CALLBACK_PARAM_NAME = "callback";

    private static final String ROUND_BRACKET_OPEN = "(";

    private static final String ROUND_BRACKET_CLOSE = ")";

    private final String characterEncoding;

    private final String contentType;

    private final boolean pretty;

    /**
     * Instantiates a new JsonTransform.
     *
     * @param transformRule the transform rule
     */
    public JsonTransformResponse(TransformRule transformRule) {
        super(transformRule);

        this.characterEncoding = transformRule.getCharacterEncoding();
        this.contentType = transformRule.getContentType();
        this.pretty = transformRule.isPretty();
    }

    @Override
    public void response(Activity activity) throws TransformResponseException {
        ResponseAdapter responseAdapter = activity.getResponseAdapter();
        if (responseAdapter == null) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("response " + transformRule);
        }

        try {
            if (this.characterEncoding != null) {
                responseAdapter.setCharacterEncoding(this.characterEncoding);
            } else {
                String characterEncoding = activity.getTranslet().getResponseCharacterEncoding();
                if (characterEncoding != null) {
                    responseAdapter.setCharacterEncoding(characterEncoding);
                }
            }

            if (contentType != null) {
                responseAdapter.setContentType(contentType);
            }

            Writer writer = responseAdapter.getWriter();
            ProcessResult processResult = activity.getProcessResult();

            // support for jsonp
            String callback = activity.getTranslet().getParameter(CALLBACK_PARAM_NAME);
            if (callback != null) {
                writer.write(callback + ROUND_BRACKET_OPEN);
            }

            JsonWriter jsonWriter = new ContentsJsonWriter(writer, pretty);
            jsonWriter.write(processResult);

            if (callback != null) {
                writer.write(ROUND_BRACKET_CLOSE);
            }

            writer.flush(); // Never close at this time. Owner will be close.
        } catch (Exception e) {
            throw new TransformResponseException(transformRule, e);
        }
    }

    @Override
    public ActionList getActionList() {
        return transformRule.getActionList();
    }

    @Override
    public Response replicate() {
        TransformRule transformRule = getTransformRule().replicate();
        return new JsonTransformResponse(transformRule);
    }

}