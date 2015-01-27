/*
 *  Copyright (c) 2008 Jeong Ju Ho, All rights reserved.
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
package com.aspectran.core.util.io;

import com.aspectran.core.var.type.ImportResourceType;

/**
 * <p>Created: 2008. 04. 24 오전 11:23:36</p>
 * 
 * @author Gulendol
 */
public abstract class AbstractImportStream {
	
	private ImportResourceType importResourceType;
	
	private long lastModified;
	
	public AbstractImportStream(ImportResourceType importResourceType) {
		this.importResourceType = importResourceType;
	}

	public long getLastModified() {
		return lastModified;
	}
	
	protected void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public ImportResourceType getImportResourceType() {
		return importResourceType;
	}
	
}
