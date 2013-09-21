package com.aspectran.support.orm.ibatis;

import java.io.InputStream;

import com.aspectran.core.util.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 *
 * @author Gulendol
 *
 * <p>Created: 2008. 05. 14 오후 7:52:29</p>
 *
 */
public class SqlMapConfig {
	
	private SqlMapClient sqlMapClient;

	public SqlMapConfig(String resource) {
		buildSqlMapClient(resource);
	}
	
	protected void buildSqlMapClient(String resource) {
		try{
			InputStream is = Resources.getResourceAsStream(resource);
			sqlMapClient = SqlMapClientBuilder.buildSqlMapClient(is);
		} catch(Exception e) {
			throw new RuntimeException("Error initializing SqlMapConfig class", e);
		}
	}
	
	public SqlMapClient getSqlMapClient(){
		return sqlMapClient;
	}
}