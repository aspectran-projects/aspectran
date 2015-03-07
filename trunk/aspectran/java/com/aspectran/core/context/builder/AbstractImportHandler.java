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
package com.aspectran.core.context.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Translet Map Parser.
 * 
 * <p>Created: 2008. 06. 14 오전 4:39:24</p>
 */
public abstract class AbstractImportHandler implements ImportHandler {
	
	private List<Importable> pendingList;
	
	public AbstractImportHandler() {
	}
	
	public void pending(Importable importable) {
		if(pendingList == null)
			pendingList = new ArrayList<Importable>();
		
		pendingList.add(importable);
	}
	
	protected void handle() throws Exception {
		if(pendingList != null) {
			List<Importable> pendedList = pendingList;
			pendingList = null;
			
			for(Importable imp : pendedList) {
				handle(imp);
			}
		}
	}
	
	abstract public void handle(Importable importable) throws Exception;

}
