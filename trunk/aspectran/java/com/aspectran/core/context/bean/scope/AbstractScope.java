/**
 * 
 */
package com.aspectran.core.context.bean.scope;

/**
 * The Class AbstractScope.
 *
 * @author Gulendol
 * @since 2011. 3. 12.
 */
public class AbstractScope implements Scope {
	
	protected ScopedBeanMap scopedBeanMap = new ScopedBeanMap();

	public ScopedBeanMap getScopedBeanMap() {
		return scopedBeanMap;
	}

	public void setScopedBeanMap(ScopedBeanMap scopedBeanMap) {
		this.scopedBeanMap = scopedBeanMap;
	}
	
	public void destroy() {
		for(ScopedBean scopedBean : scopedBeanMap) {
			scopedBean.destroy();
		}
	}
	
}
