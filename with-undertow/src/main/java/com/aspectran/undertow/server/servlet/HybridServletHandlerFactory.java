/*
 * Copyright (c) 2008-2020 The Aspectran Project
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
package com.aspectran.undertow.server.servlet;

import com.aspectran.core.component.bean.annotation.AvoidAdvice;
import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.service.CoreService;
import com.aspectran.undertow.server.resource.StaticResourceHandler;
import com.aspectran.web.service.DefaultWebService;
import com.aspectran.web.service.WebService;
import com.aspectran.web.socket.jsr356.ServerEndpointExporter;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.servlet.api.DeploymentManager;

import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;
import java.util.Arrays;
import java.util.List;

import static com.aspectran.web.service.WebService.ROOT_WEB_SERVICE_ATTR_NAME;

/**
 * <p>Created: 2019-08-04</p>
 */
public class HybridServletHandlerFactory implements ActivityContextAware {

    private ActivityContext context;

    private TowServletContainer towServletContainer;

    private StaticResourceHandler staticResourceHandler;

    private List<HandlerWrapper> outerHandlerChainWrappers;

    @Override
    @AvoidAdvice
    public void setActivityContext(ActivityContext context) {
        this.context = context;
    }

    public TowServletContainer getTowServletContainer() {
        return towServletContainer;
    }

    public void setTowServletContainer(TowServletContainer towServletContainer) {
        this.towServletContainer = towServletContainer;
    }

    public StaticResourceHandler getStaticResourceHandler() {
        return staticResourceHandler;
    }

    public void setStaticResourceHandler(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    public void setOuterHandlerChainWrappers(HandlerWrapper[] wrappers) {
        if (wrappers != null && wrappers.length > 0) {
            this.outerHandlerChainWrappers = Arrays.asList(wrappers);
        } else {
            this.outerHandlerChainWrappers = null;
        }
    }

    public HttpHandler createHandler() throws Exception {
        HttpHandler rootHandler;
        if (towServletContainer != null && towServletContainer.getDeploymentManagers() != null) {
            PathHandler pathHandler = new PathHandler();
            for (DeploymentManager manager : towServletContainer.getDeploymentManagers()) {
                manager.deploy();

                ServletContext servletContext = manager.getDeployment().getServletContext();
                Object attr = servletContext.getAttribute(TowServletContext.DERIVED_WEB_SERVICE_ATTR);
                servletContext.removeAttribute(TowServletContext.DERIVED_WEB_SERVICE_ATTR);
                if ("true".equals(attr)) {
                    CoreService rootService = context.getRootService();
                    WebService webService = DefaultWebService.create(servletContext, rootService);
                    servletContext.setAttribute(ROOT_WEB_SERVICE_ATTR_NAME, webService);
                }

                // Required for any websocket support in undertow
                ServerContainer serverContainer = (ServerContainer)servletContext.getAttribute(ServerContainer.class.getName());
                if (serverContainer != null) {
                    ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter(context);
                    serverEndpointExporter.initServletContext(servletContext);
                    serverEndpointExporter.registerEndpoints();
                }

                HttpHandler handler = manager.start();
                String contextPath = manager.getDeployment().getDeploymentInfo().getContextPath();
                pathHandler.addPrefixPath(contextPath, handler);
            }
            rootHandler = pathHandler;
        } else {
            rootHandler = ResponseCodeHandler.HANDLE_404;
        }
        if (staticResourceHandler != null && staticResourceHandler.hasPatterns()) {
            rootHandler = new HybridServletHandler(rootHandler, staticResourceHandler);
        }
        if (outerHandlerChainWrappers != null) {
            rootHandler = wrapHandlers(rootHandler, outerHandlerChainWrappers);
        }
        return rootHandler;
    }

    private static HttpHandler wrapHandlers(HttpHandler wrapee, List<HandlerWrapper> wrappers) {
        HttpHandler current = wrapee;
        for (HandlerWrapper wrapper : wrappers) {
            current = wrapper.wrap(current);
        }
        return current;
    }

}
