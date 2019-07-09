package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.email.SimpleMailService;
import com.zendaimoney.coreaccount.rmi.vo.Datagram;
import com.zendaimoney.coreaccount.service.CalculateAccrualService;
import com.zendaimoney.coreaccount.service.PrepareDataService;
import com.zendaimoney.coreaccount.task.SendEmailTask;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.coreaccount.util.SystemUtil;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 每日计息-- 000003
 * 
 * @author Jianlong Ma
 */
@Named
@Transactional
public class CalculateAccrualHandler extends MessageHandler {
    @Inject
	ThreadPoolTaskExecutor taskExecutor;
	@Inject
	private SimpleMailService simpleMailService;
    @Inject
    private PrepareDataService service;
    @Inject
    private  CalculateAccrualService calculateAccrualService;

	@Override
	public String handle(Datagram datagram) {

		try {
            service.calculateInterest(datagram.getDatagramHeader().getBusinessId());
            //calculateAccrualService.calculateInterest(datagram.getDatagramHeader().getBusinessId());
		} catch (Exception e) {
			e.printStackTrace();
			String emailSubject = PropertiesReader.readAsString("calculate.accrual.error");
			String errorCode = Constants.PROCESS_STATUS_FAIL;
			String errorInfo = StringUtils.isNotBlank(ExceptionUtils.getStackTrace(e)) ? ExceptionUtils.getStackTrace(e) : "";
			simpleMailService.sendMail(emailSubject + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n message from " + SystemUtil.getIpAddr(), null, emailSubject + "\n" + errorInfo);
			logger.info("计息批处理失败！");
			throw new BusinessException(errorCode, emailSubject);
		}

		datagram.getDatagramBody().setOperateCode(Constants.PROCESS_STATUS_OK);
		logger.info("计息批处理成功！");

		taskExecutor.execute(new SendEmailTask(PropertiesReader.readAsString("calculate.accrual.ok") + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n message from " + SystemUtil.getIpAddr(), null, "计息计算成功！"));
		return JsonHelper.toJson(datagram);
	}

}
