package com.zendaimoney.coreaccount.service.jms;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.rmi.utils.Json;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.BusinessInfoService;
import com.zendaimoney.coreaccount.service.MessageService;
import com.zendaimoney.coreaccount.service.message.MessageHandler;
import com.zendaimoney.coreaccount.service.message.MessageHelper;
import com.zendaimoney.coreaccount.task.SendEmailTask;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.coreaccount.util.SystemUtil;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Named
public class MessageReceiver implements MessageListener {
    private Logger logger = Logger.getLogger(getClass());

    @Inject
    private MessageService messageService;
    @Inject
    private JmsService jmsService;
    @Inject
    private BusinessInfoService businessInfoService;
    @Resource(name = "businessImpls")
    private Map<String, ? extends MessageHandler> messageHandlers;

    @Inject
    private Json json;
    @Inject
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void onMessage(Message message) {
        try {
            final TextMessage textMessage = (TextMessage) message;
            String messageCode = textMessage.getStringProperty("messageCode");
            if (messageCode == null)
                messageCode = MessageHelper.extractMessageCode(textMessage.getText());
            Datagram datagram = json.toBean(textMessage.getText());
            logger.debug("------------3：核心接收请求队列" + datagram.getDatagramHeader().getMessageSequence() + "请求内容");
            messageService.writeMessageLog(datagram, textMessage.getText());
            businessInfoService.saveBusinessInfo(datagram);
            // 任务分发
            String handleResult;
            try {
                handleResult = messageHandlers.get(messageCode).handle(datagram);
                if(messageCode.equals(Constants.MESSAGE_BUSINESS_TYPE_CALCULATE_ACCRUAL)){
                    logger.info("计息事务提交完成。");
                    taskExecutor.execute(new SendEmailTask(PropertiesReader.readAsString("calculate.interest.finish") + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n message from " + SystemUtil.getIpAddr(), null, "计息事务提交完成。"));
                }
            } catch (Exception e) {
                /** 诸如DB崩溃、服务异常等不可预知的错误情况 */
                logger.error(e.getMessage(), e);
                String operateCode = Constants.PROCESS_STATUS_FAIL;
                if (e instanceof BusinessException) {
                    operateCode = ((BusinessException) e).getCode();
                }
                datagram.getDatagramBody().setOperateCode(operateCode);
                datagram.getDatagramBody().setMemo(e.getMessage());
                handleResult = JsonHelper.toJson(datagram);
            }
            handleResult = handleResult.replaceFirst("\"operateTime\":(\".*?\"|null)", "\"operateTime\":\"" + DateFormatUtils.format(new Date(), Constants.DATE_TIME_FORMAT) + "\"");
            final String result = handleResult;
            String destination = com.zendaimoney.coreaccount.util.Datagram.ANALYZER.getDest(messageCode);
            logger.debug("------------4：核心开始发送处理结果到处理队列" + datagram.getDatagramHeader().getMessageSequence());
            jmsService.send(destination, result, textMessage.getJMSCorrelationID());
            logger.debug("------------5：核心发送结果结束" + datagram.getDatagramHeader().getMessageSequence());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
