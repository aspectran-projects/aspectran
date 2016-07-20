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
package com.aspectran.core.adapter;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.aspectran.core.activity.request.parameter.FileParameter;
import com.aspectran.core.context.rule.type.MethodType;

/**
 * The Interface RequestAdapter.
 *
 * @author Juho Jeong
 * @since 2011. 3. 13.
 */
public interface RequestAdapter {
	
	/**
	 * Gets the Adaptee object.
	 *
	 * @param <T> the generic type
	 * @return the Adaptee object
	 */
	<T> T getAdaptee();

	/**
	 * Gets the request method.
	 *
	 * @return the request method
	 */
	MethodType getRequestMethod();

	/**
	 * Returns the name of the character encoding used in the body of this request.
	 * 
	 * @return a {@code String} containing the name of the character encoding,
	 * 			or {@code null} if the request does not specify a character encoding
	 */
	String getCharacterEncoding();
	
	/**
	 * Overrides the name of the character encoding used in the body of this request.
	 * This method must be called prior to reading request parameters
	 * or reading input using getReader(). Otherwise, it has no effect. 
	 *
	 * @param characterEncoding a {@code String} containing the name of the character encoding. 
	 * @throws UnsupportedEncodingException if the specified encoding is invalid
	 */
	void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException;
	
	/**
	 * Returns the value of an activity's request parameter as a {@code String},
	 * or {@code null} if the parameter does not exist.
	 *
	 * @param name a {@code String} specifying the name of the parameter
	 * @return a {@code String} representing the
	 *			single value of the parameter
	 * @see #getParameterValues
	 */
	String getParameter(String name);

	/**
	 * Returns an array of {@code String} objects containing all
	 * of the values the given activity's request parameter has,
	 * or {@code null} if the parameter does not exist.
	 *
	 * @param name a {@code String} specifying the name of the parameter
	 * @return an array of {@code String} objects
	 *			containing the parameter's values
	 * @see #getParameter
	 */
	String[] getParameterValues(String name);

	/**
	 * Returns an {@code Enumeration} of {@code String} objects containing
	 * the names of the parameters contained in this request.
	 * If the request has no parameters, the method returns an empty {@code Enumeration}.
	 *
	 * @return an {@code Enumeration} of {@code String} objects, each {@code String}
	 * 			containing the name of a request parameter;
	 * 			or an empty {@code Enumeration} if the request has no parameters
	 */
	Enumeration<String> getParameterNames();

	/**
	 * Sets the value to the parameter with the given name.
	 *
	 * @param name a {@code String} specifying the name of the parameter
	 * @param value a {@code String} representing the
	 *			single value of the parameter
	 * @see #setParameter(String, String[])
	 */
	void setParameter(String name, String value);

	/**
	 * Sets the value to the parameter with the given name.
	 *
	 * @param name a {@code String} specifying the name of the parameter
	 * @param values an array of {@code String} objects
	 *			containing the parameter's values
	 * @see #setParameter
	 */
	void setParameter(String name, String[] values);

	/**
	 * Returns a {@code FileParameter} object as a given activity's request parameter name,
	 * or {@code null} if the parameter does not exist.
	 *
	 * @param name a {@code String} specifying the name of the file parameter
	 * @return a {@code FileParameter} representing the
	 *			single value of the parameter
	 * @see #getFileParameterValues
	 */
	FileParameter getFileParameter(String name);
	
	/**
	 * Returns an array of {@code FileParameter} objects containing all
	 * of the values the given activity's request parameter has,
	 * or {@code null} if the parameter does not exist.
	 *
	 * @param name a {@code String} specifying the name of the file parameter
	 * @return an array of {@code FileParameter} objects
	 *			containing the parameter's values
	 * @see #getFileParameter
	 */
	FileParameter[] getFileParameterValues(String name);

	/**
	 * Returns an {@code Enumeration} of {@code String} objects containing
	 * the names of the file parameters contained in this request.
	 * If the request has no parameters, the method returns an empty {@code Enumeration}.
	 *
	 * @return an {@code Enumeration} of {@code String} objects, each {@code String}
	 * 			containing the name of a file parameter;
	 * 			or an empty {@code Enumeration} if the request has no file parameters
	 */
	Enumeration<String> getFileParameterNames();

