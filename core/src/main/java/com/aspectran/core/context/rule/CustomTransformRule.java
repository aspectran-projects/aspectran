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
package com.aspectran.core.context.rule;

import com.aspectran.core.activity.response.transform.CustomTransformer;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.context.rule.type.TransformType;
import com.aspectran.core.util.ToStringBuilder;

/**
 * The Class CustomTransformRule.
 * 
 * <p>Created: 2019. 06. 16</p>
 */
public class CustomTransformRule {

    public static final CustomTransformRule DEFAULT = CustomTransformRule.newInstance();

    private static final ResponseType RESPONSE_TYPE = ResponseType.TRANSFORM;

    private static final TransformType TRANSFORM_TYPE = TransformType.CUSTOM;

    private final CustomTransformer transformer;

    public CustomTransformRule() {
        this(null);
    }

    public CustomTransformRule(CustomTransformer transformer) {
        this.transformer = transformer;
    }

    /**
     * Gets the transform type.
     *
     * @return the transform type
     */
    public TransformType getTransformType() {
        return TRANSFORM_TYPE;
    }

    public CustomTransformer getTransformer() {
        return transformer;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.appendForce("responseType", RESPONSE_TYPE);
        tsb.appendForce("transformType", TRANSFORM_TYPE);
        tsb.append("transformer", transformer);
        return tsb.toString();
    }

    public static CustomTransformRule newInstance() {
        return new CustomTransformRule();
    }

    public static CustomTransformRule newInstance(CustomTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("transformer must not be null");
        }
        return new CustomTransformRule(transformer);
    }

}
