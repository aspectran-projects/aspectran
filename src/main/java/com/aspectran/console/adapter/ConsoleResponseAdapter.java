/**
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
package com.aspectran.console.adapter;

import java.io.IOException;

import com.aspectran.console.inout.ConsoleInout;
import com.aspectran.core.adapter.BasicResponseAdapter;

/**
 * The Class ConsoleResponseAdapter.
 * 
 * @since 2016. 1. 18.
 */
public class ConsoleResponseAdapter extends BasicResponseAdapter {

	/**
	 * Instantiates a new ConsoleResponseAdapter.
	 *
	 * @throws IOException if an I/O error has occurred
	 */
	public ConsoleResponseAdapter(ConsoleInout consoleInout) throws IOException {
		super(null);

		setOutputStream(consoleInout.getOutput());
		setWriter(consoleInout.getUnclosableWriter());
	}

}
