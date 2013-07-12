/**
 * 
 */
package com.aspectran.core.bean.scope;

import com.aspectran.core.bean.ScopeBeanMap;

/**
 *
 * @author Gulendol
 * @since 2011. 3. 12.
 *
 */
public class AbstractScope implements Scope {
	
	protected ScopeBeanMap instantiatedBeanMap = new ScopeBeanMap();

	public ScopeBeanMap getScopeBeanMap() {
		return instantiatedBeanMap;
	}

	public void setInstantiatedBeanMap(ScopeBeanMap instantiatedBeanMap) {
		this.instantiatedBeanMap = instantiatedBeanMap;
	}
	
	public void destroy() {
		
	}
	
}
