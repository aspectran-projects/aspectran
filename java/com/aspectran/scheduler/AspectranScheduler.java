/*
 * Copyright 2008-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aspectran.scheduler;

/**
 * The Interface AspectranScheduler.
 */
public interface AspectranScheduler {

	public int getStartDelaySeconds();
	
	public void setStartDelaySeconds(int startDelaySeconds);
	
	public boolean isWaitOnShutdown();
	
	public void setWaitOnShutdown(boolean waitOnShutdown);
	
	public void startup() throws Exception;
	
	public void startup(int delaySeconds) throws Exception;
	
	public void shutdown() throws Exception;
	
	public void shutdown(boolean waitForJobsToComplete) throws Exception;
	
	public void pause(String schedulerId) throws Exception;
	
	public void resume(String schedulerId) throws Exception;
	
}
