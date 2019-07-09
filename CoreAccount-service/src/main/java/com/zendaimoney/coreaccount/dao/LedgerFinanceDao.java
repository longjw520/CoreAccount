package com.zendaimoney.coreaccount.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.StandardBasicTypes;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.hibernate.HibernateDao;

import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerFinanceVo;

/**
 * 理财分户信息业务处理； 查询投资明细
 * 
 * @author binliu
 * 
 */
@Named
public class LedgerFinanceDao extends HibernateDao<LedgerFinance, Long> {

	/**
	 * 查询投资明细接口
	 * 
	 * @param 查询条件
	 * @return 返回投资明细查询结果
	 */
	@SuppressWarnings("static-access")
	public Page<LedgerFinance> queryBy(QueryLedgerFinanceVo queryLedgerFinanceVo) {

		Criteria criteria = createCriteria();
		PageRequest pageRequest = new PageRequest(queryLedgerFinanceVo.getPageNo().intValue(), queryLedgerFinanceVo.getPageSize().intValue());
		String account = queryLedgerFinanceVo.getAccount();
		String name = queryLedgerFinanceVo.getName();
		String investCustomerName = queryLedgerFinanceVo.getInvestCustomerName();
		Long loanId = queryLedgerFinanceVo.getLoanId();
		/** 借款产品代码--数组参数 */
		String[] productCodeArray = queryLedgerFinanceVo.getProductCodeArray();
		/** 理财分户状态--数组参数 */
		String[] acctStatusArray = queryLedgerFinanceVo.getAcctStatusArray();
		/** 多个投资分账号--数组参数 */
		String[] acctArray = queryLedgerFinanceVo.getAccountArray();
		/** 多个借款编号--数组参数 */
		Long[] loanIdArray = queryLedgerFinanceVo.getLoanIdArray();
		//排除查询的分账号
		String[] excludeAccountArray = queryLedgerFinanceVo.getExcludeAccountArray();
		//债权端口日
		String loanReturnDate = queryLedgerFinanceVo.getLoanReturnDate();

		criteria.createAlias("ledger", "d");
		if (StringUtils.isNotBlank(account)) {
			criteria.add(Restrictions.eq("d.account", account));
		}
		
		if(acctArray != null){
			int acctArrayLength=acctArray.length;
			if(acctArrayLength<1000){
				criteria.add(Restrictions.in("d.account", acctArray));
			}else{
				//acctArray里面的元素超过1000个（含）时
				Disjunction dis=Restrictions.disjunction();
				for(int i=0;i<acctArrayLength;i++){
					if(this.isIntResult(i)){
						//每1k条作为一个in查询条件 最后以or连接
						dis.add(Restrictions.in("d.account", this.removeNullObject(Arrays.copyOfRange(acctArray, i, i+1000))));//from:从哪个下标开始取值  to:下标+要取的元素个数
					}
				}
				criteria.add(dis);
			}
		}
		//
		if(excludeAccountArray != null && excludeAccountArray.length > 0){
			int excludeAccountLength=excludeAccountArray.length;
			if(excludeAccountLength<1000){
				criteria.add(Restrictions.not(Restrictions.in("d.account", excludeAccountArray)));
			}else{
				//acctArray里面的元素超过1000个（含）时
				Disjunction dis=Restrictions.disjunction();
				for(int i=0;i<excludeAccountLength;i++){
					if(this.isIntResult(i)){
						//每1k条作为一个in查询条件 最后以or连接
						dis.add(Restrictions.not(Restrictions.in("d.account", Arrays.copyOfRange(excludeAccountArray, i, i+1000))));//from:从哪个下标开始取值  to:下标+要取的元素个数
					}
				}
				criteria.add(dis);
			}
		}

		criteria.createAlias("ledgerLoan", "a");
		if (loanId != null) {
			criteria.add(Restrictions.eq("a.id", loanId));
		}
		if(loanIdArray != null && loanIdArray.length > 0){
			int loanIdArrayLength=loanIdArray.length;
			if(loanIdArrayLength<1000){
				criteria.add(Restrictions.in("a.id", loanIdArray));
			}else{
				//acctArray里面的元素超过1000个（含）时
				Disjunction dis=Restrictions.disjunction();
				for(int i=0;i<loanIdArrayLength;i++){
					if(this.isIntResult(i)){
						//每1k条作为一个in查询条件 最后以or连接
						dis.add(Restrictions.in("a.id", Arrays.copyOfRange(loanIdArray, i, i+1000)));//from:从哪个下标开始取值  to:下标+要取的元素个数
					}
				}
				criteria.add(dis);
			}
		}
		if (null != productCodeArray && productCodeArray.length > 0) {
			criteria.add(Restrictions.in("a.productCode", productCodeArray));
		}
		if (null != acctStatusArray && acctStatusArray.length > 0) {
			criteria.add(Restrictions.in("acctStatus", acctStatusArray));
		}
		//债权端口日查询
		if(StringUtils.isNotBlank(loanReturnDate)){
			criteria.add(Restrictions.sqlRestriction("to_char(a2_.contract_end,'dd') = '" + loanReturnDate + "'"));
		}

		criteria.createAlias("a.ledger", "b");

		criteria.createAlias("b.customer", "c");
		
		if (StringUtils.isNotBlank(name)) {
			criteria.add(Restrictions.eq("c.name", name));
		}
		if (StringUtils.isNotBlank(investCustomerName)) {
			criteria.createAlias("d.customer", "f");
			criteria.add(Restrictions.like("f.name", "%"+investCustomerName+"%"));
		}
		System.out.println(criteria.toString());
		return findPage(pageRequest, criteria);
	}

