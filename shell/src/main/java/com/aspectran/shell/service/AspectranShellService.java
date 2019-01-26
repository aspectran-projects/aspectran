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
package com.aspectran.shell.service;

import com.aspectran.core.activity.ActivityTerminatedException;
import com.aspectran.core.activity.Translet;
import com.aspectran.core.activity.request.parameter.ParameterMap;
import com.aspectran.core.component.translet.TransletNotFoundException;
import com.aspectran.core.context.AspectranRuntimeException;
import com.aspectran.core.context.config.AspectranConfig;
import com.aspectran.core.context.config.ContextConfig;
import com.aspectran.core.context.config.ExposalsConfig;
import com.aspectran.core.context.config.ShellConfig;
import com.aspectran.core.context.rule.type.MethodType;
import com.aspectran.core.service.ServiceStateListener;
import com.aspectran.core.util.BooleanUtils;
import com.aspectran.core.util.StringUtils;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;
import com.aspectran.shell.activity.ShellActivity;
import com.aspectran.shell.command.OutputRedirection;
import com.aspectran.shell.command.TransletCommandLine;
import com.aspectran.shell.console.Console;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Provides an interactive shell that lets you use or control Aspectran directly
 * from the command line.
 *
 * @since 2016. 1. 18.
 */
public class AspectranShellService extends AbstractShellService {

    private static final Log log = LogFactory.getLog(AspectranShellService.class);

    private static final String DEFAULT_APP_CONFIG_ROOT_FILE = "/config/app-config.xml";

    private long pauseTimeout = -1L;

    private AspectranShellService(Console console) {
        super(console);
    }

    @Override
    public Translet translate(TransletCommandLine transletCommandLine) {
        if (!isExposable(transletCommandLine.getTransletName())) {
            getConsole().writeLine("Unexposable translet: " + transletCommandLine.getTransletName());
            return null;
        }

        if (pauseTimeout != 0L) {
            if (pauseTimeout == -1L || pauseTimeout >= System.currentTimeMillis()) {
                if (pauseTimeout == -1L) {
                    getConsole().writeLine(getServiceName() + " has been paused");
                } else {
                    long remains = pauseTimeout - System.currentTimeMillis();
                    if (remains > 0L) {
                        getConsole().writeLine(getServiceName() + " has been paused and will resume after "
                                + remains + " ms");
                    } else {
                        getConsole().writeLine(getServiceName() + " has been paused and will soon resume");
                    }
                }
                return null;
            } else {
                pauseTimeout = 0L;
            }
        }

        Writer outputWriter = null;
        List<OutputRedirection> redirectionList = transletCommandLine.getLineParser().getRedirectionList();
        if (redirectionList != null) {
            try {
                outputWriter = OutputRedirection.determineOutputWriter(redirectionList, getConsole());
            } catch (Exception e) {
                getConsole().writeError("Invalid Output Redirection.");
                getConsole().writeLine(e.getMessage());
                return null;
            }
        }

        boolean procedural = (transletCommandLine.getParameterMap() == null);
        ParameterMap parameterMap = transletCommandLine.getParameterMap();
        String transletName = transletCommandLine.getTransletName();
        MethodType requestMethod = transletCommandLine.getRequestMethod();

        ShellActivity activity = null;
        Translet translet = null;
        try {
            activity = new ShellActivity(this);
            activity.setProcedural(procedural);
            activity.setParameterMap(parameterMap);
            activity.setOutputWriter(outputWriter);
            activity.prepare(transletName, requestMethod);
            activity.perform();
            translet = activity.getTranslet();
            if (isVerbose()) {
                String description = translet.getDescription();
                if (StringUtils.hasLength(description)) {
                    getConsole().writeLine(description);
                }
            }
            if (outputWriter == null) {
                String result = translet.getResponseAdapter().getWriter().toString();
                if (StringUtils.hasLength(result)) {
                    getConsole().writeLine(result);
                }
            }
        } catch (TransletNotFoundException e) {
            if (log.isTraceEnabled()) {
                log.trace("Unknown translet: " + transletName);
            }
            throw e;
        } catch (ActivityTerminatedException e) {
            if (log.isDebugEnabled()) {
                log.debug("Activity terminated: Cause: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new AspectranRuntimeException("An error occurred while processing translet: " + transletName, e);
        } finally {
            if (activity != null) {
                activity.finish();
            }
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    log.warn("Failed to close redirection writer", e);
                }
            }
        }
        return translet;
    }

    /**
     * Returns a new instance of {@code AspectranShellService}.
     *
     * @param aspectranConfig the aspectran configuration
     * @param console the console
     * @return the instance of {@code AspectranShellService}
     */
    public static AspectranShellService create(AspectranConfig aspectranConfig, Console console) {
        ContextConfig contextConfig = aspectranConfig.touchContextConfig();
        String appConfigRootFile = contextConfig.getString(ContextConfig.root);
        if (!StringUtils.hasText(appConfigRootFile)) {
            if (contextConfig.getParameter(ContextConfig.parameters) == null) {
                contextConfig.putValue(ContextConfig.root, DEFAULT_APP_CONFIG_ROOT_FILE);
            }
        }

        AspectranShellService service = new AspectranShellService(console);
        ShellConfig shellConfig = aspectranConfig.getShellConfig();
        if (shellConfig != null) {
            applyShellConfig(service, shellConfig);
        }
        service.prepare(aspectranConfig);
        setServiceStateListener(service);
        return service;
    }

    private static void applyShellConfig(AspectranShellService service, ShellConfig shellConfig) {
        service.setVerbose(BooleanUtils.toBoolean(shellConfig.getBoolean(ShellConfig.verbose)));
        service.setGreetings(shellConfig.getString(ShellConfig.greetings));
        ExposalsConfig exposalsConfig = shellConfig.getExposalsConfig();
        if (exposalsConfig != null) {
            String[] includePatterns = exposalsConfig.getStringArray(ExposalsConfig.plus);
            String[] excludePatterns = exposalsConfig.getStringArray(ExposalsConfig.minus);
            service.setExposals(includePatterns, excludePatterns);
        }
    }

    private static void setServiceStateListener(final AspectranShellService service) {
        service.setServiceStateListener(new ServiceStateListener() {
            @Override
            public void started() {
                service.pauseTimeout = 0;
                service.printGreetings();
                service.printHelp();
            }

            @Override
            public void restarted() {
                started();
            }

            @Override
            public void paused(long millis) {
                if (millis < 0L) {
                    throw new IllegalArgumentException("Pause timeout in milliseconds needs to be " +
                            "set to a value of greater than 0");
                }
                service.pauseTimeout = System.currentTimeMillis() + millis;
            }

            @Override
            public void paused() {
                service.pauseTimeout = -1L;
            }

            @Override
            public void resumed() {
                service.pauseTimeout = 0;
            }

            @Override
            public void stopped() {
                paused();
            }
        });
    }

}
