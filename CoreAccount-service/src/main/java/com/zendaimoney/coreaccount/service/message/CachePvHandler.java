package com.zendaimoney.coreaccount.service.message;

import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_OK;

import javax.inject.Inject;
import javax.inject.Named;

import com.zendaimoney.coreaccount.data.task.DataLoader;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.util.JsonHelper;

/**
 * 手动触发redis缓存PV操作
 * 
 * @author larry
 * 
 */
@Named
public class CachePvHandler extends MessageHandler {

	@Inject
	private DataLoader dataLoader;

	@Override
	public String handle(Datagram datagram) {
		dataLoader.load();
		datagram.getDatagramBody().setOperateCode(PROCESS_STATUS_OK);
		return JsonHelper.toJson(datagram);
	}

}
