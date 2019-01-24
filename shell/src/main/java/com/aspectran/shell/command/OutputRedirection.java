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
package com.aspectran.shell.command;

import com.aspectran.core.util.ToStringBuilder;
import com.aspectran.shell.console.Console;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;

/**
 * <p>Created: 2017. 3. 8.</p>
 */
public class OutputRedirection {

    private final Operator operator;

    private String operand;

    public OutputRedirection(Operator operator) {
        this.operator = operator;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof OutputRedirection) {
            OutputRedirection redirection = (OutputRedirection)o;
            return redirection.getOperand().equals(operand) &&
                    redirection.getOperator().equals(operator);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 17;
        result = prime * result + operator.hashCode();
        result = prime * result + (operand != null ? operand.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        ToStringBuilder tsb = new ToStringBuilder();
        tsb.append("operator", operator);
        tsb.append("operand", operand);
        return tsb.toString();
    }

    public static String serialize(Collection<OutputRedirection> redirectionList) {
        StringBuilder sb = new StringBuilder();
        if (redirectionList != null) {
            for (OutputRedirection redirection : redirectionList) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(redirection.getOperator()).append(" ");
                sb.append(redirection.getOperand());
            }
        }
        return sb.toString();
    }

    public static PrintWriter determineOutputWriter(Console console, Writer[] redirectionWriters) {
        PrintWriter outputWriter;
        if (redirectionWriters == null || redirectionWriters.length == 0) {
            outputWriter = console.getUnclosedWriter();
        } else if (redirectionWriters.length == 1) {
            outputWriter = new PrintWriter(redirectionWriters[0]);
        } else {
            outputWriter = new PrintWriter(new MultiWriter(redirectionWriters));
        }
        return outputWriter;
    }

    /**
     * Output redirection operators.
     */
    public enum Operator {

        /**
         * Writes the command output to a text file.
         */
        OVERWRITE_OUT(">"), // >

        /**
         * Appends command output to the end of a text file.
         */
        APPEND_OUT(">>"); // >>

        private final String alias;

        Operator(String alias) {
            this.alias = alias;
        }

        @Override
        public String toString() {
            return this.alias;
        }

    }

    /**
     * The writer that handles multiple writers.
     *
     * <p>Created: 2017. 3. 9.</p>
     */
    public static class MultiWriter extends Writer {

        private final Writer[] writers;

        public MultiWriter(Writer[] writers) {
            this.writers = writers;
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            for (Writer writer : writers) {
                writer.write(cbuf, off, len);
            }
        }

        @Override
        public void flush() throws IOException {
            for (Writer writer : writers) {
                try {
                    writer.flush();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        @Override
        public void close() throws IOException {
            int unclosed = 0;
            for (Writer writer : writers) {
                try {
                    writer.close();
                } catch (IOException e) {
                    unclosed++;
                }
            }
            if (unclosed > 0) {
                throw new IOException("Failed to close the multi-writer");
            }
        }

    }

}