/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.aspectran.core.context.rule;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.expr.token.Tokenizer;
import com.aspectran.core.context.rule.type.TokenType;
import com.aspectran.core.util.BooleanUtils;

/**
 * The Class TemplateRule.
 * 
 * <p>Created: 2008. 03. 22 오후 5:51:58</p>
 */
public class TemplateRule {

	private String id;

	private final String engine;

	private String name;

	private String file;

	private String resource;
	
	private String url;

	private String encoding;

	private String content;
	
	private Token[] contentTokens;
	
	private Boolean noCache;

	private boolean builtin;

	private String templateSource;

	private long lastModifiedTime;

	private boolean loaded;

	public TemplateRule() {
		this(null);
	}

	public TemplateRule(String engine) {
		this.engine = engine;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEngine() {
		return engine;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Boolean getNoCache() {
		return noCache;
	}

	public boolean isNoCache() {
		return BooleanUtils.toBoolean(noCache);
	}

	public void setNoCache(Boolean noCache) {
		this.noCache = noCache;
	}

	public boolean isBuiltin() {
		return builtin;
	}

	public void setBuiltin(boolean builtin) {
		this.builtin = builtin;
	}

	public boolean isUseExternalSource() {
		if(name != null && file == null && resource == null && url == null)
			return true;

		return false;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		this.contentTokens = parseContentTokens(content);
	}

	public Token[] getContentTokens() {
		return this.contentTokens;
	}

	protected void setContentTokens(Token[] contentTokens) {
		this.contentTokens = contentTokens;
	}

	public Token[] getContentTokens(ApplicationAdapter applicationAdapter) throws IOException {
		if(engine != null) {
			throw new UnsupportedOperationException();
		}

		if(this.file != null || this.resource != null || this.url != null) {
			if(this.noCache) {
				String template = loadTemplateSource(applicationAdapter);
				return parseContentTokens(template);
			} else {
				loadCachedTemplateSource(applicationAdapter);
				return this.contentTokens;
			}
		} else {
			return this.contentTokens;
		}
	}

	private Token[] parseContentTokens(String content) {
		if(this.engine != null || content == null || content.length() == 0)
			return null;

		List<Token> tokenList = Tokenizer.tokenize(content, false);
		if(tokenList.size() > 0) {
			return tokenList.toArray(new Token[tokenList.size()]);
		} else {
			return new Token[0];
		}
	}

	public String getTemplateSource(ApplicationAdapter applicationAdapter) throws IOException {
		if(engine == null) {
			throw new UnsupportedOperationException();
		}

		if(this.file != null || this.resource != null || this.url != null) {
			if(this.noCache) {
				return loadTemplateSource(applicationAdapter);
			} else {
				loadCachedTemplateSource(applicationAdapter);
				return this.templateSource;
			}
		} else {
			return this.content;
		}
	}

	private void setTemplateSource(String templateSource) {
		this.templateSource = templateSource;
		this.contentTokens = parseContentTokens(templateSource);
	}

	private void loadCachedTemplateSource(ApplicationAdapter applicationAdapter) throws IOException {
		if(this.file != null) {
			File file = applicationAdapter.toRealPathAsFile(this.file);
			long lastModifiedTime = file.lastModified();

			if(lastModifiedTime > this.lastModifiedTime) {
				synchronized(this) {
					lastModifiedTime = file.lastModified();

					if(lastModifiedTime > this.lastModifiedTime) {
						Reader reader = getTemplateSourceAsReader(file, this.encoding);
						String template = readTempateSource(reader);
						reader.close();
						setTemplateSource(template);
						this.lastModifiedTime = lastModifiedTime;
					}
				}
			}
		} else if(this.resource != null) {
			if(!this.loaded) {
				synchronized(this) {
					if(!this.loaded) {
						ClassLoader classLoader = applicationAdapter.getClassLoader();
						File file = new File(classLoader.getResource(this.resource).getFile());
						Reader reader = getTemplateSourceAsReader(file, this.encoding);
						String template = readTempateSource(reader);
						reader.close();
						setTemplateSource(template);
						this.loaded = true;
					}
				}
			}
		} else if(this.url != null) {
			if(!this.loaded) {
				synchronized(this) {
					if(!this.loaded) {
						Reader reader = getTemplateSourceAsReader(new URL(this.url), this.encoding);
						String template = readTempateSource(reader);
						reader.close();
						setTemplateSource(template);
						this.loaded = true;
					}
				}
			}
		}
	}

	private String loadTemplateSource(ApplicationAdapter applicationAdapter) throws IOException {
		String templateSource = null;

		if(this.file != null) {
			File file = applicationAdapter.toRealPathAsFile(this.file);
			Reader reader = getTemplateSourceAsReader(file, this.encoding);
			templateSource = readTempateSource(reader);
			reader.close();
		} else if(this.resource != null) {
			ClassLoader classLoader = applicationAdapter.getClassLoader();
			File file = new File(classLoader.getResource(this.resource).getFile());
			Reader reader = getTemplateSourceAsReader(file, this.encoding);
			templateSource = readTempateSource(reader);
			reader.close();
		} else if(this.url != null) {
			Reader reader = getTemplateSourceAsReader(new URL(this.url), this.encoding);
			templateSource = readTempateSource(reader);
			reader.close();
		}

		return templateSource;
	}

	/**
	 * Gets the template source as reader.
	 *
	 * @param file the file
	 * @param encoding the encoding
	 * @return the template as reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Reader getTemplateSourceAsReader(File file, String encoding) throws IOException {
		Reader reader;

		if(encoding != null) {
			InputStream inputStream = new FileInputStream(file);
			reader = new InputStreamReader(inputStream, encoding);
		} else
			reader = new FileReader(file);

		return reader;
	}

	/**
	 * Gets the template source as reader.
	 *
	 * @param url the url
	 * @param encoding the encoding
	 * @return the template as reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private Reader getTemplateSourceAsReader(URL url, String encoding) throws IOException {
		URLConnection conn = url.openConnection();
		InputStream inputStream = conn.getInputStream();
		Reader reader;

		if(encoding != null)
			reader = new InputStreamReader(inputStream, encoding);
		else
			reader = new InputStreamReader(inputStream);

		return reader;
	}

	private String readTempateSource(Reader reader) throws IOException {
		final char[] buffer = new char[1024];
		StringBuilder sb = new StringBuilder();
		int len;

		while((len = reader.read(buffer)) != -1) {
			sb.append(buffer, 0, len);
		}

		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		if(!builtin) {
			sb.append("id=").append(id).append(", ");
		}
		sb.append("engine=").append(engine);
		if(file != null) {
			sb.append(", file=").append(file);
		} else if(resource != null) {
			sb.append(", resource=").append(resource);
		} else if(url != null) {
			sb.append(", url=").append(url);
		} else if(name != null) {
			sb.append(", name=").append(name);
		} else {
			sb.append(", contentLength=").append(content == null ? 0 : content.length());
		}
		if(contentTokens != null) {
			sb.append(", contentTokenNames=[");
			int i = 0;
			for(Token t : contentTokens) {
				if(t.getType() != TokenType.TEXT) {
					if(i++ > 0)
						sb.append(", ");
					sb.append(t.getName());
				}
			}
			sb.append("]");
		}
		sb.append(", encoding=").append(encoding);
		sb.append(", noCache=").append(noCache);
		sb.append("}");

		return sb.toString();
	}

	public static TemplateRule newInstance(String id, String engine, String name, String file, String resource, String url, String content, String encoding, Boolean noCache) {
		TemplateRule tr = new TemplateRule(engine);
		tr.setId(id);
		tr.setName(name);
		tr.setFile(file);
		tr.setResource(resource);
		tr.setUrl(url);
		tr.setContent(content);
		tr.setEncoding(encoding);
		tr.setNoCache(noCache);

		return tr;
	}

	public static TemplateRule newInstanceForBuiltin(String engine, String name, String file, String resource, String url, String content, String encoding, Boolean noCache) {
		TemplateRule tr = new TemplateRule(engine);
		tr.setName(name);
		tr.setFile(file);
		tr.setResource(resource);
		tr.setUrl(url);
		tr.setContent(content);
		tr.setEncoding(encoding);
		tr.setNoCache(noCache);
		tr.setBuiltin(true);

		return tr;
	}

	public static TemplateRule newDerivedBuiltinTemplateRule(TemplateRule templateRule) {
		TemplateRule newTemplateRule = new TemplateRule(templateRule.getEngine());
		newTemplateRule.setName(templateRule.getName());
		newTemplateRule.setFile(templateRule.getFile());
		newTemplateRule.setResource(templateRule.getResource());
		newTemplateRule.setUrl(templateRule.getUrl());
		newTemplateRule.setEncoding(templateRule.getEncoding());
		newTemplateRule.setContent(templateRule.getContent());
		newTemplateRule.setContentTokens(templateRule.getContentTokens());
		newTemplateRule.setNoCache(templateRule.getNoCache());
		newTemplateRule.setBuiltin(true);

		return newTemplateRule;
	}

}
