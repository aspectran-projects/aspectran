/*
 * Copyright (c) 2008-2018 The Aspectran Project
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
package com.aspectran.core.component.aspect.pointcut;

import com.aspectran.core.context.rule.PointcutPatternRule;
import com.aspectran.core.util.ConcurrentReferenceHashMap;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pointcut using Regular Expression Matching to identify joinpoints.
 */
public class RegexpPointcut extends AbstractPointcut {

    private final Map<String, Pattern> cache = new ConcurrentReferenceHashMap<>(256);

    public RegexpPointcut(List<PointcutPatternRule> pointcutPatternRuleList) {
        super(pointcutPatternRuleList);
    }

    @Override
    public boolean patternMatches(String regex, String compareString) {
        Pattern pattern = cache.get(regex);
        if (pattern == null) {
            pattern = Pattern.compile(regex);
            Pattern pattern2 = cache.putIfAbsent(regex, pattern);
            if (pattern2 != null) {
                pattern = pattern2;
            }
        }
        Matcher matcher = pattern.matcher(compareString);
        return matcher.matches();
    }

    @Override
    public boolean patternMatches(String regex, String compareString, char separator) {
        return patternMatches(regex, compareString);
    }

    @Override
    public void clear() {
        cache.clear();
    }

}
