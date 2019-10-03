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
package com.aspectran.jetty;

import com.aspectran.core.component.bean.ablility.DisposableBean;
import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The Jetty Server managed by Aspectran.
 *
 * <p>Created: 2016. 12. 22.</p>
 */
public class JettyServer extends Server implements InitializableBean, DisposableBean {

    private static final Log log = LogFactory.getLog(JettyServer.class);

    private boolean autoStartup;

    public JettyServer() {
        super();
    }

    public JettyServer(int port) {
        super(port);
    }

    public JettyServer(ThreadPool pool) {
        super(pool);
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public void setSystemProperty(String key, String value) {
        System.setProperty(key, value);
    }

    @Override
    public void initialize() throws Exception {
        start();
    }

    @Override
    public void destroy() {
        try {
            stop();
        } catch (Exception e) {
            log.error("Error while stopping jetty server: " + e.getMessage(), e);
        }
    }

    @Override
    public void doStart() throws Exception {
        if (autoStartup) {
            log.info("Starting embedded Jetty server");
            super.doStart();
            log.info("Jetty started on port(s) " + getActualPortsDescription()
                    + " with context path '" + getContextPath() + "'");
        }
    }

    @Override
    public void doStop() {
        log.info("Stopping embedded Jetty server");
        try {
            super.doStop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Unable to stop embedded Jetty server", e);
        }
    }

    private String getContextPath() {
        HandlerContainer handlerContainer = (HandlerContainer)getHandler();
        return Arrays.stream(handlerContainer.getChildHandlers())
                .filter(ContextHandler.class::isInstance).map(ContextHandler.class::cast)
                .map(ContextHandler::getContextPath).collect(Collectors.joining("', '"));
    }

    private String getActualPortsDescription() {
        StringBuilder ports = new StringBuilder();
        for (Connector connector : getConnectors()) {
            NetworkConnector connector1 = (NetworkConnector)connector;
            if (ports.length() != 0) {
                ports.append(", ");
            }
            ports.append(connector1.getLocalPort());
            ports.append(" (");
            ports.append(StringUtils.joinCommaDelimitedList(connector.getProtocols()));
            ports.append(")");
        }
        return ports.toString();
    }

}
