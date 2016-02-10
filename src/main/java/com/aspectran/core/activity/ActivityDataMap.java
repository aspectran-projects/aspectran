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
package com.aspectran.core.activity;

import java.util.HashMap;

import com.aspectran.core.activity.process.result.ActionResult;
import com.aspectran.core.activity.process.result.ContentResult;
import com.aspectran.core.adapter.RequestAdapter;

/**
 * The Class ActivityResultDataMap.
 */
public class ActivityDataMap extends HashMap<String, Object> {

	/** @serial */
	private static final long serialVersionUID = -4557424414862800204L;

	protected final Activity activity;

	protected RequestAdapter requestAdapter;

	/**
	 * Instantiates a new activity data map.
	 *
	 * @param activity the activity
	 */
	public ActivityDataMap(Activity activity) {
		this(activity, false);
	}

	/**
	 * Instantiates a new activity data map.
	 *
	 * @param activity the activity
	 * @param prefill whether the data pre-fill
	 */
	public ActivityDataMap(Activity activity, boolean prefill) {
		this.activity = activity;
		this.requestAdapter = activity.getRequestAdapter();
		
		if(prefill)
			prefillData();
	}
	
	private void prefillData() {
		if(requestAdapter != null) {
			requestAdapter.fillPrameterMap(this);
			requestAdapter.fillAttributeMap(this);
		}
		
		if(activity.getProcessResult() != null) {
			for(ContentResult cr : activity.getProcessResult()) {
				for(ActionResult ar : cr) {
					if(ar.getActionId() != null) {
						put(ar.getActionId(), ar.getResultValue());
					}
				}
			}
		}
	}
	
	@Override
	public Object get(Object key) {
		Object value = super.get(key);
		if(value != null)
			return value;

		if(key != null) {
			String name = key.toString();

			value = getActionResultWithoutCache(name);
			if(value != null) {
				put(name, value);
				return value;
			}

			value = getAttributeWithoutCache(name);
			if(value != null) {
				put(name, value);
				return value;
			}

			value = getParameterWithoutCache(name);
			if(value != null) {
				put(name, value);
				return value;
			}
		}

		return null;
	}

	public Object getParameterWithoutCache(String name) {
		if(requestAdapter != null) {
			return requestAdapter.getParameter(name);
		}
		return null;
	}

	public Object getAttributeWithoutCache(String name) {
		if(requestAdapter != null) {
			return requestAdapter.getAttribute(name);
		}
		return null;
	}

	public Object getActionResultWithoutCache(String name) {
		if(activity.getProcessResult() == null)
			return null;
		return activity.getProcessResult().getResultValue(name);
	}

}
