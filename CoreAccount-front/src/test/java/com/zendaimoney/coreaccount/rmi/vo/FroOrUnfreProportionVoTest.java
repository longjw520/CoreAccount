package com.zendaimoney.coreaccount.rmi.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import junit.framework.Assert;

import org.junit.Test;

public class FroOrUnfreProportionVoTest extends VoTest {
	@Test
	public void regex_error() {
		FroOrUnfreProportionVo froOrUnfreProportionVo = new FroOrUnfreProportionVo();
		froOrUnfreProportionVo.setAuthTeller("a");
		PartOfFroOrUnfreProportionVo partOfFroOrUnfreProportionVo = new PartOfFroOrUnfreProportionVo();
		// partOfFroOrUnfreProportionVo.setFinanceId(123L);
		partOfFroOrUnfreProportionVo.setFroOrUnfreProportion(BigDecimal.valueOf(25451.123d));
		List<PartOfFroOrUnfreProportionVo> vals = Arrays.asList(partOfFroOrUnfreProportionVo, null);
		froOrUnfreProportionVo.setFinanceIdAndRate(vals);
		froOrUnfreProportionVo.setOperateCode("111");
		froOrUnfreProportionVo.setOrgan("1111");
		froOrUnfreProportionVo.setStatus("a");
		froOrUnfreProportionVo.setOperator("a");
		Set<ConstraintViolation<FroOrUnfreProportionVo>> constraintViolations = validator.validate(froOrUnfreProportionVo);
		Assert.assertEquals(5, constraintViolations.size());
	}

	@Test
	public void regex_OK() {
		FroOrUnfreProportionVo froOrUnfreProportionVo = new FroOrUnfreProportionVo();
		froOrUnfreProportionVo.setAuthTeller("a");
		PartOfFroOrUnfreProportionVo partOfFroOrUnfreProportionVo = new PartOfFroOrUnfreProportionVo();
		partOfFroOrUnfreProportionVo.setFinanceId(123L);
		partOfFroOrUnfreProportionVo.setFroOrUnfreProportion(BigDecimal.valueOf(0.123d));
		List<PartOfFroOrUnfreProportionVo> vals = Arrays.asList(partOfFroOrUnfreProportionVo);
		froOrUnfreProportionVo.setFinanceIdAndRate(vals);
		froOrUnfreProportionVo.setOperateCode("111");
		froOrUnfreProportionVo.setOrgan("1111");
		froOrUnfreProportionVo.setStatus("1");
		froOrUnfreProportionVo.setOperator("a");
		Set<ConstraintViolation<FroOrUnfreProportionVo>> constraintViolations = validator.validate(froOrUnfreProportionVo);
		Assert.assertEquals(0, constraintViolations.size());
	}

	@Test
	public void testGetProportion() {
		FroOrUnfreProportionVo spy = new FroOrUnfreProportionVo();
		List<PartOfFroOrUnfreProportionVo> list = new ArrayList<PartOfFroOrUnfreProportionVo>();
		PartOfFroOrUnfreProportionVo vo1 = new PartOfFroOrUnfreProportionVo();
		vo1.setFinanceId(1L);
		vo1.setFroOrUnfreProportion(BigDecimal.ONE);
		PartOfFroOrUnfreProportionVo vo2 = new PartOfFroOrUnfreProportionVo();
		vo2.setFinanceId(2L);
		vo2.setFroOrUnfreProportion(BigDecimal.TEN);
		PartOfFroOrUnfreProportionVo vo3 = new PartOfFroOrUnfreProportionVo();
		vo3.setFinanceId(2L);
		vo3.setFroOrUnfreProportion(BigDecimal.ONE);

		list.add(vo3);
		list.add(vo2);
		list.add(vo1);
		spy.setFinanceIdAndRate(list);
		Map<Long, BigDecimal> result = spy.getProportion();
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(BigDecimal.ONE, result.get(1L));
		Assert.assertEquals(BigDecimal.valueOf(11), result.get(2L));
	}
}
