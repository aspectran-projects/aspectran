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
package com.aspectran.shell.console;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

/**
 * Console I/O implementation that supports System Console.
 *
 * <p>Created: 2017. 3. 4.</p>
 */
public class DefaultConsole extends AbstractConsole {

    private volatile boolean reading;

    public DefaultConsole() {
        this(null);
    }

    public DefaultConsole(String encoding) {
        super(encoding);
    }

    @Override
    public String readCommandLine() {
        String prompt = getCommandPrompt();
        return readCommandLine(prompt);
    }

    @Override
    public String readCommandLine(String prompt) {
        try {
            String line;
            if (System.console() != null) {
                line = System.console().readLine(prompt).trim();
            } else {
                if (prompt != null) {
                    write(prompt);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                line = reader.readLine().trim();
            }
            line = readCommandMultiLine(line);
            return line;
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

    private String readCommandMultiLine(String line) throws IOException {
        boolean comments = COMMENT_DELIMITER.equals(line);
        boolean multiline = MULTILINE_DELIMITER.equals(line);
        boolean quoted = ("\"".equals(line) || "'".equals(line));
        if (comments || multiline || quoted) {
            if (System.console() != null) {
                line = System.console().readLine(comments ? COMMENT_PROMPT : MULTILINE_PROMPT).trim();
            } else {
                write(comments ? COMMENT_PROMPT : MULTILINE_PROMPT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                line = reader.readLine().trim();
            }
        }
        if (comments) {
            if (line.isEmpty()) {
                return line;
            }
            readCommandMultiLine(COMMENT_DELIMITER);
            return "";
        }
        if (quoted) {
            return line;
        }
        String next = null;
        String quote = searchQuote(line);
        if (quote != null) {
            next = readCommandMultiLine(quote);
            line += System.lineSeparator() + next;
            quote = searchQuote(line);
            if (quote != null) {
                return readCommandMultiLine(line);
            } else if (!line.endsWith(MULTILINE_DELIMITER)) {
                return line;
            }
        }
        if (line.endsWith(MULTILINE_DELIMITER)) {
            line = line.substring(0, line.length() - MULTILINE_DELIMITER.length()).trim();
            next = readCommandMultiLine(MULTILINE_DELIMITER);
        }
        if (!line.isEmpty() && !line.startsWith(COMMENT_DELIMITER)) {
            if (next != null && !next.isEmpty()) {
                return line + " " + next;
            } else {
                return line;
            }
        } else {
            if (next != null && !next.isEmpty()) {
                return next;
            } else {
                return "";
            }
        }
    }

    @Override
    public String readLine() {
        return readLine(null);
    }

    @Override
    public String readLine(String prompt) {
        try {
            reading = true;
            String line;
            if (System.console() != null) {
                line = System.console().readLine(prompt);
            } else {
                if (prompt != null) {
                    write(prompt);
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                line = reader.readLine();
            }
            return readMultiLine(line);
        } catch (IOException e) {
            throw new IOError(e);
        } finally {
            reading = false;
        }
    }

    private String readMultiLine(String line) throws IOException {
        if (line == null) {
            if (System.console() != null) {
                line = System.console().readLine(MULTILINE_PROMPT).trim();
            } else {
                write(MULTILINE_PROMPT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                line = reader.readLine().trim();
            }
        }
        if (line.endsWith(MULTILINE_DELIMITER)) {
            line = line.substring(0, line.length() - MULTILINE_DELIMITER.length()) +
                    System.lineSeparator() + readMultiLine(null);
        }
        return line;
    }

    @Override
    public String readLine(String format, Object... args) {
        return readLine(String.format(format, args));
    }

    @Override
    public String readPassword() {
        return readPassword(null);
    }

    @Override
    public String readPassword(String prompt) {
        if (System.console() != null) {
            return new String(System.console().readPassword(prompt));
        } else {
            return readLine(prompt);
        }
    }

    @Override
    public String readPassword(String format, Object... args) {
        return readPassword(String.format(format, args));
    }

    @Override
    public void write(String string) {
        System.out.print(string);
    }

    @Override
    public void write(String format, Object... args) {
        System.out.print(String.format(format, args));
    }

    @Override
    public void writeLine(String string) {
        System.out.println(string);
    }

    @Override
    public void writeLine(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    @Override
    public void writeLine() {
        System.out.println();
    }

    @Override
    public void writeError(String string) {
        System.err.println(string);
    }

    @Override
    public void writeError(String format, Object... args) {
        System.err.println(String.format(format, args));
    }

    @Override
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    @Override
    public String getEncoding() {
        return Charset.defaultCharset().name();
    }

    public OutputStream getOutput() {
        return System.out;
    }

    @Override
    public PrintWriter getWriter() {
        if (System.console() != null) {
            return System.console().writer();
        } else {
            return new PrintWriter(System.out);
        }
    }

    @Override
    public String[] getStyles() {
        return null;
    }

    @Override
    public void setStyle(String... styles) {
        // Nothing to do
    }

    @Override
    public void styleOff() {
        // Nothing to do
    }

    @Override
    public boolean isReading() {
        return reading;
    }

    @Override
    public boolean confirmRestart() {
        return confirmRestart(null);
    }

    @Override
    public boolean confirmRestart(String message) {
        if (isReading()) {
            writeLine("Illegal State");
            return false;
        }
        if (message != null) {
            writeLine(message);
        }
        String confirm = "Would you like to restart this shell [Y/n]? ";
        String yn = readLine(confirm);
        return (yn.isEmpty() || yn.equalsIgnoreCase("Y"));
    }

    @Override
    public boolean confirmQuit() {
        String confirm = "Are you sure you want to quit [Y/n]? ";
        String yn = readLine(confirm);
        return (yn.isEmpty() || yn.equalsIgnoreCase("Y"));
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public List<String> getCommandHistory() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void clearCommandHistory() {
        // History not supported
    }

    @Override
    public void setCommandHistoryFile(String historyFile) {
        // History not supported
    }

}
