package com.zendaimoney.coreaccount.front.filter;

import com.zendaimoney.coreaccount.front.entity.MessageContent;
import com.zendaimoney.coreaccount.front.entity.MessageLog;
import com.zendaimoney.coreaccount.front.service.MessageContentService;
import com.zendaimoney.coreaccount.front.service.MessageInfoService;
import com.zendaimoney.coreaccount.front.service.MessageLogService;
import com.zendaimoney.coreaccount.front.session.CoreAccountFrontSession;
import com.zendaimoney.coreaccount.front.vo.Constant;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.rmi.vo.DatagramBody;
import com.zendaimoney.coreaccount.rmi.vo.DatagramHeader;
import com.zendaimoney.exception.BusinessException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * 请求报文存入DB，log信息存入DB
 * 
 * @author Jianlong Ma
 * 
 */
@Named
public class DatagramSaveFilter extends DatagramFilter {

	@Inject
	private MessageLogService messageLogService;
	@Inject
	private MessageContentService messageContentService;

	@Inject
	private MessageInfoService messageInfoService;
	@Inject
	private ExecutorService threadPool;

	@Override
	public void doFilter(Object datagram) throws BusinessException {
		Datagram dg = (Datagram) CoreAccountFrontSession.get(Constant.DATAGRAM_NAME_IN_SESSION);
		DatagramHeader header = dg.getDatagramHeader();
		DatagramBody body = dg.getDatagramBody();
		/** 把报文转换成VO之后的log存入Front的FB_T_MESSAGE_LOG表中，此处REQUEST_STATUS=000 */
		MessageLog messageLog = new MessageLog();
		messageLog.setMessageSequence(header.getMessageSequence());
		/** REQUEST_STATUS=000 初始状态 */
		messageLog.setRequestStatus(Constant.REQUEST_INITIAL_STATUS);
		messageLog.setRequestDate(new Date());
		messageLog.setMessageTypeId(messageInfoService.getMessageInfo(header.getMessageCode()).getId());
		messageLog.setMemo(body.getMemo());

		messageLog.setRequestName(header.getUserName());
		messageLog.setRequestPwd(header.getPassword());
		/** 请求操作人 */
		messageLog.setRequestOperator(body.getOperator());
		/** 请求IP */
		messageLog.setRequestIp(CoreAccountFrontSession.get(Constant.CLIENT_HOST_IN_SESSION).toString());
		/** 请求系统 */
		messageLog.setRequestSystem(header.getSenderSystemCode());
		/** 接收线程 */
		messageLog.setReceiveThread("" + Thread.currentThread().getId());
		messageLog.setCallbackThread("CallbackThread");
		/** 回调时间callbackDate */
		messageLog.setCallbackDate(new Date());

		messageLogService.saveMessageLog(messageLog);

		/** 把报文转换成VO之后的entity存入Front的FB_T_MESSAGE_CONTENT表中 */
		final MessageContent messageContent = new MessageContent();
		messageContent.setId(messageLog.getId());
		/** 接受报文内容 */
		messageContent.setReceiverContent(datagram.toString());
		CoreAccountFrontSession.put(Constant.DATAGRAM_VO_ENTITY_IN_SESSION, messageContent);
		CoreAccountFrontSession.put(Constant.DATAGRAM_VO_LOG_IN_SESSION, messageLog);
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				messageContentService.saveMessageContent(messageContent);
			}
		});
	}
}