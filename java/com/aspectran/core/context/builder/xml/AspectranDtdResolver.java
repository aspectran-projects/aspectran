/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.core.context.builder.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Offline entity resolver for the Aspectran DTDs
 * 
 * <p>Created: 2008. 06. 14 오전 4:48:34</p>
 */
public class AspectranDtdResolver implements EntityResolver {

	private static final String ASPECTRAN_DTD = "/com/aspectran/core/context/builder/xml/dtd/aspectran-1.0.dtd";

	private static final Map<String, String> doctypeMap = new HashMap<String, String>();

	private final boolean validating;
	
	static {
		doctypeMap.put("-//aspectran.com//DTD Aspectran 1.0//EN".toUpperCase(), ASPECTRAN_DTD);
		doctypeMap.put("aspectran-1.0.dtd".toUpperCase(), ASPECTRAN_DTD);
	}

	public AspectranDtdResolver() {
		this(true);
	}
	
	public AspectranDtdResolver(boolean validating) {
		this.validating = validating;
	}
	
	/**
	 * Converts a public DTD into a local one.
	 * 
	 * @param publicId Unused but required by EntityResolver interface
	 * @param systemId The DTD that is being requested
	 * 
	 * @return The InputSource for the DTD
	 * 
	 * @throws SAXException If anything goes wrong
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
		if(validating) {
			try {
				InputSource source = null;
	
				if(publicId != null) {
					String path = doctypeMap.get(publicId.toUpperCase());
					source = getInputSource(path);
				}
				
				if(source == null && systemId != null) {
					String path = doctypeMap.get(systemId.toUpperCase());
					source = getInputSource(path);
				}
	
				return source;
			} catch(Exception e) {
				throw new SAXException(e.toString());
			}
		} else {
			return new InputSource(new StringReader(""));
		}
	}

	/**
	 * Gets the input source.
	 * 
	 * @param path the path
	 * @param source the source
	 * 
	 * @return the input source
	 * @throws IOException 
	 */
	private InputSource getInputSource(String path) throws IOException {
		InputSource source = null;
		
		if(path != null) {
			InputStream in = getClass().getResourceAsStream(path);
			
			source = new InputSource(in);
		}
		
		return source;
	}
}
