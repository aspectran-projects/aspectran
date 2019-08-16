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
package com.aspectran.undertow.activity;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.activity.ActivityTerminatedException;
import com.aspectran.core.activity.AdapterException;
import com.aspectran.core.activity.CoreActivity;
import com.aspectran.core.activity.TransletNotFoundException;
import com.aspectran.core.activity.request.RequestMethodNotAllowedException;
import com.aspectran.core.activity.request.RequestParseException;
import com.aspectran.core.adapter.ResponseAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.rule.RequestRule;
import com.aspectran.core.context.rule.type.MethodType;
import com.aspectran.core.support.i18n.locale.LocaleChangeInterceptor;
import com.aspectran.core.support.i18n.locale.LocaleResolver;
import com.aspectran.core.util.StringUtils;
import com.aspectran.undertow.adapter.TowRequestAdapter;
import com.aspectran.undertow.adapter.TowResponseAdapter;
import com.aspectran.undertow.adapter.TowSessionAdapter;
import com.aspectran.undertow.service.TowService;
import com.aspectran.web.activity.request.MultipartFormDataParser;
import com.aspectran.web.activity.request.MultipartRequestParseException;
import com.aspectran.web.activity.request.WebRequestBodyParser;
import com.aspectran.web.support.http.HttpHeaders;
import com.aspectran.web.support.http.HttpStatus;
import com.aspectran.web.support.http.MediaType;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.Headers;

import java.io.UnsupportedEncodingException;

/**
 * <p>Created: 2019-07-27</p>
 */
public class TowActivity extends CoreActivity {

    private static final String MULTIPART_FORM_DATA_PARSER_SETTING_NAME = "multipartFormDataParser";

    private static final String MAX_REQUEST_SIZE_SETTING_NAME = "maxRequestSize";

    private final TowService service;

    private final HttpServerExchange exchange;

    /**
     * Instantiates a new tow service
     *
     * @param service the tow service
     */
    public TowActivity(TowService service, HttpServerExchange exchange) {
        super(service.getActivityContext());
        this.service = service;
        this.exchange = exchange;
    }

    @Override
    public void prepare(String transletName, MethodType requestMethod) {
        // Check for HTTP POST with the X-HTTP-Method-Override header
        if (requestMethod == MethodType.POST) {
            String method = exchange.getRequestHeaders().getFirst(HttpHeaders.X_METHOD_OVERRIDE);
            if (method != null) {
                // Check if the header value is in our methods list
                MethodType hiddenRequestMethod = MethodType.resolve(method);
                if (hiddenRequestMethod != null) {
                    // Change the request method
                    requestMethod = hiddenRequestMethod;
                }
            }
        }

        try {
            super.prepare(transletName, requestMethod);
        } catch (TransletNotFoundException e) {
            if (StringUtils.startsWith(transletName, ActivityContext.NAME_SEPARATOR_CHAR) &&
                    !StringUtils.endsWith(transletName, ActivityContext.NAME_SEPARATOR_CHAR)) {
                String transletName2 = transletName + ActivityContext.NAME_SEPARATOR_CHAR;
                if (getActivityContext().getTransletRuleRegistry().contains(transletName2, requestMethod)) {
                    exchange.setStatusCode(HttpStatus.MOVED_PERMANENTLY.value());
                    exchange.getResponseHeaders().put(Headers.LOCATION, transletName2);
                    exchange.getResponseHeaders().put(Headers.CONNECTION, "close");
                    throw new ActivityTerminatedException("Provides for \"trailing slash\" redirects and " +
                            "serving directory index files");
                }
            }
            throw e;
        }
    }

