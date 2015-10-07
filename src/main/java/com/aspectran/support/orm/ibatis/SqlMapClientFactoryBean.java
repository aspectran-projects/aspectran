/*
 * Copyright 2015 the original author or authors.
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
package com.aspectran.support.orm.ibatis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.aspectran.core.activity.Translet;
import com.aspectran.core.context.bean.ablility.FactoryBean;
import com.aspectran.core.context.bean.ablility.InitializableTransletBean;
import com.aspectran.core.util.Assert;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
*
* @author Juho Jeong
*
* <p>Created: 2015. 04. 03</p>
*
*/
public class SqlMapClientFactoryBean implements InitializableTransletBean, FactoryBean<SqlMapClient> {
	
	private String configLocation;

	private Properties sqlMapClientProperties;

	private SqlMapClient sqlMapClient;

	public SqlMapClientFactoryBean() {
	}

	/**
	 * Set the location of the iBATIS SqlMapClient config file.
	 * A typical value is "/WEB-INF/sql-map-config.xml".
	 */
	public void setConfigLocation(String configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Set optional properties to be passed into the SqlMapClientBuilder, as
	 * alternative to a <code>&lt;properties&gt;</code> tag in the sql-map-config.xml
	 * file. Will be used to resolve placeholders in the config file.
	 * @see #setConfigLocation
	 * @see com.ibatis.sqlmap.client.SqlMapClientBuilder#buildSqlMapClient(java.io.Reader, java.util.Properties)
	 */
	public void setSqlMapClientProperties(Properties sqlMapClientProperties) {
		this.sqlMapClientProperties = sqlMapClientProperties;
	}

	/* (non-Javadoc)
	 * @see com.aspectran.core.context.bean.ablility.InitializableTransletBean#initialize(com.aspectran.core.activity.Translet)
	 */
	public void initialize(Translet translet) throws Exception {
		Assert.notNull(configLocation, "Property 'configLocation' is required");

		File file = translet.getApplicationAdapter().toRealPathAsFile(configLocation);
		InputStream is = new FileInputStream(file);

		buildSqlMapClient(is);
	}
	
	public void buildSqlMapClient(InputStream is) throws Exception {
		if(sqlMapClientProperties != null)
			sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(new InputStreamReader(is), sqlMapClientProperties);
		else
			sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(new InputStreamReader(is));
	}

	public SqlMapClient getObject() {
		return this.sqlMapClient;
	}
	
}