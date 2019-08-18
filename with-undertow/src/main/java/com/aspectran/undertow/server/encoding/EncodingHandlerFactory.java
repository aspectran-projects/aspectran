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
package com.aspectran.undertow.server.encoding;

import com.aspectran.web.support.http.HttpHeaders;
import com.aspectran.web.support.http.MediaType;
import com.aspectran.web.support.http.MediaTypeUtils;
import io.undertow.attribute.RequestHeaderAttribute;
import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.encoding.ContentEncodingRepository;
import io.undertow.server.handlers.encoding.DeflateEncodingProvider;
import io.undertow.server.handlers.encoding.EncodingHandler;
import io.undertow.server.handlers.encoding.GzipEncodingProvider;
import io.undertow.util.HttpString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Configure HTTP compression in Undertow {@link HttpHandler} and
 * create an {@link EncodingHandler}.
 *
 * <p>Created: 2019-08-18</p>
 */
public class EncodingHandlerFactory {

    private static final String GZIP = "gzip";

    private static final String DEFLATE = "deflate";

    private HttpHandler handler;

    private String[] contentEncodingProviderNames;

    private long maxContentSize;

    private String[] mediaTypes;

    private String[] excludedUserAgents;

    public void setHandler(HttpHandler handler) {
        this.handler = handler;
    }

    public void setContentEncodingProviders(String... contentEncodingProviderNames) {
        this.contentEncodingProviderNames = contentEncodingProviderNames;
    }

    public void setMaxContentSize(long maxContentSize) {
        this.maxContentSize = maxContentSize;
    }

    public void setMediaTypes(String[] mediaTypes) {
        this.mediaTypes = mediaTypes;
    }

    public void setExcludedUserAgents(String[] excludedUserAgents) {
        this.excludedUserAgents = excludedUserAgents;
    }

    public EncodingHandler createEncodingHandler() {
        if (handler == null) {
            throw new IllegalStateException("The next handler is not specified");
        }
        ContentEncodingRepository contentEncodingRepository = new ContentEncodingRepository();
        if (contentEncodingProviderNames != null && contentEncodingProviderNames.length != 0) {
            Set<String> names = new LinkedHashSet<>(Arrays.asList(contentEncodingProviderNames));
            Predicate predicate = Predicates.and(getCompressionPredicates());
            int priority = 1000;
            for (String name : names) {
                if (GZIP.equalsIgnoreCase(name)) {
                    priority /= 10;
                    contentEncodingRepository.addEncodingHandler(GZIP, new GzipEncodingProvider(), priority, predicate);
                } else if (DEFLATE.equalsIgnoreCase(name)) {
                    priority /= 10;
                    contentEncodingRepository.addEncodingHandler(DEFLATE, new DeflateEncodingProvider(), priority, predicate);
                } else {
                    throw new IllegalArgumentException("Unknown content encoding provider '" + name + "'");
                }
            }
        }
        return new EncodingHandler(handler, contentEncodingRepository);
    }

    private Predicate[] getCompressionPredicates() {
        List<Predicate> predicates = new ArrayList<>();
        if (maxContentSize > 0L) {
            predicates.add(Predicates.maxContentSize(maxContentSize));
        }
        if (mediaTypes != null && mediaTypes.length > 0) {
            predicates.add(new CompressibleMimeTypePredicate(mediaTypes));
        }
        if (excludedUserAgents != null && excludedUserAgents.length > 0) {
            for (String agent : excludedUserAgents) {
                RequestHeaderAttribute agentHeader = new RequestHeaderAttribute(new HttpString(HttpHeaders.USER_AGENT));
                predicates.add(Predicates.not(Predicates.regex(agentHeader, agent)));
            }
        }
        return predicates.toArray(new Predicate[0]);
    }

    private static class CompressibleMimeTypePredicate implements Predicate {

        private final List<MediaType> mediaTypes;

        CompressibleMimeTypePredicate(String[] mediaTypes) {
            this.mediaTypes = new ArrayList<>(mediaTypes.length);
            for (String mediaTypeString : mediaTypes) {
                this.mediaTypes.add(MediaTypeUtils.parseMediaType(mediaTypeString));
            }
        }

        @Override
        public boolean resolve(HttpServerExchange exchange) {
            String contentType = exchange.getResponseHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            if (contentType != null) {
                for (MediaType mediaType : this.mediaTypes) {
                    if (mediaType.isCompatibleWith(MediaTypeUtils.parseMediaType(contentType))) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

}
