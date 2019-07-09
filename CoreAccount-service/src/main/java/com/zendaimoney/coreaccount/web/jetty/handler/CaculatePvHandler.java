package com.zendaimoney.coreaccount.web.jetty.handler;

import java.io.IOException;
import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.service.LedgerLoanService;

@Named
@Transactional(readOnly = true)
public class CaculatePvHandler extends AbstractHandler {
	@Inject
	private LedgerLoanService ledgerLoanService;

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		BigDecimal pv;
		try {
			pv = ledgerLoanService.pvTest(Long.valueOf(request.getParameter("id")), request.getParameter("date"), Boolean.valueOf(request.getParameter("deducted")));
			response.getWriter().println(pv.toPlainString());
		} catch (Exception e) {
			e.printStackTrace(response.getWriter());
		}
	}

}