    @Override
    protected void adapt() throws AdapterException {
        try {
            if (!exchange.isBlocking()) {
                exchange.startBlocking();
            }

            if (getOuterActivity() == null) {
                SessionManager sessionManager = exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
                SessionConfig sessionConfig = exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
                if (sessionManager != null && sessionConfig != null) {
                    setSessionAdapter(new TowSessionAdapter(exchange));
                }
            } else {
                setSessionAdapter(getOuterActivity().getSessionAdapter());
            }

            TowRequestAdapter requestAdapter = new TowRequestAdapter(getTranslet().getRequestMethod(), exchange);
            if (getOuterActivity() == null) {
                String requestEncoding = getIntendedRequestEncoding();
                if (requestEncoding != null) {
                    try {
                        requestAdapter.setEncoding(requestEncoding);
                    } catch (UnsupportedEncodingException e) {
                        throw new RequestParseException("Unable to set request encoding to " + requestEncoding, e);
                    }
                }
                requestAdapter.preparse();
            } else {
                requestAdapter.preparse((TowRequestAdapter)getOuterActivity().getRequestAdapter());
            }
            setRequestAdapter(requestAdapter);

            ResponseAdapter responseAdapter = new TowResponseAdapter(exchange, this);
            if (getOuterActivity() == null) {
                String responseEncoding = getIntendedResponseEncoding();
                if (responseEncoding != null) {
                    responseAdapter.setEncoding(responseEncoding);
                }
            }
            setResponseAdapter(responseAdapter);

            super.adapt();
        } catch (Exception e) {
            throw new AdapterException("Failed to adapt for Tow Activity", e);
        }
    }

    @Override
    protected void parseRequest() {
        MethodType requestMethod = getRequestAdapter().getRequestMethod();
        MethodType allowedMethod = getRequestRule().getAllowedMethod();
        if (allowedMethod != null && !allowedMethod.equals(requestMethod)) {
            throw new RequestMethodNotAllowedException(allowedMethod);
        }

        String maxRequestSizeSetting = getSetting(MAX_REQUEST_SIZE_SETTING_NAME);
        if (!StringUtils.isEmpty(maxRequestSizeSetting)) {
            long maxRequestSize = Long.parseLong(maxRequestSizeSetting);
            if (maxRequestSize >= 0L) {
                getRequestAdapter().setMaxRequestSize(maxRequestSize);
                exchange.setMaxEntitySize(maxRequestSize);
            }
        }

        MediaType mediaType = ((TowRequestAdapter)getRequestAdapter()).getMediaType();
        if (mediaType != null) {
            if (WebRequestBodyParser.isMultipartForm(requestMethod, mediaType)) {
                parseMultipartFormData();
            } else if (WebRequestBodyParser.isURLEncodedForm(mediaType)) {
                parseURLEncodedFormData();
            }
        }

        super.parseRequest();
    }

    /**
     * Parse the multipart form data.
     */
    private void parseMultipartFormData() {
        String multipartFormDataParser = getSetting(MULTIPART_FORM_DATA_PARSER_SETTING_NAME);
        if (multipartFormDataParser == null) {
            throw new MultipartRequestParseException("The setting name 'multipartFormDataParser' for multipart " +
                    "form data parsing is not specified. Please specify 'multipartFormDataParser' via Aspect so " +
                    "that Translet can parse multipart form data.");
        }

        MultipartFormDataParser parser = getBean(multipartFormDataParser);
        if (parser == null) {
            throw new MultipartRequestParseException("No bean named '" + multipartFormDataParser + "' is defined");
        }
        parser.parse(getRequestAdapter());
    }

    /**
     * Parse the URL-encoded Form Data to get the request parameters.
     */
    private void parseURLEncodedFormData() {
        WebRequestBodyParser.parseURLEncoded(getRequestAdapter());
    }

    @Override
    protected LocaleResolver resolveLocale() {
        LocaleResolver localeResolver = super.resolveLocale();
        if (localeResolver != null) {
            String localeChangeInterceptorId = getSetting(RequestRule.LOCALE_CHANGE_INTERCEPTOR_SETTING_NAME);
            if (localeChangeInterceptorId != null) {
                LocaleChangeInterceptor localeChangeInterceptor = getBean(localeChangeInterceptorId,
                        LocaleChangeInterceptor.class);
                localeChangeInterceptor.handle(getTranslet(), localeResolver);
            }
        }
        return localeResolver;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Activity> T newActivity() {
        TowActivity activity = new TowActivity(service, exchange);
        activity.setIncluded(true);
        return (T)activity;
    }

}
