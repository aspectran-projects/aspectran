/*
 *  Copyright (c) 2010 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.scheduler.activity;

import com.aspectran.core.activity.AbstractCoreTranslet;
import com.aspectran.core.activity.CoreActivity;

/**
 * The Class AspectranWebTranslet.
 * 
 * <p>Created: 2013. 11. 18 오후 3:40:48</p>
 */
public class SchedulingTransletImpl extends AbstractCoreTranslet implements SchedulingTranslet {
	
	/**
	 * Instantiates a new aspectran web translet.
	 *
	 * @param activity the activity
	 */
	public SchedulingTransletImpl(CoreActivity activity, boolean aspectAdvicable) {
		super(activity, aspectAdvicable);
	}
	
}
