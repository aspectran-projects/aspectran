/**
 * 
 */
package com.aspectran.core.context.bean;


/**
 *
 * @author Gulendol
 *
 * <p>Created: 2012. 11. 9. 오전 11:36:47</p>
 *
 */
public interface BeanRegistry {

	public <T> T getBean(String id);
	
}
