/*
 * Copyright (c) 2008-2021 The Aspectran Project
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
package com.aspectran.core.context.expr;

/**
 * The Class ExpressionEvaluationException.
 */
public class ExpressionEvaluationException extends RuntimeException {

    /** @serial */
    private static final long serialVersionUID = 4909566791419959020L;

    private final String expression;

    /**
     * Instantiates a new expression evaluation exception.
     *
     * @param expression the expression
     * @param cause the root cause
     */
    public ExpressionEvaluationException(String expression, Throwable cause) {
        super("Error evaluating expression '" + expression + "'. Cause: " + cause, cause);
        this.expression = expression;
    }

    /**
     * Gets the expression that failed evaluation.
     *
     * @return the item rule
     */
    public String getExpression() {
        return this.expression;
    }

}
