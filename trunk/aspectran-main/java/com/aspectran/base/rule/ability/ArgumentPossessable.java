/**
 * 
 */
package com.aspectran.base.rule.ability;

import com.aspectran.base.rule.ItemRule;
import com.aspectran.base.rule.ItemRuleMap;

/**
 *
 * @author Gulendol
 * @since 2011. 2. 21.
 *
 */
public interface ArgumentPossessable {

	/**
	 * Gets the argument item rule map.
	 *
	 * @return the argument item rule map
	 */
	public ItemRuleMap getArgumentItemRuleMap();
	
	/**
	 * Sets the argument item rule map.
	 *
	 * @param argumentItemRuleMap the new argument item rule map
	 */
	public void setArgumentItemRuleMap(ItemRuleMap argumentItemRuleMap);

	/**
	 * Adds the item rule for argument.
	 * 
	 * @param parameterRule the item rule for argument
	 */
	public void addArgumentItemRule(ItemRule argumentItemRule);

}
