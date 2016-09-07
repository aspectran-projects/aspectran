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
package com.aspectran.console.adapter;

import java.util.Random;

import com.aspectran.core.adapter.BasicSessionAdapter;

/**
 * The Class BasicSessionAdapter.
 * 
 * @since 2.3.0
 */
public class ConsoleSessionAdapter extends BasicSessionAdapter {
	
	private final long creationTime = System.currentTimeMillis();

	private final String id = generateSessionId();
	
	/**
	 * Instantiates a new BasicApplicationAdapter.
	 */
	public ConsoleSessionAdapter() {
		super(null);
	}
	
	/**
	 * Instantiates a new BasicApplicationAdapter.
	 *
	 * @param adaptee the adaptee object
	 */
	public ConsoleSessionAdapter(Object adaptee) {
		super(adaptee);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public long getLastAccessedTime() {
		return -1L;
	}

	@Override
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	public void invalidate() {
		// nothing to do
	}
	
	private String generateSessionId() {
		long seed = creationTime;
		seed ^= Runtime.getRuntime().freeMemory();

		Random rnd = new Random(seed);
		
		return Long.toString(Math.abs(rnd.nextLong()),16);
	} 

}
