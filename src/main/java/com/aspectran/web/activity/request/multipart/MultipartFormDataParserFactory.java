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
package com.aspectran.web.activity.request.multipart;

import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.SystemUtils;

/**
 * The Class MultipartFormDataParserFactory.
 *
 * @since 2.0.0
 */
public class MultipartFormDataParserFactory {
	
	private String temporaryFilePath;
	
	private long maxRequestSize = -1L;

	private String allowedFileExtensions;

	private String deniedFileExtensions;

	/**
	 * Instantiates a new Multipart request wrapper resolver.
	 */
	public MultipartFormDataParserFactory() {
	}

	/**
	 * Gets the temporary file path.
	 *
	 * @return the temporary file path
	 */
	public String getTemporaryFilePath() {
		return temporaryFilePath;
	}

	/**
	 * Sets the temporary file path.
	 *
	 * @param temporaryFilePath the temporary file path
	 */
	public void setTemporaryFilePath(String temporaryFilePath) {
		this.temporaryFilePath = temporaryFilePath;
	}

	/**
	 * Gets the maximum size of the request.
	 *
	 * @return the maximum size of the request
	 */
	public long getMaxRequestSize() {
		return maxRequestSize;
	}

	/**
	 * Sets the maximum size of the request.
	 *
	 * @param maxRequestSize the maximum size of the request
	 */
	public void setMaxRequestSize(long maxRequestSize) {
		this.maxRequestSize = maxRequestSize;
	}

	/**
	 * Sets the maximum size of the request.
	 *
	 * @param maxRequestSize the maximum size of the request
	 */
	public void setMaxRequestSize(String maxRequestSize) {
		this.maxRequestSize = StringUtils.convertToMachineFriendlyByteSize(maxRequestSize);
	}

	/**
	 * Gets the allowed file extensions.
	 *
	 * @return the allowed file extensions
	 */
	public String getAllowedFileExtensions() {
		return allowedFileExtensions;
	}

	/**
	 * Sets the allowed file extensions.
	 *
	 * @param allowedFileExtensions the allowed file extensions
	 */
	public void setAllowedFileExtensions(String allowedFileExtensions) {
		this.allowedFileExtensions = allowedFileExtensions;
	}

	/**
	 * Gets the denied file extensions.
	 *
	 * @return the denied file extensions
	 */
	public String getDeniedFileExtensions() {
		return deniedFileExtensions;
	}

	/**
	 * Sets the denied file extensions.
	 *
	 * @param deniedFileExtensions the denied file extensions
	 */
	public void setDeniedFileExtensions(String deniedFileExtensions) {
		this.deniedFileExtensions = deniedFileExtensions;
	}

	/**
	 * Creates a new MultipartFormDataParser object.
	 *
	 * @return the multipart form data parser
	 */
	public MultipartFormDataParser createMultipartFormDataParser() {
		MultipartFormDataParser parser = new MultipartFormDataParser();

		if(maxRequestSize > -1)
			parser.setMaxRequestSize(maxRequestSize);

		if(temporaryFilePath != null)
			parser.setTemporaryFilePath(temporaryFilePath);
		else
			parser.setTemporaryFilePath(SystemUtils.getProperty("java.io.tmpdir"));

		parser.setAllowedFileExtensions(allowedFileExtensions);
		parser.setDeniedFileExtensions(deniedFileExtensions);

		return parser;
	}

}
