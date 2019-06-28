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
package com.aspectran.core.util.json;

import com.aspectran.core.util.BeanUtils;
import com.aspectran.core.util.apon.Parameter;
import com.aspectran.core.util.apon.ParameterValue;
import com.aspectran.core.util.apon.Parameters;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Converts an object to a JSON formatted string.
 * <p>If pretty-printing is enabled, the JsonWriter will add newlines and
 * indentation to the written data. Pretty-printing is disabled by default.</p>
 *
 * <p>Created: 2008. 06. 12 PM 8:20:54</p>
 * 
 * @author Juho Jeong
 */
public class JsonWriter implements Flushable, Closeable {

    private static final String DEFAULT_INDENT_STRING = "\t";

    private final Writer out;

    private boolean prettyPrint;

    private String indentString;

    private int indentDepth;

    private boolean willWriteValue;

    /**
     * Instantiates a new JsonWriter.
     * Pretty-printing is disabled by default.
     *
     * @param out the character-output stream
     */
    public JsonWriter(Writer out) {
        this(out, false);
    }

    /**
     * Instantiates a new JsonWriter.
     * If pretty-printing is enabled, includes spaces, tabs and new-lines to make the format more readable.
     * The default indentation string is a tab character.
     *
     * @param out the character-output stream
     * @param prettyPrint enables or disables pretty-printing
     */
    public JsonWriter(Writer out, boolean prettyPrint) {
        this.out = out;
        this.prettyPrint = prettyPrint;
        this.indentString = (prettyPrint ? DEFAULT_INDENT_STRING : null);
    }

    /**
     * Instantiates a new JsonWriter.
     * If pretty-printing is enabled, includes spaces, tabs and new-lines to make the format more readable.
     *
     * @param out the character-output stream
     * @param indentString the string that should be used for indentation when pretty-printing is enabled
     */
    public JsonWriter(Writer out, String indentString) {
        this.out = out;
        this.prettyPrint = (indentString != null);
        this.indentString = indentString;
    }

