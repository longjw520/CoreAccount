package com.zendaimoney.coreaccount.service.message;

import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.rmi.vo.*;
import com.zendaimoney.coreaccount.service.RepaymentPlanService;
import com.zendaimoney.coreaccount.service.message.mixin.RepaymentPlanMixin;
import com.zendaimoney.coreaccount.util.JsonHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springside.modules.orm.Page;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询还款计划(990006)
 * 
 * @author binliu
 * @update:[2013-11-18] [ShiMing]
 */
@Named
@Transactional(readOnly = true)
public class QueryRepaymentPlanHandler extends MessageHandler {

	@Inject
	private RepaymentPlanService repaymentPlanService;

	private final static Map<Class<?>, Class<?>> queryMap = new HashMap<Class<?>, Class<?>>();
	static {
		queryMap.put(RepaymentPlan.class, RepaymentPlanMixin.class);
	}

	@Override
	public String handle(Datagram datagram) {
		if(datagram.getDatagramBody().getMultiple()) {
			MultipleVoDatagramBody<QueryRepaymentPlanVo> datagramBody = (MultipleVoDatagramBody<QueryRepaymentPlanVo>) datagram.getDatagramBody();
			MultipleVoDatagramBody<QueryResultVo> resultBody = new MultipleVoDatagramBody<QueryResultVo>();
			for (QueryRepaymentPlanVo queryRepaymentPlanVo : datagramBody.getVos()) {
				Page<RepaymentPlan> results = repaymentPlanService.queryBy(queryRepaymentPlanVo);
				QueryResultVo queryResultVo = new QueryResultVo(queryRepaymentPlanVo.getPageNo(), queryRepaymentPlanVo.getPageSize());
				queryResultVo.setResult(results.getResult());
				queryResultVo.setTotalCount(results.getTotalItems());
				resultBody.getVos().add(queryResultVo);
			}
			setResultVO(datagramBody, resultBody);
			datagram.setDatagramBody(resultBody);
		}else {
			QueryRepaymentPlanVo queryRepaymentPlanVo = (QueryRepaymentPlanVo) datagram.getDatagramBody();
			QueryResultVo resultBody = new QueryResultVo(queryRepaymentPlanVo.getPageNo(), queryRepaymentPlanVo.getPageSize());
			Page<RepaymentPlan> results = repaymentPlanService.queryBy(queryRepaymentPlanVo);
			resultBody.setResult(results.getResult());
			resultBody.setTotalCount(results.getTotalItems());
			setResultVO(queryRepaymentPlanVo, resultBody);
			datagram.setDatagramBody(resultBody);
		}
		logger.debug("查询还款计划成功");
		return JsonHelper.toJson(datagram, queryMap);
	}

}
