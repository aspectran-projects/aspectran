/*
 *  Copyright (c) 2008 Jeong Ju Ho, All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.aspectran.core.activity.process.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectran.core.activity.CoreActivity;
import com.aspectran.core.activity.process.ActionList;
import com.aspectran.core.context.aspect.AspectAdviceRuleRegistry;
import com.aspectran.core.var.ValueMap;
import com.aspectran.core.var.rule.EchoActionRule;
import com.aspectran.core.var.token.ItemTokenExpression;
import com.aspectran.core.var.token.ItemTokenExpressor;
import com.aspectran.core.var.type.ActionType;

/**
 * <p>Created: 2008. 03. 22 오후 5:50:44</p>
 */
public class EchoAction extends AbstractAction implements Executable {

	private final Logger logger = LoggerFactory.getLogger(EchoAction.class);
	
	private final EchoActionRule echoActionRule;
	
	/**
	 * Instantiates a new echo action.
	 * 
	 * @param echoActionRule the echo action rule
	 * @param parent the parent
	 */
	public EchoAction(EchoActionRule echoActionRule, ActionList parent) {
		super(echoActionRule.getActionId(), parent);
		this.echoActionRule = echoActionRule;
	}
	
	/* (non-Javadoc)
	 * @see org.jhlabs.translets.engine.process.action.Executable#execute(org.jhlabs.translets.action.Translet)
	 */
	public Object execute(CoreActivity activity) throws Exception {
		if(echoActionRule.getItemRuleMap() == null)
			return null;
		
		try {
			ItemTokenExpressor expressor = new ItemTokenExpression(activity);
			ValueMap valueMap = expressor.express(echoActionRule.getItemRuleMap());
			
			return valueMap;
		} catch(Exception e) {
			logger.error("action execution error: echoActionRule " + echoActionRule + " Cause: " + e.toString());
			throw e;
		}
	}
	
	/**
	 * Gets the echo action rule.
	 * 
	 * @return the echoActionRule
	 */
	public EchoActionRule getEchoActionRule() {
		return echoActionRule;
	}

	/* (non-Javadoc)
	 * @see org.jhlabs.translets.engine.process.action.Executable#getId()
	 */
	public String getActionId() {
		return echoActionRule.getActionId();
	}

	/* (non-Javadoc)
	 * @see org.jhlabs.translets.engine.process.action.Executable#isHidden()
	 */
	public boolean isHidden() {
		return echoActionRule.getHidden();
	}
	
	public ActionType getActionType() {
		return ActionType.ECHO;
	}
	
	public AspectAdviceRuleRegistry getAspectAdviceRuleRegistry() {
		return echoActionRule.getAspectAdviceRuleRegistry();
	}
	
	public void setAspectAdviceRuleRegistry(AspectAdviceRuleRegistry aspectAdviceRuleRegistry) {
		echoActionRule.setAspectAdviceRuleRegistry(aspectAdviceRuleRegistry);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{fullActionId=").append(fullActionId);
		sb.append(", actionType=").append(getActionType());
		sb.append(", echoActionRule=").append(echoActionRule.toString());
		sb.append("}");
		
		return sb.toString();
	}
}
