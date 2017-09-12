/*
 * Copyright 2008-2017 Juho Jeong
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
package com.aspectran.core.context.rule.parser;

import com.aspectran.core.adapter.ApplicationAdapter;
import com.aspectran.core.context.ActivityContext;
import com.aspectran.core.context.rule.appender.HybridRuleAppendHandler;
import com.aspectran.core.context.rule.appender.RuleAppendHandler;
import com.aspectran.core.context.rule.converter.ParamsToRuleConverter;
import com.aspectran.core.context.rule.params.AspectranParameters;

/**
 * The Class HybridActivityContextParser.
 * 
 * <p>Created: 2015. 01. 27 PM 10:36:29</p>
 */
public class HybridActivityContextParser extends AbstractActivityContextParser {

    public HybridActivityContextParser(ApplicationAdapter applicationAdapter) {
        super(applicationAdapter);
    }

    @Override
    public ActivityContext parse(String rootContext) throws ActivityContextParserException {
        try {
            if (rootContext == null) {
                throw new IllegalArgumentException("Argument 'rootContext' must not be null");
            }

            RuleAppendHandler appendHandler = createRuleAppendHandler();
            appendHandler.handle(resolveAppender(rootContext));

            return createActivityContext();
        } catch (Exception e) {
            throw new ActivityContextParserException("Failed to parse an ActivityContext with " + rootContext, e);
        }
    }

    @Override
    public ActivityContext parse(AspectranParameters aspectranParameters) throws ActivityContextParserException {
        try {
            if (aspectranParameters == null) {
                throw new IllegalArgumentException("Argument 'aspectranParameters' must not be null");
            }

            RuleAppendHandler appendHandler = createRuleAppendHandler();

            ParamsToRuleConverter ruleConverter = new ParamsToRuleConverter(getContextRuleAssistant());
            ruleConverter.convertAsRule(aspectranParameters);

            appendHandler.handle(null);

            return createActivityContext();
        } catch (Exception e) {
            throw new ActivityContextParserException("Failed to parse an ActivityContext with " + aspectranParameters, e);
        }
    }

    private RuleAppendHandler createRuleAppendHandler() {
        RuleAppendHandler appendHandler = new HybridRuleAppendHandler(getContextRuleAssistant(), getEncoding(), isHybridLoad());
        getContextRuleAssistant().setRuleAppendHandler(appendHandler);
        return appendHandler;
    }

}
