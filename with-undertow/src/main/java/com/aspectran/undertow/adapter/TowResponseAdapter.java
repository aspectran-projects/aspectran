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
package com.aspectran.undertow.adapter;

import com.aspectran.core.adapter.AbstractResponseAdapter;
import com.aspectran.core.context.rule.RedirectRule;
import com.aspectran.undertow.activity.TowActivity;
import com.aspectran.web.adapter.HttpServletResponseAdapter;
import com.aspectran.web.support.http.HttpStatus;
import com.aspectran.web.support.http.MediaType;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.CanonicalPathUtils;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.URLUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The Class TowResponseAdapter.
 *
 * <p>Created: 2019-07-27</p>
 */
public class TowResponseAdapter extends AbstractResponseAdapter {

    private final TowActivity activity;

    private String contentType;

    private String charset;

    private Writer writer;

    private ResponseState responseState = ResponseState.NONE;

    public TowResponseAdapter(HttpServerExchange exchange, TowActivity activity) {
        super(exchange);
        this.activity = activity;
    }

    @Override
    public String getHeader(String name) {
        return getHttpServerExchange().getResponseHeaders().getFirst(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return getHttpServerExchange().getResponseHeaders().get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return getHttpServerExchange().getResponseHeaders()
                .getHeaderNames()
                .stream()
                .map(HttpString::toString)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean containsHeader(String name) {
        return getHttpServerExchange().getResponseHeaders().contains(name);
    }

    @Override
    public void setHeader(String name, String value) {
        setHeader(HttpString.tryFromString(name), value);
    }

    private void setHeader(HttpString name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("Header name must not be null");
        }
        if (name.equals(Headers.CONTENT_TYPE)) {
            setContentType(value);
        } else {
            getHttpServerExchange().getResponseHeaders().put(name, value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        addHeader(HttpString.tryFromString(name), value);
    }

    private void addHeader(final HttpString name, final String value) {
        if (name == null) {
            throw new IllegalArgumentException("Header name must not be null");
        }
        if (name.equals(Headers.CONTENT_TYPE) &&
                !getHttpServerExchange().getResponseHeaders().contains(Headers.CONTENT_TYPE)) {
            setContentType(value);
        } else {
            getHttpServerExchange().getResponseHeaders().add(name, value);
        }
    }

    @Override
    public String getEncoding() {
        if (charset != null) {
            return charset;
        }
        return StandardCharsets.ISO_8859_1.name();
    }

    @Override
    public void setEncoding(String encoding) throws UnsupportedEncodingException {
        this.charset = encoding;
        if (contentType != null) {
            getHttpServerExchange().getResponseHeaders().put(Headers.CONTENT_TYPE, getContentType());
        }
    }

    @Override
    public String getContentType() {
        if (contentType != null) {
            if (charset != null) {
                return contentType + ";charset=" + charset;
            } else {
                return contentType;
            }
        }
        return null;
    }

    @Override
    public void setContentType(String contentType) {
        if (contentType == null) {
            return;
        }

        MediaType mediaType = MediaType.parseMediaType(contentType);
        String type = mediaType.getType();
        String charset = mediaType.getParameter("charset");

        this.contentType = type;
        if (charset != null) {
            this.charset = charset;
        }

        if (this.charset != null) {
            getHttpServerExchange().getResponseHeaders().put(Headers.CONTENT_TYPE,
                    type + "; charset=" + this.charset);
        } else {
            getHttpServerExchange().getResponseHeaders().put(Headers.CONTENT_TYPE, type);
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (responseState == ResponseState.WRITER) {
            throw new IllegalStateException("Cannot call getOutputStream(), getWriter() already called");
        }
        responseState = ResponseState.STREAM;
        ifStartBlocking();
        return getHttpServerExchange().getOutputStream();
    }

    @Override
    public Writer getWriter() throws IOException {
        if (writer == null) {
            if (responseState == ResponseState.STREAM) {
                throw new IllegalStateException("Cannot call getWriter(), getOutputStream() already called");
            }
            responseState = ResponseState.WRITER;
            ifStartBlocking();
            writer = new OutputStreamWriter(getHttpServerExchange().getOutputStream(), getEncoding());
        }
        return writer;
    }

    @Override
    public void flush() throws IOException {
        if (getHttpServerExchange().isResponseStarted()) {
            if (writer != null) {
                writer.flush();
            } else {
                getHttpServerExchange().getOutputStream().flush();
            }
        }
    }

    @Override
    public void redirect(String path) throws IOException {
        setStatus(HttpStatus.FOUND.value());
        if (URLUtils.isAbsoluteUrl(path)) { //absolute url
            getHttpServerExchange().getResponseHeaders().put(Headers.LOCATION, path);
        } else {
            String realPath;
            if (path.startsWith("/")) {
                realPath = path;
            } else {
                realPath = CanonicalPathUtils.canonicalize(path);
            }
            String url = getHttpServerExchange().getRequestScheme() + "://" +
                    getHttpServerExchange().getHostAndPort() + realPath;
            getHttpServerExchange().getResponseHeaders().put(Headers.LOCATION, url);
        }
    }

    @Override
    public String redirect(RedirectRule redirectRule) throws IOException {
        String path = HttpServletResponseAdapter.makeRedirectPath(redirectRule, activity);
        redirect(path);
        return path;
    }

    @Override
    public int getStatus() {
        return getHttpServerExchange().getStatusCode();
    }

    @Override
    public void setStatus(int status) {
        getHttpServerExchange().setStatusCode(status);
    }

    private void ifSetChunked() {
        HeaderMap responseHeaders = getHttpServerExchange().getResponseHeaders();
        if (!responseHeaders.contains(Headers.CONTENT_LENGTH)) {
            getHttpServerExchange().getResponseHeaders().put(Headers.TRANSFER_ENCODING, Headers.CHUNKED.toString());
        }
    }

    private void ifStartBlocking() {
        if (!getHttpServerExchange().isBlocking()) {
            getHttpServerExchange().startBlocking();
        }
    }

    private HttpServerExchange getHttpServerExchange() {
        return (HttpServerExchange)getAdaptee();
    }

    public enum ResponseState {
        NONE,
        STREAM,
        WRITER
    }

}
