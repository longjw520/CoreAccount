package com.zendaimoney.coreaccount.service.message;

import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_OK;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.RepaymentVo;
import com.zendaimoney.coreaccount.service.RepaymentPlanService;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 回款(020041)
 * 
 * @author binliu
 * 
 */
@Named
@Transactional
public class RepaymentHandler extends MessageHandler {
	@Inject
	private RepaymentPlanService repaymentPlanService;

	@Override
	public String handle(Datagram datagram) {
		RepaymentVo repaymentVo = (RepaymentVo) datagram.getDatagramBody();
		repaymentPlanService.repayment(repaymentVo, datagram.getDatagramHeader().getBusinessId());
		repaymentVo.setOperateCode(PROCESS_STATUS_OK);
		logger.info("回款成功！！！");
		return JsonHelper.toJson(datagram);
	}

}