	/**
	 * Sets the {@code FileParameter} object to the file parameter with the given name.
	 *
	 * @param name a {@code String} specifying the name of the file parameter
	 * @param fileParameter a {@code FileParameter} representing the
	 *			single value of the parameter
	 * @see #setFileParameter(String, FileParameter[])
	 */
	void setFileParameter(String name, FileParameter fileParameter);

	/**
	 * Sets the value to the file parameter with the given name.
	 *
	 * @param name a {@code String} specifying the name of the file parameter
	 * @param fileParameters an array of {@code FileParameter} objects
	 *			containing the file parameter's values
	 * @see #setFileParameter
	 */
	void setFileParameter(String name, FileParameter[] fileParameters);

	/**
	 * Removes the file parameter with the specified name.
	 *
	 * @param name a {@code String} specifying the name of the file parameter
	 * @return the removed file parameters
	 */
	FileParameter[] removeFileParameter(String name);
	
	/**
	 * Returns the value of the named attribute as a given type,
	 * or {@code null} if no attribute of the given name exists.
	 *
	 * @param <T> the generic type
	 * @param name a {@code String} specifying the name of the attribute
	 * @return an {@code Object} containing the value of the attribute,
	 * 			or {@code null} if the attribute does not exist
	 */
	<T> T getAttribute(String name);
	
	/**
	 * Stores an attribute in this request.
	 *
	 * @param name specifying the name of the attribute
	 * @param value the {@code Object} to be stored
	 */
	void setAttribute(String name, Object value);
	
	/**
	 * Returns an {@code Enumeration} containing the
	 * names of the attributes available to this request.
	 * This method returns an empty {@code Enumeration}
	 * if the request has no attributes available to it.
	 *
	 * @return the attribute names
	 */
	Enumeration<String> getAttributeNames();
	
	/**
	 * Removes an attribute from this request.
	 *
	 * @param name a {@code String} specifying the name of the attribute to remove
	 */
	void removeAttribute(String name);

	/**
	 * Return a mutable Map of the request parameters,
	 * with parameter names as map keys and parameter values as map values.
	 * If the parameter value type is the String then map value will be of type String.
	 * If the parameter value type is the String array then map value will be of type String array.
	 *
	 * @return the parameter map
	 * @since 1.4.0
	 */
	Map<String, Object> getParameterMap();

	/**
	 * Fills all parameters to the specified map.
	 *
	 * @param parameterMap the parameter map
	 * @since 2.0.0
	 */
	void fillPrameterMap(Map<String, Object> parameterMap);
	
	/**
	 * Return a mutable {@code Map} of the request attributes,
	 * with attribute names as map keys and attribute value as map value.
	 *
	 * @return the attribute map
	 * @since 2.0.0
	 */
	Map<String, Object> getAttributeMap();

	/**
	 * Fills all attributes to the specified map.
	 *
	 * @param attributeMap the attribute map
	 * @since 2.0.0
	 */
	void fillAttributeMap(Map<String, Object> attributeMap);
	
	/**
	 * Returns whether request header has exceed the maximum length.
	 *
	 * @return true, if max length exceeded
	 */
	boolean isMaxLengthExceeded();

	/**
	 * Sets whether request header has exceed the maximum length.
	 *
	 * @param maxLengthExceeded whether the max length exceeded
	 */
	void setMaxLengthExceeded(boolean maxLengthExceeded);

	/**
	 * Returns the preferred {@code Locale}.
	 *
	 * @return a preferred {@code Locale}
     */
	Locale getLocale();

	/**
	 * Sets the preferred {@code Locale}.
	 *
	 * @param locale a given {@code Locale}
	 */
	void setLocale(Locale locale);

	/**
	 * Returns the preferred {@code TimeZone}.
	 *
	 * @return a preferred {@code TimeZone}
	 */
	TimeZone getTimeZone();

	/**
	 * Sets the preferred {@code TimeZone}.
	 *
	 * @param timeZone a given {@code TimeZone}
	 */
	void setTimeZone(TimeZone timeZone);

}
