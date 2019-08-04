package com.aspectran.undertow.server.handlers.http;

import com.aspectran.core.component.bean.ablility.InitializableBean;
import com.aspectran.undertow.service.AspectranTowService;

/**
 * Implementation of Undertow {@link io.undertow.server.HttpHandler} to handle HTTP
 * requests as activities of Aspectran.
 *
 * <p>Created: 2019-07-27</p>
 */
public class DefaultHttpHandler extends AbstractHttpHandler implements InitializableBean {

    private AspectranTowService towService;

    public AspectranTowService getTowService() {
        return towService;
    }

    @Override
    public void initialize() throws Exception {
        towService = AspectranTowService.create(getActivityContext().getRootService());
        if (getTowSessionManager() != null) {
            towService.setTowSessionManager(getTowSessionManager());
        }
    }

}
