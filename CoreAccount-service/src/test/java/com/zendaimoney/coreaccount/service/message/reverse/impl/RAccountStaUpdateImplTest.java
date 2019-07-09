package com.zendaimoney.coreaccount.service.message.reverse.impl;

import static com.zendaimoney.coreaccount.constants.Constants.LEDGER_FINANCE;
import static com.zendaimoney.coreaccount.constants.Constants.LEDGER_LOAN;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.BusinessInfoDao;
import com.zendaimoney.coreaccount.dao.LedgerFinanceDao;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.entity.BusinessInfo;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.WorkFlow;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.coreaccount.rmi.vo.ReverseVo;
import com.zendaimoney.exception.BusinessException;

@RunWith(PowerMockRunner.class)
public class RAccountStaUpdateImplTest {

	@InjectMocks
	private RAccountStaUpdateImpl rAccountStaUpdateImpl;
	@Mock
	private LedgerFinanceDao ledgerFinanceDao;

	@Mock
	private BusinessInfoDao businessInfoDao;
	@Mock
	private WorkFlowDao workFlowDao;
	@Mock
	private SequenceDao sequenceDao;
	@Mock
	private LedgerLoanDao ledgerLoanDao;

	@Test
	public void testProcessTx_ledgerFinance() {
		Datagram datagram = new Datagram();
		ReverseVo vo = new ReverseVo();
		datagram.setDatagramBody(vo);
		vo.setReverseMessageSequence("324342");
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("messageSequence11");
		datagram.setDatagramHeader(header);

		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);

		WorkFlow f1 = getWorkFlow(LEDGER_FINANCE);

		when(businessInfoDao.findUniqueBy("messageSequence", vo.getReverseMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.findUniqueBy("businessId", 13L)).thenReturn(f1);
		when(sequenceDao.nextWorkFlowNo()).thenReturn(100L);
		//when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.workFlowSave(businessInfo.getId(), null, 1L, "2", null, "1", 100l, LEDGER_FINANCE, 23L)).thenReturn(100L);
		when(ledgerFinanceDao.get(1L)).thenReturn(new LedgerFinance());
		rAccountStaUpdateImpl.processTx(datagram);
		//verify(workFlowDao, times(1)).workFlowSave(businessInfo.getId(), null, 1L, "2", null, "1", 100L, LEDGER_FINANCE, 23L);
		//Assert.assertEquals(new Long(100L), f1.getReversedNo());

	}

	@Test
	public void testProcessTx_ledgerFinance_null() {
		Datagram datagram = new Datagram();
		ReverseVo vo = new ReverseVo();
		datagram.setDatagramBody(vo);
		vo.setReverseMessageSequence("324342");
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("messageSequence11");
		datagram.setDatagramHeader(header);

		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);

		WorkFlow f1 = getWorkFlow(LEDGER_FINANCE);

		when(businessInfoDao.findUniqueBy("messageSequence", vo.getReverseMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.findUniqueBy("businessId", 13L)).thenReturn(f1);
		when(sequenceDao.nextWorkFlowNo()).thenReturn(100L);
		when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.workFlowSave(businessInfo.getId(), null, 1L, "2", null, "1", 100l, LEDGER_FINANCE, 23L)).thenReturn(100L);
		when(ledgerFinanceDao.get(1L)).thenReturn(null);
		try {
			rAccountStaUpdateImpl.processTx(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void testProcessTx_ledgerLoan() {
		Datagram datagram = new Datagram();
		ReverseVo vo = new ReverseVo();
		datagram.setDatagramBody(vo);
		vo.setReverseMessageSequence("324342");
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("messageSequence11");
		datagram.setDatagramHeader(header);

		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);
		WorkFlow f1 = getWorkFlow(LEDGER_LOAN);
		when(businessInfoDao.findUniqueBy("messageSequence", vo.getReverseMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.findUniqueBy("businessId", 13L)).thenReturn(f1);
		when(sequenceDao.nextWorkFlowNo()).thenReturn(100L);
		when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.workFlowSave(businessInfo.getId(), null, 1L, "2", null, "1", 100l, LEDGER_LOAN, 23L)).thenReturn(100L);
		when(ledgerLoanDao.get(1L)).thenReturn(new LedgerLoan());
		rAccountStaUpdateImpl.processTx(datagram);
        //verify(workFlowDao, times(1)).workFlowSave(businessInfo.getId(), null, 1L, "2", null, "1", 100L, LEDGER_LOAN, 23L);
		//Assert.assertEquals(new Long(100L), f1.getReversedNo());

	}

	@Test
	public void testProcessTx_ledgerLoan_null() {
		Datagram datagram = new Datagram();
		ReverseVo vo = new ReverseVo();
		datagram.setDatagramBody(vo);
		vo.setReverseMessageSequence("324342");
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("messageSequence11");
		datagram.setDatagramHeader(header);

		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);
		WorkFlow f1 = getWorkFlow(LEDGER_LOAN);
		when(businessInfoDao.findUniqueBy("messageSequence", vo.getReverseMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.findUniqueBy("businessId",13L)).thenReturn(f1);
		when(sequenceDao.nextWorkFlowNo()).thenReturn(100L);
		when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.workFlowSave(businessInfo.getId(), null, 1L, "2", null, "1", 100l, LEDGER_LOAN, 23L)).thenReturn(100L);
		when(ledgerLoanDao.get(1L)).thenReturn(null);
		try {
			rAccountStaUpdateImpl.processTx(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}

	}
	
	@Test
	public void testProcessTx_ledgerLoan_error() {
		Datagram datagram = new Datagram();
		ReverseVo vo = new ReverseVo();
		datagram.setDatagramBody(vo);
		vo.setReverseMessageSequence("324342");
		DatagramHeader header = new DatagramHeader();
		header.setMessageSequence("messageSequence11");
		datagram.setDatagramHeader(header);

		BusinessInfo businessInfo = new BusinessInfo();
		businessInfo.setId(13L);
		businessInfo.setBusinessTypeId(23L);
		WorkFlow f1 = getWorkFlow(Constants.DEBT);
		when(businessInfoDao.findUniqueBy("messageSequence", vo.getReverseMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.findUniqueBy("businessId", businessInfo)).thenReturn(f1);
		when(sequenceDao.nextWorkFlowNo()).thenReturn(100L);
		when(businessInfoDao.findUniqueBy("messageSequence", datagram.getDatagramHeader().getMessageSequence())).thenReturn(businessInfo);
		when(workFlowDao.workFlowSave(businessInfo.getId(), null, 1L, "2", null, "1", 100l, LEDGER_LOAN, 23L)).thenReturn(100L);
		when(ledgerLoanDao.get(1L)).thenReturn(null);
		try {
			rAccountStaUpdateImpl.processTx(datagram);
			Assert.fail();
		} catch (BusinessException e) {
			Assert.assertTrue(true);
		}

	}

	private WorkFlow getWorkFlow(String memo) {
		WorkFlow f1 = new WorkFlow();
		f1.setBusinessTypeId(17L);
		f1.setBusinessId(1L);
		f1.setStartValue("1");
		f1.setEndValue("2");
		f1.setObjectId(1L);
		f1.setMemo(memo);
		f1.setId(24L);
		return f1;
	}

}
