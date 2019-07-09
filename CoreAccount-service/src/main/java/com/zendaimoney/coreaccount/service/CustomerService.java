package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.CustomerDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.entity.Customer;
import com.zendaimoney.coreaccount.rmi.vo.CustomerVO;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.util.ObjectHelper;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.Date;

/**
 * 客户开户
 * 
 * @author binliu
 * 
 */
@Named
@Transactional
public class CustomerService {

	@Inject
	private CustomerDao customerDao;

	@Inject
	private SequenceDao sequenceDao;

	/**
	 * 生成总账号(8位营业机构+8位流水号)
	 * 
	 * @return 总账号
	 */
	@Transactional(readOnly = true)
	public String genMainAccount(String openacctOrgan) {
		return openacctOrgan + StringUtils.leftPad(String.valueOf(Long.parseLong(customerDao.getMaxFlowBy(openacctOrgan)) + 1), 8, '0');
	}

	/**
	 * 根据证件编号和证件类型获取客户信息
	 * 
	 * @param cardId
	 *            (证件号码)
	 * @param cardType
	 *            (证件类型)
	 * @return
	 */
	@Transactional(readOnly = true)
	public Customer getCustomerBy(String cardId, String cardType) {
		return customerDao.getCustomer(cardId, cardType);
	}

	/**
	 * 处理开户业务(入口)
	 * 
	 * @param customer
	 */
	public String openAccount(Datagram datagram) {
		CustomerVO customerVO = (CustomerVO) datagram.getDatagramBody();
		Customer customer = null;
		try {
			ValidateUtil.validateCustomer(customer = getCustomerBy(customerVO.getCardId(), customerVO.getCardType()));
		} catch (BusinessException e) {// 用户已经开户
			customerVO.setTotalAcct(customer.getTotalAcct());
			throw e;
		}
		customerVO.setTotalAcct(genMainAccount(customerVO.getOrgan()));
		customerVO.setCustomerNo(sequenceDao.nextCustomerNo());
		customer = new Customer();
		ObjectHelper.copy(customerVO, customer);
		customer.setOpenacctDate(new Date());
		customerDao.save(customer);
		customerVO.setOperateTime(DateFormatUtils.format(Calendar.getInstance(), Constants.DATE_FORMAT));
		customerVO.setOperateCode(Constants.PROCESS_STATUS_OK);
		customerVO.setMemo(null);
		return JsonHelper.toJson(datagram);
	}

}
