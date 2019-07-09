package com.zendaimoney.coreaccount.service.message.reverse.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.dao.LedgerFinanceDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.WorkFlow;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.coreaccount.rmi.vo.ReverseVo;

@RunWith(PowerMockRunner.class)
public class RFroOrUnfreProportionImplTest {
	@InjectMocks
	private RFroOrUnfreProportionImpl rFroOrUnfreProportionImpl;
	@Mock
	private LedgerFinanceDao ledgerFinanceDao;

	@Mock
	private BusinessInfoDao businessInfoDao;
	@Mock
	private WorkFlowDao workFlowDao;
	@Mock
	private SequenceDao sequenceDao;

	@Test
	public void testProcessTx() throws Exception {
		Datagram datagram = new Datagram();
		ReverseVo vo = new ReverseVo();
		datagram.setDatagramBody(vo);
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("messageSequence11");
		datagram.setDatagramHeader(header);
		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);
		when(businessInfoDao.findUniqueBy("messageSequence", vo.getReverseMessageSequence())).thenReturn(businessInfo);
		List<WorkFlow> list = new ArrayList<WorkFlow>();
		WorkFlow f1 = new WorkFlow();
		f1.setStartValue("10");
		f1.setEndValue("110");
		f1.setObjectId(1L);
		f1.setMemo("DEBT_PROPORTION");
		f1.setTradeValue("1");
		f1.setId(24L);
		list.add(f1);
		when(workFlowDao.findBy("businessId", businessInfo.getId())).thenReturn(list);
		BusinessInfo info = new BusinessInfo();
		info.setId(132L);
		info.setBusinessTypeId(2L);
		when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(info);
		LedgerFinance finance = new LedgerFinance();
		finance.setDebtProportion(new BigDecimal("0.21"));
		finance.setFrozenPorportion(new BigDecimal("0.11"));
		when(sequenceDao.nextWorkFlowNo()).thenReturn(new Random().nextLong());
		when(ledgerFinanceDao.get(f1.getObjectId())).thenReturn(finance);
		rFroOrUnfreProportionImpl.processTx(datagram);
		verify(workFlowDao, times(1)).save(Mockito.any(WorkFlow.class));
	}
}
