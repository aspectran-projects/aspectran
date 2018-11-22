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
/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context;

/**
 * An exception is thrown when Aspectran fails to run properly due to
 * insufficient environment settings.
 */
public class InsufficientEnvironmentException extends IllegalStateException {

    /** @serial */
    private static final long serialVersionUID = -8963344360314936952L;

    /**
     * Constructs a InsufficientEnvironmentException.
     */
    public InsufficientEnvironmentException() {
        super();
    }

    /**
     * Constructs an InsufficientEnvironmentException with the specified message.
     *
     * @param msg the specific message
     */
    public InsufficientEnvironmentException(String msg) {
        super(msg);
    }

    /**
     * Constructs an InsufficientEnvironmentException with the wrapped exception.
     *
     * @param cause the real cause of the exception
     */
    public InsufficientEnvironmentException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an AspectranCheckedException with the specified message and wrapped exception.
     *
     * @param msg the specific message
     * @param cause the real cause of the exception
     */
    public InsufficientEnvironmentException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
