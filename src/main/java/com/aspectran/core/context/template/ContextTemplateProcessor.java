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
package com.aspectran.core.context.template;

import com.aspectran.core.activity.Activity;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.expr.TokenExpression;
import com.aspectran.core.context.expr.TokenExpressor;
import com.aspectran.core.context.expr.token.Token;
import com.aspectran.core.context.rule.TemplateRule;
import com.aspectran.core.context.template.engine.TemplateEngine;
import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

import java.io.*;

/**
 * The Class ContextTemplateProcessor.
 *
 * <p>Created: 2016. 1. 14.</p>
 */
public class ContextTemplateProcessor implements TemplateProcessor {

    /** The log. */
    private final Log log = LogFactory.getLog(ContextTemplateProcessor.class);

    /** The context. */
    protected ActivityContext context;

    /** The template rule registry. */
    protected final TemplateRuleRegistry templateRuleRegistry;

    /** The initialized. */
    private boolean initialized;

    /**
     * Instantiates a new context template processor.
     *
     * @param templateRuleRegistry the template rule registry
     */
    public ContextTemplateProcessor(TemplateRuleRegistry templateRuleRegistry) {
        this.templateRuleRegistry = templateRuleRegistry;
    }

    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#getTemplateRuleRegistry()
     */
    public TemplateRuleRegistry getTemplateRuleRegistry() {
        return templateRuleRegistry;
    }

    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#process(java.lang.String)
     */
    public String process(String templateId) {
        StringWriter writer = new StringWriter();
        process(templateId, writer);

        return writer.toString();
    }

    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#process(java.lang.String, java.io.Writer)
     */
    public void process(String templateId, Writer writer) {
    	Activity activity = context.getCurrentActivity();

        process(templateId, activity, writer);
    }
    
    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#process(com.aspectran.core.context.rule.TemplateRule)
     */
    public String process(TemplateRule templateRule) {
        StringWriter writer = new StringWriter();
        process(templateRule, writer);

        return writer.toString();
    }

    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#process(com.aspectran.core.context.rule.TemplateRule, java.io.Writer)
     */
    public void process(TemplateRule templateRule, Writer writer) {
    	Activity activity = context.getCurrentActivity();

        process(templateRule, activity, writer);
    }

    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#process(java.lang.String, com.aspectran.core.activity.Activity, java.io.Writer)
     */
    public void process(String templateId, Activity activity, Writer writer) {
    	TemplateRule templateRule = templateRuleRegistry.getTemplateRule(templateId);

        if(templateRule == null) {
            throw new TemplateNotFoundException(templateId);
        }

        process(templateRule, activity, writer);
    }

    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#process(com.aspectran.core.context.rule.TemplateRule, com.aspectran.core.activity.Activity, java.io.Writer)
     */
    public void process(TemplateRule templateRule, Activity activity, Writer writer) {
        try {
            String engineBeanId = templateRule.getEngine();

            if(engineBeanId != null) {
                TemplateEngine engine = context.getBeanRegistry().getBean(engineBeanId);

                if(engine == null)
                    throw new IllegalArgumentException("No template engine bean registered for '" + engineBeanId + "'.");

                TemplateDataMap templateDataMap = new TemplateDataMap(activity);

                if(templateRule.isUseExternalSource()) {
                    String templateName = templateRule.getName();
                    engine.process(templateName, templateDataMap, writer, templateDataMap.getLocale());
                } else {
                    String templateSource = templateRule.getTemplateSource(activity.getApplicationAdapter());

                    if(templateSource != null) {
                        String templateName = templateRule.getId();
                        if(templateName == null)
                            templateName = templateRule.getEngine() + "/" + templateRule.hashCode();

                        engine.process(templateName, templateDataMap, templateSource, writer);
                    }
                }
            } else {
                Token[] contentTokens = templateRule.getContentTokens(activity.getApplicationAdapter());

                if(contentTokens != null) {
                    TokenExpressor expressor = new TokenExpression(activity);
                    expressor.express(contentTokens, writer);
                }
            }
        } catch(Exception e) {
            throw new TemplateProcessorException(templateRule, "Template processing failed.", e);
        }
    }
    
    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#initialize(com.aspectran.core.context.ActivityContext)
     */
    public synchronized void initialize(ActivityContext context) {
        if(initialized) {
            throw new UnsupportedOperationException("TemplateProcessor has already been initialized.");
        }

        this.context = context;

        initialized = true;

        log.info("TemplateProcessor has been initialized successfully.");
    }

    /* (non-Javadoc)
     * @see com.aspectran.core.context.template.TemplateProcessor#destroy()
     */
    public synchronized void destroy() {
        if(!initialized) {
            throw new UnsupportedOperationException("TemplateProcessor has not yet initialized.");
        }

        initialized = false;

        log.info("TemplateProcessor has been destroyed successfully.");
    }

}
