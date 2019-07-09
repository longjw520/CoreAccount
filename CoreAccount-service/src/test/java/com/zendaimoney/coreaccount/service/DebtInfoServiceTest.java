package com.zendaimoney.coreaccount.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springside.modules.orm.Page;

import com.zendaimoney.coreaccount.dao.DebtInfoDao;
import com.zendaimoney.coreaccount.entity.Debt;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryAccountsReceivableAndPayableVo;


/**
 * 债务服务  测试
 * 
 * @author ShiMing
 */
@RunWith(PowerMockRunner.class)
public class DebtInfoServiceTest {

	@InjectMocks
	private DebtInfoService debtInfoService;

	@Mock
	private DebtInfoDao debtInfoDao;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testQueryDebtInfo() {
		QueryAccountsReceivableAndPayableVo queryAccountsReceivableAndPayableVo = new QueryAccountsReceivableAndPayableVo();
		Datagram datagram = new Datagram();
		datagram.setDatagramBody(queryAccountsReceivableAndPayableVo);
		Mockito.when(debtInfoDao.queryAccountsReceivableAndPayablePage(queryAccountsReceivableAndPayableVo)).thenReturn(new Page());
		Page<Debt> result = debtInfoService.queryDebtInfo(queryAccountsReceivableAndPayableVo);
		Assert.assertNotNull(result);
	}
}
