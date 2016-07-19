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
package com.aspectran.core.activity.response;

import com.aspectran.core.activity.response.transform.JsonTransform;
import com.aspectran.core.activity.response.transform.TextTransform;
import com.aspectran.core.activity.response.transform.XmlTransform;
import com.aspectran.core.context.rule.TransformRule;
import com.aspectran.core.context.rule.type.TransformType;

/**
 * The Class TransformResponseFactory.
 * 
 * @author Juho Jeong
 * @since 2011. 3. 12.
 */
public class TransformResponseFactory {

	public static Response getResponse(TransformRule transformRule) {
		if(transformRule == null) {
			throw new IllegalArgumentException("transformRule must not be null.");
		}
		
		TransformType type = transformRule.getTransformType();
		Response res;
		
		if(type == TransformType.XML) {
			res = new XmlTransform(transformRule);
		} else if(type == TransformType.XSL) {
			res = new XmlTransform(transformRule);
		} else if(type == TransformType.JSON) {
			res = new JsonTransform(transformRule);
		} else if(type == TransformType.TEXT) {
			res = new TextTransform(transformRule);
		} else {
			throw new IllegalArgumentException("The specified tranform-type is not valid. transformRule " + transformRule);
		}
		
		return res;
	}
	
}
