package com.zendaimoney.coreaccount.util;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;

public class ObjectHelperTest {

	@Test
	public void testCopy() {
		Teacher t = new Teacher();
		t.setId(43);
		t.setName("admin");
		t.setBirthday("2011-05-10");
		Student s = new Student();
		ObjectHelper.copy(t, s);
		Assert.assertEquals("admin", s.getName());
		Assert.assertEquals("2011-05-10", new SimpleDateFormat("yyyy-MM-dd").format(s.getBirthday()));
	}

	@Test
	public void testCopy_ignore() {
		Teacher t = new Teacher();
		t.setId(43);
		t.setName("admin");
		t.setBirthday("2011-05-10");
		Student s = new Student();
		ObjectHelper.copy(t, s, "name");
		Assert.assertNull(s.getName());
		Assert.assertEquals("2011-05-10", new SimpleDateFormat("yyyy-MM-dd").format(s.getBirthday()));
	}

	@Test
	public void testCopy_noParent() {
		SmallStudent s = new SmallStudent();
		s.setName("a");
		Teacher teacher = new Teacher();

		ObjectHelper.copy(s, teacher);
		Assert.assertEquals("a", teacher.getName());
	}
}

class Student extends Parent implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Date birthday;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

}

class SmallStudent {

	private String name;
	private Date birthday;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

}

class Parent {
	private String pid;

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}

class Teacher extends Parent implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	@DateTimeFormat
	private String birthday;
	private String memo;

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

}