	/**
	 * @author     ym10093 fanqp
	 * @createTime 2016年9月8日 下午4:11:37
	 * @description 遍历数组时当下标为1000的倍数时，返回true(每1000条数据处理一批)
	 */
	public boolean isIntResult(int i){
		boolean flag=false;
		BigDecimal b=new BigDecimal(i).divide(new BigDecimal(1000),2,BigDecimal.ROUND_CEILING);
		String s=String.valueOf(b);
		if(s.indexOf(".00")>=0){
			flag=true;
		}
		return flag;
	}
	
	/**
	 * @author     ym10093 fanqp
	 * @createTime 2016年9月8日 下午4:11:34
	 * @description 移除数组里为null的元素
	 */
	public static String[] removeNullObject(String[] array){
		List<String> list=Arrays.asList(array);
		List<String> newlist=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			if(list.get(i)==null){
				break;
			}
			newlist.add(list.get(i));
		}
		return (String[]) newlist.toArray(new String[newlist.size()]);
	}
	
	public static void main(String[] args) {
		String[] s=new String[]{"0","1","2","3","4"};
		String news=Arrays.toString(s);
		System.out.println(news);
		String[] newss=Arrays.copyOfRange(s, 0, 1000);//对s数组从下标为from的开始取，一共取（下标+要取得元素个数）to个值
		String[] newss2=removeNullObject(newss);
		for(int i=0;i<newss2.length;i++){
			System.out.println(newss2[i]);
		}
	}
	/**
	 * 根据账号和状态查找理财分户信息
	 * 
	 * @param status
	 * @param account
	 * @return
	 */
	public List<LedgerFinance> queryBy(String[] status, String account) {
		Criteria criteria = createCriteria();
		if (status != null && status.length > 0) {
			criteria.add(Restrictions.in("acctStatus", status));
		}
		if (account != null) {
			criteria.createAlias("ledger", "a");
			criteria.add(Restrictions.eq("a.account", account));
		}
		return find(criteria);
	}
	
	//TODO
	/**
	 * 根据债权编号和买方分账号查询买方对改债权的理财信息，按持有债权本金降序排列
	 * @param ledgerId
	 * @param loanId
	 * @return
	 */
	public List<LedgerFinance> queryBuyFinances(Long ledgerId, Long loanId, String[] status){
		Criteria criteria = createCriteria();
		if(ledgerId!=null){
			criteria.createAlias("ledger", "l");
			criteria.add(Restrictions.eq("l.id", ledgerId));
		}
		if(loanId!=null){
			criteria.createAlias("ledgerLoan", "n");
			criteria.add(Restrictions.eq("n.id", loanId));
		}
		if (null != status && status.length > 0) {
			criteria.add(Restrictions.in("acctStatus", status));
		}
		criteria.addOrder(Order.desc("debtAmount"));
		return find(criteria);
	}
	
	/**
	 * 债权有效性校验sql查询
	 */
	public List<Object> queryForLoanJudger(){
		String sql = " select financeid from "
					+ " (select lf.id as financeid, (nvl(lf.debt_proportion,0)+nvl(lf.frozen_porportion,0))*ll.amount_spare as temppv "
					+ "		from ac_t_ledger_finance lf "
					+ "		left join ac_t_ledger_loan ll on lf.loan_id = ll.id "
					+ "		where lf.acct_status in (1,3) ) "
					+ "	where temppv < 0.01"
					+ " union all "
					+ " select lf.id from ac_t_ledger_finance lf "
					+ "		left join ac_t_ledger_loan ll on lf.loan_id = ll.id "
					+ "		where ll.acct_status =2 and lf.acct_status in (1,3)";
		Query query = this.getSession().createSQLQuery(sql).addScalar("financeid", StandardBasicTypes.LONG);
		List<Object> result = query.list();
		return result;
	}
	
}
