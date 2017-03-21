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
package com.aspectran.core.context.rule.type;

/**
 * Type of Action.
 * 
 * <p>Created: 2008. 03. 26 AM 12:58:38</p>
 */
public enum ActionType {

    BEAN("bean"),
    METHOD("method"),
    INCLUDE("include"),
    ECHO("echo"),
    HEADERS("headers");

    private final String alias;

    ActionType(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return this.alias;
    }

    /**
     * Returns an {@code ActionType} with a value represented
     * by the specified {@code String}.
     *
     * @param alias the action type as a {@code String}
     * @return an {@code ActionType}, may be {@code null}
     */
    public static ActionType resolve(String alias) {
        for (ActionType type : values()) {
            if (type.alias.equals(alias)) {
                return type;
            }
        }
        return null;
    }

}
