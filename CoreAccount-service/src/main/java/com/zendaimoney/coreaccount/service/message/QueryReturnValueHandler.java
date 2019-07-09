package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.QueryReturnValueVo;
import com.zendaimoney.coreaccount.rmi.vo.ReturnValueVo;
import com.zendaimoney.coreaccount.service.QueryReturnService;
import com.zendaimoney.coreaccount.util.JsonHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * 查询回款金额和资产价值(990007)
 * 
 * @author larry
 * 
 */
@Named
@Transactional(readOnly = true)
public class QueryReturnValueHandler extends MessageHandler {

	@Inject
	private QueryReturnService queryReturnService;

	@Override
	public String handle(Datagram datagram) {
		QueryReturnValueVo queryReturnValue = (QueryReturnValueVo) datagram.getDatagramBody();
		ReturnValueVo returnValueVo = queryReturnService.getReturnValue(queryReturnValue.getAccount(), queryReturnValue.getRepayDay());
		setResultVO(queryReturnValue, returnValueVo);
		datagram.setDatagramBody(returnValueVo);
		logger.debug("查询回款金额和资产价值成功");
		return JsonHelper.toJson(datagram);
	}
}
