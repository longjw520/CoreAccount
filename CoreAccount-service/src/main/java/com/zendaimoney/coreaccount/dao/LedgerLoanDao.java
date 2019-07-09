package com.zendaimoney.coreaccount.dao;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.LoanHouseholdVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.util.ArrayUtil;
import com.zendaimoney.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.StandardBasicTypes;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PageRequest;
import org.springside.modules.orm.hibernate.HibernateDao;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 贷款分户业务
 * 
 * @author liubin
 * @since 1.0
 */
@Named
public class LedgerLoanDao extends HibernateDao<LedgerLoan, Long> {

	@Inject
	private DataSource dataSource;

	/**
	 * 查询债券接口
	 * 
	 * @param queryLedgerLoanVo 查询条件
	 * @return 返回债权查询结果
	 */
	public Page<LedgerLoan> queryBy(QueryLedgerLoanVo queryLedgerLoanVo) {

		Criteria criteria = createCriteria();
		PageRequest pageRequest = new PageRequest(queryLedgerLoanVo.getPageNo().intValue(), queryLedgerLoanVo.getPageSize().intValue());
		if (queryLedgerLoanVo.getId() != null) {
			if (queryLedgerLoanVo.getIsIdIndistinct() != null && queryLedgerLoanVo.getIsIdIndistinct().booleanValue())
				criteria.add(Restrictions.eq("id", queryLedgerLoanVo.getId()));
			else
				criteria.add(Restrictions.sqlRestriction("lower(this_.ID) like '%" + String.valueOf(queryLedgerLoanVo.getId()).toLowerCase() + "%'"));
		}
		
		Long[] idArray = queryLedgerLoanVo.getIdArray();
		if(null != idArray && idArray.length > 0)
			criteria.add(Restrictions.in("id", idArray));
		
		if (queryLedgerLoanVo.getMinRate() != null)
			criteria.add(Restrictions.ge("rateSpare", queryLedgerLoanVo.getMinRate()));
		if (queryLedgerLoanVo.getMaxRate() != null)
			criteria.add(Restrictions.le("rateSpare", queryLedgerLoanVo.getMaxRate()));
		// ---start---fix bug CA-135---by Jianlong Ma
		/** 多个借款产品--数组参数 */
		String[] productCodeArray = queryLedgerLoanVo.getProductCodeArray();
		if (null != productCodeArray && productCodeArray.length > 0)
			criteria.add(Restrictions.in("productCode", productCodeArray));

		/** 理财分户状态--数组参数 */
		String[] acctStatusArray = queryLedgerLoanVo.getAcctStatusArray();
		if (null != acctStatusArray && acctStatusArray.length > 0)
			criteria.add(Restrictions.in("acctStatus", acctStatusArray));
		// -----end---fix bug CA-135-----
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMinImportDate())) {
			Date minDate = DateUtils.nullSafeParseDate(queryLedgerLoanVo.getMinImportDate() + " 00:00:00", Constants.DATE_TIME_FORMAT);
			criteria.add(Restrictions.ge("dateSpare", minDate));
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMaxImportDate())) {
			Date maxDate = DateUtils.nullSafeParseDate(queryLedgerLoanVo.getMaxImportDate() + " 23:59:59", Constants.DATE_TIME_FORMAT);
			criteria.add(Restrictions.le("dateSpare", maxDate));
		}
		//债权到期日
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMinEndDate())) {
			Date minDate = DateUtils.nullSafeParseDate(queryLedgerLoanVo.getMinEndDate() + " 00:00:00", Constants.DATE_TIME_FORMAT);
			criteria.add(Restrictions.ge("contractEnd", minDate));
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMaxEndDate())) {
			Date maxDate = DateUtils.nullSafeParseDate(queryLedgerLoanVo.getMaxEndDate() + " 23:59:59", Constants.DATE_TIME_FORMAT);
			criteria.add(Restrictions.le("contractEnd", maxDate));
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getName())) {
			criteria.createAlias("ledger", "b");
			criteria.createAlias("b.customer", "c");
			if (queryLedgerLoanVo.getIsNameIndistinct() != null && queryLedgerLoanVo.getIsNameIndistinct().booleanValue())
				criteria.add(Restrictions.eq("c.name", queryLedgerLoanVo.getName()));
			else
				criteria.add(Restrictions.ilike("c.name", queryLedgerLoanVo.getName(), MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getNextExpiry())) {
			Date minDate = DateUtils.nullSafeParseDate(queryLedgerLoanVo.getNextExpiry() + " 00:00:00", Constants.DATE_TIME_FORMAT);
			criteria.add(Restrictions.ge("nextExpiry", minDate));
			criteria.add(Restrictions.lt("nextExpiry", DateUtils.addDays(minDate,1)));
		}
		if(StringUtils.isNotBlank(queryLedgerLoanVo.getReturnDate())){
			criteria.add(Restrictions.sqlRestriction("to_char(this_.next_expiry,'dd') =" + queryLedgerLoanVo.getReturnDate()));
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getRemark())) {
			criteria.add(Restrictions.eq("remark", queryLedgerLoanVo.getRemark()));
		}
		//主债权ID
		if (queryLedgerLoanVo.getFatherLoanId() != null) {
            criteria.add(Restrictions.eq("fatherLoanId", queryLedgerLoanVo.getFatherLoanId()));
        }
		return findPage(pageRequest, criteria);
	}

	/** 计息--查找有效债权 */
	public List<LedgerLoan> getLedgerLoans(String... acctStatus) {
		Criteria criteria = createCriteria();
		criteria.createAlias("ledgerFinances", "lf", JoinType.LEFT_OUTER_JOIN);
		if (null != acctStatus && acctStatus.length > 0) {
			criteria.add(Restrictions.in("acctStatus", acctStatus));
		}
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return find(criteria);
	}

	/**
	 * 查询债权ID列表根据状态
	 * 
	 * @param status
	 *            (债权状态)
	 * @return
	 */
	/**
	 * @param status
	 * @return
	 */
	public Object[] getAllLoanIdByStatus(String... status) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			String sql = "select ID,RATE from AC_T_LEDGER_LOAN where ACCT_STATUS in(";
			for (int i = 0; i < status.length; i++) {
				sql = sql.concat("'").concat(status[i]).concat("'");
				if (i < status.length - 1) {
					sql = sql.concat(",");
				}
			}
			sql = sql.concat(")");
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			int size = 0;
			if (rs.next()) {
				rs.last();
				size = rs.getRow();// 获取总记录数
			}
			Object[] result = new Object[size << 1];
			if (size == 0)
				return result;
			rs.beforeFirst();
			for (int n = 0; rs.next();) {
				result[n] = rs.getLong(1);
				result[++n] = rs.getBigDecimal(2);
				n++;
			}
			return result;
		} catch (SQLException e) {
			return new Object[0];
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * 查询所有的债权的Id(不包括2)
	 * 
	 * @return
	 */
	public List<Object[]> getPVKeys(QueryLedgerLoanVo queryLedgerLoanVo) {
		String sql = "select t.ID , t.RATE , t.INTEREST_START from %s where %s";
		if (queryLedgerLoanVo.getId() != null) {
			if (queryLedgerLoanVo.getIsIdIndistinct() != null && queryLedgerLoanVo.getIsIdIndistinct().booleanValue()) {
				sql += " and t.ID=" + queryLedgerLoanVo.getId();
			} else {
				sql += " and lower(t.ID) like '%%" + String.valueOf(queryLedgerLoanVo.getId()).toLowerCase() + "%%'";
			}
		}
		if (queryLedgerLoanVo.getMinRate() != null) {
			sql += " and t.RATE_SPARE >=" + queryLedgerLoanVo.getMinRate();
		}
		if (queryLedgerLoanVo.getMaxRate() != null) {
			sql += " and t.RATE_SPARE <=" + queryLedgerLoanVo.getMaxRate();
		}
		if(queryLedgerLoanVo.getReturnDate() != null){
			sql += " and to_char(t.next_expiry, 'dd') =" + queryLedgerLoanVo.getReturnDate();
		}
		String[] productCodeArray = queryLedgerLoanVo.getProductCodeArray();
		if (null != productCodeArray && productCodeArray.length > 0) {
			sql += " and t.PRODUCT_CODE in(";
			for (int idx = 0; idx < productCodeArray.length; idx++) {
				sql += "'" + productCodeArray[idx] + "'";
				if (idx < productCodeArray.length - 1) {
					sql += ',';
				}
			}
			sql += ')';
		}
		String[] acctStatusArray = queryLedgerLoanVo.getAcctStatusArray();
		if (acctStatusArray != null && acctStatusArray.length > 0) {
			acctStatusArray = ArrayUtil.removeAll(acctStatusArray, new String[] { "2", "6" });
			sql += " and t.ACCT_STATUS in(";
			for (int idx = 0; idx < acctStatusArray.length; idx++) {
				sql += "'" + acctStatusArray[idx] + "'";
				if (idx < acctStatusArray.length - 1) {
					sql += ',';
				}
			}
			sql += ')';
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMinImportDate())) {
			sql += " and t.DATE_SPARE >= to_date('" + queryLedgerLoanVo.getMinImportDate() + " 00:00:00','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMaxImportDate())) {
			sql += " and t.DATE_SPARE <= to_date('" + queryLedgerLoanVo.getMaxImportDate() + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMinEndDate())) {
			sql += " and t.contract_end >= to_date('" + queryLedgerLoanVo.getMinEndDate() + " 00:00:00','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getMaxEndDate())) {
			sql += " and t.contract_end <= to_date('" + queryLedgerLoanVo.getMaxEndDate() + " 23:59:59','yyyy-mm-dd hh24:mi:ss')";
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getName())) {
			if (queryLedgerLoanVo.getIsNameIndistinct() != null && queryLedgerLoanVo.getIsNameIndistinct().booleanValue()) {
				sql += " and t2.NAME='" + queryLedgerLoanVo.getName() + "'";
			} else {
				sql += " and lower(t2.NAME) like '%%" + queryLedgerLoanVo.getName().toLowerCase() + "%%'";
			}
			sql = String.format(sql, "AC_T_LEDGER_LOAN t,AC_T_LEDGER t1, AC_T_CUSTOMER t2", "t.LEDGER_ID=t1.ID and t1.TOTAL_ACCOUNT_ID=t2.ID");
		} else {
			sql = String.format(sql, "AC_T_LEDGER_LOAN t", "1=1");
		}
		if (StringUtils.isNotBlank(queryLedgerLoanVo.getRemark())) {
			sql += " and t.REMARK='" + queryLedgerLoanVo.getRemark() + "'";
		}
		sql += " and t.ACCT_STATUS not in(?,?)";
		logger.debug(sql);
		return this.createSQLQuery(sql, "2", "6").list();
	}
	
	/**
	 * 2-->外部债权
	 * @param loanHouseholdVo
	 * @return
	 */
	public boolean exist(LoanHouseholdVo loanHouseholdVo) {
		return null != createCriteria(Restrictions.eq("remark", loanHouseholdVo.getRemark())).add(Restrictions.eq("productCode", loanHouseholdVo.getProductCode())).uniqueResult();
	}
	
	public List<Object> queryForLoanJudgeTimer(){
		StringBuffer sb = new StringBuffer("");
		sb.append(" select ll.id as loanid from ac_t_ledger_loan ll "
				+ "		where ll.acct_status in (3,4) "
				+ "		and not exists "
				+ "			(select 1 from ac_t_ledger_finance lf "
				+ "				where lf.acct_status not in (2,6)"
				+ "				and lf.loan_id=ll.id ) ");
		
		Query query = this.getSession().createSQLQuery(sb.toString()).addScalar("loanid", StandardBasicTypes.LONG);
		List<Object> list = query.list();
		return list;
	}

}
