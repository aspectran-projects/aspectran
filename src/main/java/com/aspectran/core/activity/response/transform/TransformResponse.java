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
package com.aspectran.core.activity.response.transform;

import com.aspectran.core.activity.response.Response;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.context.rule.type.ResponseType;
import com.aspectran.core.context.rule.type.TransformType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * The Class TransformResponse.
 * 
 * Created: 2008. 03. 22 PM 5:51:58
 */
public abstract class TransformResponse implements Response {
	
	/** The transform rule. */
	protected final TransformRule transformRule;
	
	/**
	 * Instantiates a new TransformResponse.
	 * 
	 * @param transformRule the transform rule
	 */
	public TransformResponse(TransformRule transformRule) {
		this.transformRule = transformRule;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.response.Response#getResponseType()
	 */
	public ResponseType getResponseType() {
		return TransformRule.RESPONSE_TYPE;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.activity.response.Response#getContentType()
	 */
	public String getContentType() {
		return transformRule.getContentType();
	}

	/**
	 * Gets the transform type.
	 * 
	 * @return the transform type
	 */
	public TransformType getTransformType() {
		return transformRule.getTransformType();
	}
	
	/**
	 * Gets the transform rule.
	 * 
	 * @return the transform rule
	 */
	public TransformRule getTransformRule() {
		return transformRule;
	}
	
	/**
	 * Gets the template as stream.
	 * 
	 * @param file the file
	 * 
	 * @return the template as stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected InputStream getTemplateAsStream(File file) throws IOException {
		return new FileInputStream(file);
	}
	
	/**
	 * Gets the template as stream.
	 * 
	 * @param url the url
	 * 
	 * @return the template as stream
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected InputStream getTemplateAsStream(URL url) throws IOException {
		URLConnection conn = url.openConnection();
		return conn.getInputStream();
	}
	
	/**
	 * Gets the template as reader.
	 * 
	 * @param file the file
	 * @param encoding the encoding
	 * 
	 * @return the template as reader
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected Reader getTemplateAsReader(File file, String encoding) throws IOException {
		Reader reader;
		
		if(encoding != null) {
			InputStream inputStream = getTemplateAsStream(file);
			reader = new InputStreamReader(inputStream, encoding);
		} else
			reader = new FileReader(file);

		return reader;
	}
	
	/**
	 * Gets the template as reader.
	 * 
	 * @param url the url
	 * @param encoding the encoding
	 * 
	 * @return the template as reader
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected Reader getTemplateAsReader(URL url, String encoding) throws IOException {
		InputStream inputStream = getTemplateAsStream(url);
		Reader reader;
		
		if(encoding != null)
			reader = new InputStreamReader(inputStream, encoding);
		else
			reader = new InputStreamReader(inputStream);

		return reader;
	}
	
}
