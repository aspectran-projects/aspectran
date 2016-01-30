/**
 * Copyright 2008-2016 Juho Jeong
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
package com.aspectran.core.context.template.engine.freemarker.directive;

import freemarker.template.TemplateModelException;

import java.util.Map;

/**
 * The Class TrimDirective.
 *
 * <dl>
 * <dt>Basically, this trims the body string(removing leading and tailing spaces).
 * <dt>If the result of trimming is empty, it will return just empty string.
 * <dt>prefix="string" <dd>If the result of trimming is not empty, prefix "string" to the result.
 * <dt>suffix="string" <dd>If the result of trim is not empty, suffix "string" to the result.
 * <dt>deprefixes=["string1", "string2", ...] <dd>If the result of trimming is not empty,
 *      the first appearing string in the leading of the result will be removed.
 * <dt>desuffixes=["string1", "string2", ...] <dd>If the result of trimming is not empty,
 *      the first appearing string in the tail of the result will be removed.
 *</dl>
 *
 * <p>Created: 2016. 1. 29.</p>
 */
public class TrimDirective extends AbstractTrimDirectiveModel {

    public static final String PREFIX_PARAM_NAME = "prefix";

    public static final String SUFFIX_PARAM_NAME = "suffix";

    public static final String DEPREFIX_PARAM_NAME = "deprefixes";

    public static final String DESUFFIX_PARAM_NAME = "desuffixes";

    public static final String CASE_SENSITIVE_PARAM_NAME = "caseSensitive";

    public static final String TRIM_DIRECTIVE_NAME = "trim";

    public TrimDirective() {
        super(TRIM_DIRECTIVE_NAME);
    }

    @Override
    protected Trimmer getTrimmer(Map params) throws TemplateModelException {
        String prefix = parseStringParameter(params, PREFIX_PARAM_NAME);
        String suffix = parseStringParameter(params, SUFFIX_PARAM_NAME);
        String[] deprefixes = parseSequenceParameter(params, DEPREFIX_PARAM_NAME);
        String[] desuffixes = parseSequenceParameter(params, DESUFFIX_PARAM_NAME);
        String caseSensitive = parseStringParameter(params, CASE_SENSITIVE_PARAM_NAME);

        Trimmer trimmer = new Trimmer();
        trimmer.setPrefix(prefix);
        trimmer.setSuffix(suffix);
        trimmer.setDeprefixes(deprefixes);
        trimmer.setDesuffixes(desuffixes);
        trimmer.setCaseSensitive(Boolean.parseBoolean(caseSensitive));

        return trimmer;
    }

}
