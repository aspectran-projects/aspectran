/*
 * Copyright (c) 2008-2021 The Aspectran Project
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
package com.aspectran.core.context.rule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The Class ItemRuleMap.
 * 
 * <p>Created: 2008. 03. 29 PM 5:00:20</p>
 */
public class ItemRuleMap extends LinkedHashMap<String, ItemRule> {

    private static final long serialVersionUID = 192817512158305803L;

    private String profile;

    private List<ItemRuleMap> candidates;

    public ItemRuleMap() {
        super();
    }

    public ItemRuleMap(ItemRuleMap itemRuleMap) {
        super(itemRuleMap);
    }

    /**
     * Adds a item rule.
     * @param itemRule the item rule
     * @return the item rule
     */
    public ItemRule putItemRule(ItemRule itemRule) {
        if (itemRule.isAutoNamed()) {
            autoNaming(itemRule);
        }
        return put(itemRule.getName(), itemRule);
    }

    /**
     * Auto-naming for unnamed item.
     * Auto-naming if did not specify the name of the item.
     * @param itemRule the item rule
     */
    private void autoNaming(ItemRule itemRule) {
        itemRule.setName("item#" + size());
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public List<ItemRuleMap> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<ItemRuleMap> candidates) {
        this.candidates = candidates;
    }

    public List<ItemRuleMap> addCandidate(ItemRuleMap itemRuleMap) {
        if (candidates == null) {
            candidates = new ArrayList<>();
        }
        candidates.add(itemRuleMap);
        return candidates;
    }

}
