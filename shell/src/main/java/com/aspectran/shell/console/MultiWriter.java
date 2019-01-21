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

import java.io.IOException;
import java.io.Writer;

/**
 * The writer that handles multiple writers.
 *
 * <p>Created: 2017. 3. 9.</p>
 */
public class MultiWriter extends Writer {

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
