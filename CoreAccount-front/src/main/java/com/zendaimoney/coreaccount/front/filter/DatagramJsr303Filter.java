package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 请求报文转换成VO对象
 * 
 * @author Jianlong Ma
 * 
 */
@Named
public class DatagramJsr303Filter extends DatagramFilter {
	@Inject
	private Validator validator;
	@Override
	public void doFilter(Object datagram) throws BusinessException {
		Datagram dg = (Datagram) CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION);
		Set<ConstraintViolation<Datagram>> constraintViolations = validator.validate(dg);
		if (constraintViolations.size() > 0) {
			StringBuilder message = new StringBuilder();
			for (ConstraintViolation<Datagram> constrainViolation : constraintViolations) {
				message.append(constrainViolation.getPropertyPath().toString() + constrainViolation.getMessage() + "|");
			}
			throw new BusinessException(Constant.REQUEST_STATUS_MESSAGE_ERROR, message.toString());
		}
		if(StringUtils.isBlank(dg.getDatagramBody().getOperator()) || StringUtils.isBlank(dg.getDatagramBody().getOrgan())){
			throw new BusinessException(Constant.REQUEST_STATUS_MESSAGE_ERROR, "datagramBody.operator or datagramBody.organ is blank");
		}
	}
}
