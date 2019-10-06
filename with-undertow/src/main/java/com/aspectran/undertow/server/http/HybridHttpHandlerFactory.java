package com.aspectran.undertow.server.http;

import com.aspectran.core.component.bean.ablility.DisposableBean;
import com.aspectran.core.component.bean.aware.ActivityContextAware;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.config.AspectranConfig;
import com.aspectran.core.context.config.ContextConfig;
import com.aspectran.core.util.Assert;
import com.aspectran.core.util.lifecycle.LifeCycle;
import com.aspectran.undertow.server.TowServer;
import com.aspectran.undertow.server.resource.StaticResourceHandler;
import com.aspectran.undertow.service.DefaultTowService;
import com.aspectran.undertow.service.TowService;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ResourceManager;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionCookieConfig;
import io.undertow.server.session.SessionManager;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Created: 06/10/2019</p>
 */
public class HybridHttpHandlerFactory implements ActivityContextAware, DisposableBean {

    private ActivityContext context;

    private TowServer towServer;

    private ResourceManager resourceManager;

    private StaticResourceHandler staticResourceHandler;

    private SessionManager sessionManager;

    private SessionConfig sessionConfig;

    private List<HandlerWrapper> outerHandlerChainWrappers;

    private AspectranConfig aspectranConfig;

    private TowService towService;

    @Override
    public void setActivityContext(ActivityContext context) {
        this.context = context;
    }

    public void setTowServer(TowServer towServer) {
        this.towServer = towServer;
    }

    public void setResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void setStaticResourceHandler(StaticResourceHandler staticResourceHandler) {
        this.staticResourceHandler = staticResourceHandler;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    public void setOuterHandlerChainWrappers(HandlerWrapper[] wrappers) {
        if (wrappers != null && wrappers.length > 0) {
            this.outerHandlerChainWrappers = Arrays.asList(wrappers);
        } else {
            this.outerHandlerChainWrappers = null;
        }
    }

    public void setAspectranConfig(AspectranConfig aspectranConfig) {
        this.aspectranConfig = aspectranConfig;
    }

    public HttpHandler createHandler() {
        TowService towService = createTowService();

        if (sessionManager != null) {
            if (sessionConfig == null) {
                setSessionConfig(new SessionCookieConfig());
            }
            sessionManager.start();
        }

        HybridHttpHandler defaultHttpHandler = new HybridHttpHandler(resourceManager);
        defaultHttpHandler.setStaticResourceHandler(staticResourceHandler);
        defaultHttpHandler.setSessionManager(sessionManager);
        defaultHttpHandler.setSessionConfig(sessionConfig);
        defaultHttpHandler.setTowService(towService);

        if (outerHandlerChainWrappers != null) {
            return wrapHandlers(defaultHttpHandler, outerHandlerChainWrappers);
        } else {
            return defaultHttpHandler;
        }
    }

    private TowService createTowService() {
        Assert.state(towService == null, "Cannot reconfigure DefaultTowService");
        if (aspectranConfig == null) {
            towService = DefaultTowService.create(context.getRootService());
        } else {
            ContextConfig contextConfig = aspectranConfig.getContextConfig();
            if (contextConfig != null) {
                String basePath = contextConfig.getBasePath();
                if (basePath == null) {
                    contextConfig.setBasePath(context.getApplicationAdapter().getBasePath());
                }
            }
            towService = DefaultTowService.create(aspectranConfig);
        }
        if (towServer != null) {
            towServer.addLifeCycleListener(new LifeCycle.Listener() {
                @Override
                public void lifeCycleStopping(LifeCycle event) {
                    destroyTowService();
                }
            });
        }
        return towService;
    }

    private void destroyTowService() {
        if (towService != null) {
            if (towService.getServiceController().isActive()) {
                towService.getServiceController().stop();
                if (towService.isDerived()) {
                    towService.leaveFromRootService();
                }
            }
            towService = null;
        }
    }

    @Override
    public void destroy() throws Exception {
        destroyTowService();
        if (sessionManager != null) {
            sessionManager.stop();
        }
    }

    private static HttpHandler wrapHandlers(HttpHandler wrapee, List<HandlerWrapper> wrappers) {
        HttpHandler current = wrapee;
        for (HandlerWrapper wrapper : wrappers) {
            current = wrapper.wrap(current);
        }
        return current;
    }

}
