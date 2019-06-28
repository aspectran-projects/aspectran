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

import com.aspectran.core.util.Assert;
import com.aspectran.core.util.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * Writes an APON object to an output source.
 *
 * <p>By default, the indentation string is "  " (two blanks)</p>
 */
public class AponWriter extends AponFormat implements Flushable, Closeable {

    private final Writer out;

    private final boolean nullWritable;

    private boolean typeHintWritable;

    private String indentString = DEFAULT_INDENT_STRING;

    private int indentDepth;

    /**
     * Instantiates a new AponWriter.
     *
     * @param out the character-output stream
     */
    public AponWriter(Writer out) {
        this(out, true);
    }

    /**
     * Instantiates a new AponWriter.
     *
     * @param out the character-output stream
     * @param nullWritable whether to write a null parameter
     */
    public AponWriter(Writer out, boolean nullWritable) {
        this.out = out;
        this.nullWritable = nullWritable;
    }

    /**
     * Instantiates a new AponWriter.
     *
     * @param file a File object to write to
     * @throws IOException if an I/O error occurs
     */
    public AponWriter(File file) throws IOException {
        this(new FileWriter(file));
    }

    /**
     * Instantiates a new AponWriter.
     *
     * @param file a File object to write to
     * @param nullWritable whether to write a null parameter
     * @throws IOException if an I/O error occurs
     */
    public AponWriter(File file, boolean nullWritable) throws IOException {
        this(new FileWriter(file), nullWritable);
    }

    /**
     * Sets whether write a type hint for values.
     *
     * @param typeHintWritable true, write a type hint for values
     */
    public void setTypeHintWritable(boolean typeHintWritable) {
        this.typeHintWritable = typeHintWritable;
    }

    /**
     * Specifies the indent string.
     *
     * @param indentString the indentation string, by default "  " (two blanks).
     */
    public void setIndentString(String indentString) {
        this.indentString = indentString;
    }

    /**
     * Write a Parameters object to the character-output stream.
     *
     * @param parameters the Parameters object to be converted
     * @throws IOException if an I/O error occurs
     */
    public void write(Parameters parameters) throws IOException {
        if (parameters != null) {
            for (Parameter pv : parameters.getParameterValueMap().values()) {
                if (nullWritable || pv.isAssigned()) {
                    write(pv);
                }
            }
        }
    }