    /**
     * Write an object to a character stream.
     *
     * @param object the object to write to a character-output stream.
     * @throws IOException if an I/O error has occurred.
     */
    public JsonWriter write(Object object) throws IOException {
        if (object == null) {
            writeNull();
        } else if (object instanceof String
                || object instanceof Date) {
            writeValue(object.toString());
        } else if (object instanceof Boolean) {
            writeValue((Boolean)object);
        } else if (object instanceof Number) {
            writeValue((Number)object);
        } else if (object instanceof Parameters) {
            beginBlock();

            Map<String, ParameterValue> params = ((Parameters)object).getParameterValueMap();
            Iterator<ParameterValue> it = params.values().iterator();
            while (it.hasNext()) {
                Parameter p = it.next();
                String name = p.getName();
                Object value = p.getValue();
                checkCircularReference(object, value);

                writeName(name);
                write(value);
                if (it.hasNext()) {
                    writeComma();
                }
            }

            endBlock();
        } else if (object instanceof Map<?, ?>) {
            beginBlock();

            @SuppressWarnings("unchecked")
            Iterator<Map.Entry<Object, Object>> it = ((Map<Object, Object>)object).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Object, Object> entry = it.next();
                String name = entry.getKey().toString();
                Object value = entry.getValue();
                checkCircularReference(object, value);

                writeName(name);
                write(value);
                if (it.hasNext()) {
                    writeComma();
                }
            }

            endBlock();
        } else if (object instanceof Collection<?>) {
            beginArray();

            @SuppressWarnings("unchecked")
            Iterator<Object> it = ((Collection<Object>)object).iterator();
            while (it.hasNext()) {
                Object value = it.next();
                checkCircularReference(object, value);

                write(value);
                if (it.hasNext()) {
                    writeComma();
                }
            }

            endArray();
        } else if (object.getClass().isArray()) {
            beginArray();

            int len = Array.getLength(object);
            for (int i = 0; i < len; i++) {
                Object value = Array.get(object, i);
                checkCircularReference(object, value);

                if (i > 0) {
                    writeComma();
                }
                write(value);
            }

            endArray();
        } else {
            String[] readablePropertyNames = BeanUtils.getReadablePropertyNamesWithoutNonSerializable(object);
            if (readablePropertyNames != null && readablePropertyNames.length > 0) {
                beginBlock();

                for (int i = 0; i < readablePropertyNames.length; i++) {
                    Object value;
                    try {
                        value = BeanUtils.getProperty(object, readablePropertyNames[i]);
                    } catch (InvocationTargetException e) {
                        throw new IOException(e);
                    }
                    checkCircularReference(object, value);

                    writeName(readablePropertyNames[i]);
                    write(value);
                    if (i < (readablePropertyNames.length - 1)) {
                        writeComma();
                    }
                }

                endBlock();
            } else {
                writeValue(object.toString());
            }
        }
        return this;
    }

    /**
     * Writes a key name to a character stream.
     *
     * @param name the string to write to a character-output stream
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter writeName(String name) throws IOException {
        indent();
        out.write(escape(name));
        out.write(":");
        if (prettyPrint) {
            out.write(" ");
        }
        willWriteValue = true;
        return this;
    }

    /**
     * Writes a string to a character stream.
     * If {@code value} is null, write a null string ("").
     *
     * @param value the string to write to a character-output stream
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter writeValue(String value) throws IOException {
        if (!willWriteValue) {
            indent();
        }
        out.write(escape(value));
        willWriteValue = false;
        return this;
    }

    /**
     *  Writes a {@code Boolean} object to a character stream.
     *
     * @param value a {@code Boolean} object to write to a character-output stream
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter writeValue(Boolean value) throws IOException {
        if (!willWriteValue) {
            indent();
        }
        out.write(value.toString());
        willWriteValue = false;
        return this;
    }

    /**
     *  Writes a {@code Number} object to a character stream.
     *
     * @param value a {@code Number} object to write to a character-output stream
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter writeValue(Number value) throws IOException {
        if (!willWriteValue) {
            indent();
        }
        out.write(value.toString());
        willWriteValue = false;
        return this;
    }

    /**
     * Write a string "null" to a character stream.
     *
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter writeNull() throws IOException {
        out.write("null");
        return this;
    }

    /**
     * Write a comma character to a character stream.
     *
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter writeComma() throws IOException {
        out.write(",");
        if (prettyPrint) {
            out.write(" ");
        }
        nextLine();
        return this;
    }

    /**
     * Open a single curly bracket.
     *
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter beginBlock() throws IOException {
        if (!willWriteValue) {
            indent();
        }
        out.write("{");
        nextLine();
        indentDepth++;
        return this;
    }

    /**
     * Close the open curly bracket.
     *
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter endBlock() throws IOException {
        indentDepth--;
        nextLine();
        indent();
        out.write("}");
        return this;
    }

    /**
     * Open a single square bracket.
     *
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter beginArray() throws IOException {
        if (!willWriteValue) {
            indent();
        }
        out.write("[");
        nextLine();
        indentDepth++;
        willWriteValue = false;
        return this;
    }

    /**
     * Close the open square bracket.
     *
     * @return this JsonWriter
     * @throws IOException if an I/O error has occurred
     */
    public JsonWriter endArray() throws IOException {
        indentDepth--;
        nextLine();
        indent();
        out.write("]");
        return this;
    }

    /**
     * Write a tab character to a character stream.
     *
     * @throws IOException if an I/O error has occurred
     */
    protected void indent() throws IOException {
        if (prettyPrint) {
            for (int i = 0; i < indentDepth; i++) {
                out.write(indentString);
            }
        }
    }

    /**
     * Write a new line character to a character stream.
     *
     * @throws IOException if an I/O error has occurred
     */
    protected void nextLine() throws IOException {
        if (prettyPrint) {
            out.write("\n");
        }
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if (out != null) {
            out.close();
        }
    }

    private void checkCircularReference(Object wrapper, Object member) throws IOException {
        if (wrapper.equals(member)) {
            throw new IOException("JSON Serialization Failure: A circular reference was detected " +
                    "while converting a member object [" + member + "] in [" + wrapper + "]");
        }
    }

    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within &lt;/, allowing JSON
     * text to be delivered in HTML. In JSON text, a string cannot contain a
     * control character or an unescaped quote or backslash.
     *
     * @param string the input String, may be null
     * @return a String correctly formatted for insertion in a JSON text
     */
    private static String escape(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        int len = string.length();
        char b;
        char c = 0;
        String t;

        StringBuilder sb = new StringBuilder(len + 4);
        sb.append('"');
        for (int i = 0; i < len; i++) {
            b = c;
            c = string.charAt(i);

            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
                if (b == '<') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u").append(t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Converts an object to a JSON formatted string.
     * Pretty-printing is disabled by default.
     *
     * @param object an object to convert to a JSON formatted string
     * @return the JSON formatted string
     * @throws IOException if an I/O error has occurred
     */
    public static String stringify(Object object) throws IOException {
        return stringify(object, null);
    }

    /**
     * Converts an object to a JSON formatted string.
     * If pretty-printing is enabled, includes spaces, tabs and new-lines to make the format more readable.
     * The default indentation string is a tab character.
     *
     * @param object an object to convert to a JSON formatted string
     * @param prettyPrint enables or disables pretty-printing
     * @return the JSON formatted string
     * @throws IOException if an I/O error has occurred
     */
    public static String stringify(Object object, boolean prettyPrint) throws IOException {
        if (prettyPrint) {
            return stringify(object, DEFAULT_INDENT_STRING);
        } else {
            return stringify(object, null);
        }
    }

    /**
     * Converts an object to a JSON formatted string.
     * If pretty-printing is enabled, includes spaces, tabs and new-lines to make the format more readable.
     *
     * @param object an object to convert to a JSON formatted string
     * @param indentString the string that should be used for indentation when pretty-printing is enabled
     * @return the JSON formatted string
     * @throws IOException if an I/O error has occurred
     */
    public static String stringify(Object object, String indentString) throws IOException {
        if (object == null) {
            return null;
        }
        Writer out = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(out, indentString);
        jsonWriter.write(object);
        jsonWriter.close();
        return out.toString();
    }

}
