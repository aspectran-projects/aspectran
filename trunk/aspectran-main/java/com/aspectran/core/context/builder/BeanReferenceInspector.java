package com.aspectran.core.context.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.var.rule.BeanActionRule;
import com.aspectran.core.var.rule.BeanRuleMap;
import com.aspectran.core.var.rule.ItemRule;
import com.aspectran.core.var.rule.RedirectResponseRule;
import com.aspectran.core.var.rule.TransformRule;

public class BeanReferenceInspector {

	private final Logger logger = LoggerFactory.getLogger(BeanReferenceInspector.class);
	
	private Map<String, Set<Object>> relationMap = new LinkedHashMap<String, Set<Object>>();
	
	public BeanReferenceInspector() {
	}
	
	public void putRelation(String beanId, Object rule) {
		Set<Object> ruleSet = relationMap.get(beanId);
		
		if(ruleSet == null) {
			ruleSet = new LinkedHashSet<Object>();
			ruleSet.add(rule);
			relationMap.put(beanId,  ruleSet);
		} else {
			ruleSet.add(rule);
		}
	}
	
	public void inpect(BeanRuleMap beanRuleMap) {
		List<String> unknownBeanIdList = new ArrayList<String>();
		
		for(Map.Entry<String, Set<Object>> entry : relationMap.entrySet()) {
			if(!beanRuleMap.containsKey(entry.getKey())) {
				String beanId = entry.getKey();
				unknownBeanIdList.add(beanId);
				
				Set<Object> set = entry.getValue();
				
				for(Object o : set) {
					String ruleName;
					
					if(o instanceof BeanActionRule) {
						ruleName = "beanActionRule";
					} else if(o instanceof ItemRule) {
						ruleName = "itemRule";
					} else if(o instanceof TransformRule) {
						ruleName = "transformRule";
					} else if(o instanceof RedirectResponseRule) {
						ruleName = "redirectResponseRule";
					} else {
						ruleName = "rule";
					}
					
					logger.error("Cannot resolve reference to bean '" + beanId + "' on " + ruleName + " " + o);
				}
			}
		}
		
		if(unknownBeanIdList.size() > 0) {
			String[] beanIds = unknownBeanIdList.toArray(new String[unknownBeanIdList.size()]);

			for(String beanId : beanIds) {
				relationMap.remove(beanId);
			}
			
			BeanReferenceException bre = new BeanReferenceException(beanIds);
			bre.setBeanReferenceInspector(this);
			
			throw bre;
		}
	}
	
	public Map<String, Set<Object>> getRelationMap() {
		return relationMap;
	}
	
}
