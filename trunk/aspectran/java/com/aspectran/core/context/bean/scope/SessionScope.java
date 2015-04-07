/**
 * 
 */
package com.aspectran.core.context.bean.scope;

import com.aspectran.core.util.logging.Log;
import com.aspectran.core.util.logging.LogFactory;

/**
 * The Class SessionScope.
 *
 * @author Gulendol
 * @since 2011. 3. 12.
 */
public class SessionScope extends AbstractScope implements Scope {

	private final Log log = LogFactory.getLog(SessionScope.class);
	
	public void destroy() {
		if(log.isDebugEnabled())
			log.debug("destroy session-scoped beans " + scopedBeanMap);
		
		super.destroy();
	}
}
