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
package com.aspectran.console.inout.jline;

import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.jline.terminal.Terminal;

/**
 * The Class JlineAnsiStringWriter.
 *
 * <p>Created: 2017. 5. 21.</p>
 *
 * @since 4.1.0
 */
public class JlineAnsiStringWriter extends StringWriter {

    private final Terminal terminal;

    private final Writer writer;

    private int flushPos;

    public JlineAnsiStringWriter(Terminal terminal, Writer writer) {
        this.terminal = terminal;
        this.writer = writer;
    }

    @Override
    public void flush() {
        String source =  (flushPos > 0 ? getBuffer().substring(flushPos) : getBuffer().toString());
        flushPos = getBuffer().length();

        try {
            writer.write(JlineAnsiStringUtils.toAnsi(source , terminal));
        } catch (IOException e) {
            throw new IOError(e);
        }
    }

}
