package com.aspectran.undertow.server.handlers.http;

import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.component.session.SessionManager;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.util.ResourceUtils;
import com.aspectran.undertow.service.TowService;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.server.handlers.resource.ResourceManager;

import java.io.File;
import java.io.IOException;

/**
 * <p>Created: 2019-07-31</p>
 */
public abstract class AbstractHttpHandler implements HttpHandler, ActivityContextAware {

    private ActivityContext context;

    private SessionManager sessionManager;

    private ResourceHandler resourceHandler;

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setResourceBase(String resourceBase) throws IOException {
        ResourceManager resourceManager;
        if (resourceBase.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX)) {
            String basePackage = resourceBase.substring(ResourceUtils.CLASSPATH_URL_PREFIX.length());
            resourceManager = new ClassPathResourceManager(context.getEnvironment().getClassLoader(), basePackage);
        } else {
            File basePath = context.getEnvironment().toRealPathAsFile(resourceBase);
            resourceManager = new FileResourceManager(basePath);
        }
        resourceHandler = new ResourceHandler(resourceManager, ResponseCodeHandler.HANDLE_404);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
        } else {
            boolean processed = getTowService().execute(exchange);
            if (!processed && resourceHandler != null) {
                resourceHandler.handleRequest(exchange);
            }
        }
    }

    public abstract TowService getTowService();

    public ActivityContext getActivityContext() {
        return context;
    }

    @Override
    public void setActivityContext(ActivityContext context) {
        this.context = context;
    }

}
