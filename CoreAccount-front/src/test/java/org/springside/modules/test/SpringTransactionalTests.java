package org.springside.modules.test;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Before;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springside.modules.test.data.Fixtures;

public abstract class SpringTransactionalTests extends
		AbstractTransactionalJUnit4SpringContextTests {
	
	@Inject
	protected DataSource dataSource;
	
	@Before
	public void loadDataFromXml() throws Exception {
		Fixtures.reloadData(dataSource, getDataFilePath());
	}
	
	/**
	 * 获取xml数据文件的路径
	 * @return
	 */
	protected abstract String getDataFilePath();
}
