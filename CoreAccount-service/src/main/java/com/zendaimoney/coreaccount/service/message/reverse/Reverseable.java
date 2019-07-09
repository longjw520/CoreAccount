package com.zendaimoney.coreaccount.service.message.reverse;

import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.exception.BusinessException;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_OK;

/**
 * 冲正业务接口
 * 
 * @author larry
 * 
 */
@Transactional
public abstract class Reverseable {

	protected Logger logger = Logger.getLogger(getClass());

	/** 冲正业务处理实现 */
	protected void processBusiness(Datagram datagram) {
	}

	/** 冲正事务相关处理实现 */
	protected void processTx(Datagram datagram) {
	}

	public String process(Datagram datagram) throws BusinessException {
		processBusiness(datagram);
		processTx(datagram);
		datagram.getDatagramBody().setOperateCode(PROCESS_STATUS_OK);
		logger.info("针对请求[" + datagram.getDatagramHeader().getMessageSequence() + "]的冲正完成");
		return JsonHelper.toJson(datagram);
	}
}
