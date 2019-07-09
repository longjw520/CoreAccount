package com.zendaimoney.coreaccount.util;

import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.RechargeVo;
import com.zendaimoney.coreaccount.tools.JobMixin;
import com.zendaimoney.coreaccount.tools.PersonMixin;
import com.zendaimoney.utils.DateUtils;

public class JsonHelperTest {

	@Test
	public void testGetInstance() {
		Assert.assertSame(JsonHelper.getInstance(), JsonHelper.getInstance());
		Assert.assertTrue(JsonHelper.getInstance() == JsonHelper.getInstance());
	}

	@Test
	public void testtoJson() throws Exception {
		Job job = new Job();
		job.setId(12);
		job.setDoSomething("学习");
		job.setName("root");
		Person p = new Person();
		p.setAge(23);
		p.setPersonName("javase");
		job.setPerson(p);
		Map<Class<?>, Class<?>> filters = new HashMap<Class<?>, Class<?>>();
		filters.put(Job.class, JobMixin.class);
		filters.put(Person.class, PersonMixin.class);
		String json = JsonHelper.toJson(job, filters);
		assertNull(JsonHelper.getInstance().readTree(json).get("name"));
		assertNull(JsonHelper.getInstance().readTree(json).get("person").get("personName"));
	}

	@Test
	public void dateFormat() {
		String date = "2012-12-16";
		ClassForDate classForDate = new ClassForDate();
		classForDate.setD1(DateUtils.nullSafeParseDate(date, "yyyy-MM-dd"));
		classForDate.setD2(DateUtils.nullSafeParseDate(date, "yyyy-MM-dd"));
		Map<Class<?>, Class<?>> map = new HashMap<Class<?>, Class<?>>();
		map.put(ClassForDate.class, ClassForDateMixIn.class);
		String json = JsonHelper.toJson(classForDate, map);
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Assert.assertEquals(date, objectMapper.readTree(json).get("d1").asText());
			Assert.assertEquals(date + " 00:00:00", objectMapper.readTree(json).get("d2").asText());
		} catch (Exception e) {
			Assert.fail();
		}
	}


	@Test
	public void toBean() throws Exception {
		RechargeVo vo = new RechargeVo();
		vo.setRechargeCommission(new BigDecimal("0.0000001")); // pocketMoney
		Datagram d = new Datagram();
		d.setDatagramBody(vo);
		String json = JsonHelper.toJson(d);
		System.out.println(json);
	}

	@Test
	public void toBeaaan() throws Exception {

		System.out.println(BigDecimal.valueOf(0.0000001).toPlainString());
		System.out.println(BigDecimal.valueOf(0.0000001D).toPlainString());
		System.out.println(new BigDecimal("0.0000001").toPlainString());
	}
}

class Worker implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal pocketMoney;

	public BigDecimal getPocketMoney() {
		return pocketMoney;
	}

	public void setPocketMoney(BigDecimal pocketMoney) {
		this.pocketMoney = pocketMoney;
	}
}

class ClassForDate {
	private Date d1;
	private Date d2;

	public Date getD1() {
		return d1;
	}

	public void setD1(Date d) {
		this.d1 = d;
	}

	public Date getD2() {
		return d2;
	}

	public void setD2(Date d2) {
		this.d2 = d2;
	}

}

class ClassForDateMixIn extends ClassForDate {
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Override
	public Date getD1() {
		return super.getD1();
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Override
	public Date getD2() {
		return super.getD2();
	}

}

class Job {
	private String name;
	private int id;
	private String doSomething;

	private Person person;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDoSomething() {
		return doSomething;
	}

	public void setDoSomething(String doSomething) {
		this.doSomething = doSomething;
	}

}

class Person {
	private String personName;
	private int age;

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}