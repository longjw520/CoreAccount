package com.zendaimoney.coreaccount.front.filter;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.validation.Validator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springside.modules.utils.Reflections;

@ContextConfiguration(locations = { "/applicationContext-test.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class BaseTest<T extends DatagramFilter> {

	private Class<T> target;
	protected DatagramFilter datagramFilter;
	@Inject
	protected Validator validator;

	@Before
	public void SetUp() throws Exception {
		target = Reflections.getSuperClassGenricType(getClass());
		datagramFilter = (DatagramFilter) target.newInstance();
		Field f = null;
		try {
			f = datagramFilter.getClass().getDeclaredField("validator");
			f.setAccessible(true);
			f.set(datagramFilter, validator);
		} catch (NoSuchFieldException e) {
		}
	}
}
