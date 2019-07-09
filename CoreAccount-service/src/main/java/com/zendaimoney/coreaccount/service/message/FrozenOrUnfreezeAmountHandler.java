package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.FrozenOrUnfreezeAmountVo;
import com.zendaimoney.coreaccount.rmi.vo.MultipleVoDatagramBody;
import com.zendaimoney.coreaccount.service.LedgerService;
import com.zendaimoney.coreaccount.util.JsonHelper;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

/**
 * 冻结解冻现金020002
 * 
 * @author: Wei Chenxi 
 * @update ShiMing
 */
@Named
public class FrozenOrUnfreezeAmountHandler extends MessageHandler {
	@Inject
	private LedgerService ledgerService;

	@Override
	public String handle(Datagram datagram) {
		logger.info("冻结解冻现金 START...");
		if(datagram.getDatagramBody().getMultiple()) {
			MultipleVoDatagramBody<FrozenOrUnfreezeAmountVo> body = (MultipleVoDatagramBody) datagram.getDatagramBody();
			List<FrozenOrUnfreezeAmountVo> failedList = new ArrayList<FrozenOrUnfreezeAmountVo>();
			for (FrozenOrUnfreezeAmountVo vo : body.getVos()) {
				try {
					ledgerService.frozenOrUnfreezeAmount(vo, datagram.getDatagramHeader().getBusinessId());
				} catch (Exception e) {
					failedList.add(vo);
					logger.error("account: [" + vo.getAccount() + "] 冻结/解冻现金失败", e);
				}
			}
			body.setVos(failedList);
		}else {
			FrozenOrUnfreezeAmountVo body = (FrozenOrUnfreezeAmountVo) datagram.getDatagramBody();
			ledgerService.frozenOrUnfreezeAmount(body, datagram.getDatagramHeader().getBusinessId());
		}
		datagram.getDatagramBody().setOperateCode(Constants.PROCESS_STATUS_OK);
		logger.info("冻结解冻现金 END...");
		return JsonHelper.toJson(datagram);
	}
}
