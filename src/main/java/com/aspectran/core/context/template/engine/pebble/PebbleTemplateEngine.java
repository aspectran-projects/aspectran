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
package com.aspectran.core.context.template.engine.pebble;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import com.aspectran.core.context.template.engine.TemplateEngine;
import com.aspectran.core.context.template.engine.TemplateEngineProcessException;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

/**
 * The Class PebbleTemplateEngine.
 *
 * <p>Created: 2016. 1. 9.</p>
 */
public class PebbleTemplateEngine implements TemplateEngine {

    private final PebbleEngine engine;

    public PebbleTemplateEngine(PebbleEngine engine) {
        this.engine = engine;
    }

    @Override
    public void process(String templateName, Map<String, Object> model, String templateSource, Writer writer) throws TemplateEngineProcessException {
        try {
            PebbleTemplate compiledTemplate = engine.getTemplate(templateSource);
            compiledTemplate.evaluate(writer, model);
        } catch(Exception e) {
            throw new TemplateEngineProcessException(e);
        }
    }

    @Override
    public void process(String templateName, Map<String, Object> model, Writer writer) throws TemplateEngineProcessException {
        process(templateName, model, writer, null);
    }

    @Override
    public void process(String templateName, Map<String, Object> model, Writer writer, Locale locale) throws TemplateEngineProcessException {
        try {
            PebbleTemplate compiledTemplate = engine.getTemplate(templateName);
            compiledTemplate.evaluate(writer, model, locale);
        } catch(Exception e) {
            throw new TemplateEngineProcessException(e);
        }
    }
    
}
