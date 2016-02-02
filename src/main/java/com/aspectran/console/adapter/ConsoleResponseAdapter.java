/**
 * Copyright 2008-2016 Juho Jeong
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
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;

import com.aspectran.console.activity.ConsoleActivity;
import com.aspectran.core.activity.Activity;
import com.aspectran.core.adapter.AbstractResponseAdapter;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.context.rule.RedirectResponseRule;

/**
 * The Class ConsoleResponseAdapter.
 * 
 * @author Juho Jeong
 * @since 2016. 1. 18.
 */
public class ConsoleResponseAdapter extends AbstractResponseAdapter implements ResponseAdapter {

	private PrintStream printStream;
	
	private String characterEncoding;
	
	private String contentType;

	/**
	 * Instantiates a new ConsoleResponseAdapter.
	 *
	 * @param activity the console activity
	 */
	public ConsoleResponseAdapter(ConsoleActivity activity) {
		super(activity);
		printStream = System.out;
		characterEncoding = System.getProperty(ConsoleRequestAdapter.FILE_ENCODING_PROP_NAME);
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return printStream;
	}

	@Override
	public Writer getWriter() throws IOException {
		if(characterEncoding != null)
			return new ConsolePrintWriter(printStream, characterEncoding);
		else
			return new ConsolePrintWriter(printStream);
	}

	@Override
	public void redirect(String url) throws IOException {
	}

	@Override
	public String redirect(Activity activity, RedirectResponseRule redirectResponseRule) throws IOException {
		return null;
	}

}
