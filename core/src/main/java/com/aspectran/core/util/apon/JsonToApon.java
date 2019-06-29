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
package com.aspectran.core.util.apon;

import com.aspectran.core.lang.Nullable;
import com.aspectran.core.util.json.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Converts JSON to APON.
 *
 * @since 6.2.0
 */
public class JsonToApon {

    public static Parameters from(String json) throws IOException {
        return from(json, null);
    }

    public static Parameters from(String json, @Nullable Parameters container) throws IOException {
        if (json == null) {
            throw new IllegalArgumentException("json must not be null");
        }
        return from(new StringReader(json), container);
    }

    public static Parameters from(Reader in, @Nullable Parameters container) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("in must not be null");
        }

        JsonReader reader = new JsonReader(in);
        String name = null;

        if (container != null) {
            if (container instanceof ArrayParameters) {
                name = ArrayParameters.NONAME;
            }
        } else {
            container = new VariableParameters();
        }

        read(reader, container, name);

        return container;
    }

    private static void read(JsonReader reader, Parameters container, String name) throws IOException {
        switch (reader.peek()) {
            case BEGIN_BLOCK:
                reader.beginBlock();
                if (name != null) {
                    container = container.newParameters(name);
                }
                while (reader.hasNext()) {
                    read(reader, container, reader.nextName());
                }
                reader.endBlock();
                return;
            case BEGIN_ARRAY:
                reader.beginArray();
                while (reader.hasNext()) {
                    read(reader, container, name);
                }
                reader.endArray();
                return;
            case STRING:
                container.putValue(name, reader.nextString());
                return;
            case BOOLEAN:
                container.putValue(name, reader.nextBoolean());
                return;
            case NUMBER:
                try {
                    container.putValue(name, reader.nextInt());
                } catch (NumberFormatException e0) {
                    try {
                        container.putValue(name, reader.nextLong());
                    } catch (NumberFormatException e1) {
                        container.putValue(name, reader.nextDouble());
                    }
                }
                return;
            case NULL:
                reader.nextNull();
                Parameter parameter = container.getParameter(name);
                if (parameter == null || parameter.getValueType() != ValueType.PARAMETERS) {
                    container.putValue(name, null);
                }
                return;
            default:
                throw new IllegalStateException();
        }
    }

}