    /**
     * Write a Parameter object to the character-output stream.
     *
     * @param parameter the Parameter object to be converted
     * @throws IOException if an I/O error occurs
     */
    public void write(Parameter parameter) throws IOException {
        Assert.notNull(parameter, "parameter must not be null");
        if (parameter.getValueType() == ParameterValueType.PARAMETERS) {
            if (parameter.isArray()) {
                List<Parameters> list = parameter.getValueAsParametersList();
                if (list != null) {
                    if (parameter.isBracketed()) {
                        writeName(parameter);
                        beginArray();
                        for (Parameters p : list) {
                            indent();
                            beginBlock();
                            write(p);
                            endBlock();
                        }
                        endArray();
                    } else {
                        for (Parameters p : list) {
                            writeName(parameter);
                            beginBlock();
                            write(p);
                            endBlock();
                        }
                    }
                }
            } else {
                if (nullWritable || parameter.getValueAsParameters() != null) {
                    writeName(parameter);
                    beginBlock();
                    write(parameter.getValueAsParameters());
                    endBlock();
                }
            }
        } else if (parameter.getValueType() == ParameterValueType.VARIABLE) {
            if (parameter.isArray()) {
                List<?> list = parameter.getValueList();
                if (list != null) {
                    if (parameter.isBracketed()) {
                        writeName(parameter);
                        beginArray();
                        for (Object value : list) {
                            indent();
                            if (value instanceof Parameters) {
                                write((Parameters)value);
                            } else if (value != null) {
                                writeString(value.toString());
                            } else {
                                writeNull();
                            }
                        }
                        endArray();
                    } else {
                        for (Object value : list) {
                            writeName(parameter);
                            if (value instanceof Parameters) {
                                write((Parameters)value);
                            } else if (value != null) {
                                writeString(value.toString());
                            } else {
                                writeNull();
                            }
                        }
                    }
                }
            } else {
                Object value = parameter.getValue();
                if (nullWritable || value != null) {
                    writeName(parameter);
                    beginBlock();
                    if (value instanceof Parameters) {
                        write((Parameters)value);
                    } else if (value != null) {
                        writeString(value.toString());
                    } else {
                        writeNull();
                    }
                    endBlock();
                }
            }
        } else if (parameter.getValueType() == ParameterValueType.STRING) {
            if (parameter.isArray()) {
                List<String> list = parameter.getValueAsStringList();
                if (list != null) {
                    if (parameter.isBracketed()) {
                        writeName(parameter);
                        beginArray();
                        for (String value : list) {
                            indent();
                            writeString(value);
                        }
                        endArray();
                    } else {
                        for (String value : list) {
                            writeName(parameter);
                            writeString(value);
                        }
                    }
                }
            } else {
                String s = parameter.getValueAsString();
                if (nullWritable || s != null) {
                    writeName(parameter);
                    writeString(s);
                }
            }
        } else if (parameter.getValueType() == ParameterValueType.TEXT) {
            if (parameter.isArray()) {
                List<String> list = parameter.getValueAsStringList();
                if (list != null) {
                    if (parameter.isBracketed()) {
                        writeName(parameter);
                        beginArray();
                        for (String text : list) {
                            indent();
                            beginText();
                            writeText(text);
                            endText();
                        }
                        endArray();
                    } else {
                        for (String text : list) {
                            writeName(parameter);
                            beginText();
                            writeText(text);
                            endText();
                        }
                    }
                }
            } else {
                String text = parameter.getValueAsString();
                if (text != null) {
                    writeName(parameter);
                    beginText();
                    writeText(text);
                    endText();
                } else if (nullWritable) {
                    writeName(parameter);
                    writeNull();
                }
            }
        } else {
            if (parameter.isArray()) {
                List<?> list = parameter.getValueList();
                if (list != null) {
                    if (parameter.isBracketed()) {
                        writeName(parameter);
                        beginArray();
                        for (Object value : list) {
                            indent();
                            write(value);
                        }
                        endArray();
                    } else {
                        for (Object value : list) {
                            writeName(parameter);
                            write(value);
                        }
                    }
                }
            } else {
                if (nullWritable || parameter.getValue() != null) {
                    writeName(parameter);
                    write(parameter.getValue());
                }
            }
        }
    }

    /**
     * Writes a comment to the character-output stream.
     *
     * @param message the comment to write to a character-output stream
     * @throws IOException if an I/O error occurs
     */
    public void comment(String message) throws IOException {
        if (message != null) {
            if (message.indexOf(NEW_LINE_CHAR) != -1) {
                String line;
                int start = 0;
                while ((line = readLine(message, start)) != null) {
                    out.write(COMMENT_LINE_START);
                    out.write(SPACE_CHAR);
                    out.write(line);
                    newLine();

                    start += line.length();
                    start = skipNewLineChar(message, start);
                    if (start == -1) {
                        break;
                    }
                }
                if (start != -1) {
                    out.write(COMMENT_LINE_START);
                    newLine();
                }
            } else {
                out.write(COMMENT_LINE_START);
                out.write(SPACE_CHAR);
                out.write(message);
                newLine();
            }
        }
    }

    private void writeName(Parameter parameter) throws IOException {
        indent();
        out.write(parameter.getName());
        if (typeHintWritable || parameter.isValueTypeHinted()) {
            out.write(ROUND_BRACKET_OPEN);
            out.write(parameter.getValueType().toString());
            out.write(ROUND_BRACKET_CLOSE);
        }
        out.write(NAME_VALUE_SEPARATOR);
        out.write(SPACE_CHAR);
    }

    private void writeString(String value) throws IOException {
        if (value == null) {
            writeNull();
        } else {
            if (value.isEmpty()) {
                out.write(DOUBLE_QUOTE_CHAR);
                out.write(DOUBLE_QUOTE_CHAR);
            } else if (value.startsWith(SPACE) || value.endsWith(SPACE)) {
                out.write(DOUBLE_QUOTE_CHAR);
                out.write(escape(value, false));
                out.write(DOUBLE_QUOTE_CHAR);
            } else {
                out.write(escape(value, true));
            }
            newLine();
        }
    }

    private void writeText(String text) throws IOException {
        String line;
        int start = 0;
        while ((line = readLine(text, start)) != null) {
            indent();
            out.write(TEXT_LINE_START);
            out.write(line);
            newLine();

            start += line.length();
            start = skipNewLineChar(text, start);
            if (start == -1) {
                break;
            }
        }
        if (start != -1) {
            indent();
            out.write(TEXT_LINE_START);
            newLine();
        }
    }

    private void write(Object value) throws IOException {
        if (value != null) {
            out.write(value.toString());
            newLine();
        } else {
            writeNull();
        }
    }

    private void writeNull() throws IOException {
        out.write(NULL);
        newLine();
    }

    private void beginBlock() throws IOException {
        out.write(CURLY_BRACKET_OPEN);
        newLine();
        increaseIndent();
    }

    private void endBlock() throws IOException {
        decreaseIndent();
        indent();
        out.write(CURLY_BRACKET_CLOSE);
        newLine();
    }

    private void beginArray() throws IOException {
        out.write(SQUARE_BRACKET_OPEN);
        newLine();
        increaseIndent();
    }

    private void endArray() throws IOException {
        decreaseIndent();
        indent();
        out.write(SQUARE_BRACKET_CLOSE);
        newLine();
    }

    private void beginText() throws IOException {
        out.write(ROUND_BRACKET_OPEN);
        newLine();
        increaseIndent();
    }

    private void endText() throws IOException {
        decreaseIndent();
        indent();
        out.write(ROUND_BRACKET_CLOSE);
        newLine();
    }

    private void newLine() throws IOException {
        out.write(NEW_LINE);
    }

    private void indent() throws IOException {
        if (indentString != null) {
            for (int i = 0; i < indentDepth; i++) {
                out.write(indentString);
            }
        }
    }

    private void increaseIndent() {
        if (indentString != null) {
            indentDepth++;
        }
    }

    private void decreaseIndent() {
        if (indentString != null) {
            indentDepth--;
        }
    }

    private String readLine(String value, int start) {
        if (start >= value.length()) {
            return null;
        }
        int end = start;
        for (; end < value.length(); end++) {
            char c = value.charAt(end);
            if (c == '\n' || c == '\r') {
                break;
            }
        }
        return (end > start ? value.substring(start, end) : StringUtils.EMPTY);
    }

    private int skipNewLineChar(String value, int start) {
        int end = start;
        boolean cr = false;
        boolean lf = false;
        for (; end < value.length(); end++) {
            char c = value.charAt(end);
            if (c != '\n' && c != '\r') {
                break;
            }
            if ((lf && c == '\n') || (cr && c == '\r')) {
                break;
            }
            if (c == '\n') {
                lf = true;
            }
            if (c == '\r') {
                cr = true;
            }
        }
        return (end > start ? end : -1);
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

    /**
     * Converts a Parameters object to an APON formatted string.
     *
     * @param parameters the Parameters object to be converted
     * @return a string that contains the APON text
     */
    public static String stringify(Parameters parameters) {
        return stringify(parameters, DEFAULT_INDENT_STRING);
    }

    /**
     * Converts a Parameters object to an APON formatted string.
     *
     * @param parameters the Parameters object to be converted
     * @param indentString the indentation string
     * @return a string that contains the APON text
     */
    public static String stringify(Parameters parameters, String indentString) {
        if (parameters == null) {
            return null;
        }
        try {
            Writer writer = new StringWriter();
            AponWriter aponWriter = new AponWriter(writer);
            aponWriter.setIndentString(indentString);
            aponWriter.write(parameters);
            aponWriter.close();
            return writer.toString();
        } catch (IOException e) {
            return null;
        }
    }

}
